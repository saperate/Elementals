package dev.saperate.elementals.data;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.advancements.HasElementCriterion;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dev.saperate.elementals.effects.BurnoutStatusEffect.BURNOUT_EFFECT;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.network.ModMessages.*;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

public class Bender {
    public static final float CHI_REGENERATION_RATE = 0.1f;//this is per tick (1/20 of a second)
    public static Map<UUID, Bender> benders = new HashMap<>();
    public PlayerEntity player; //TODO change this to ServerPlayerEntity
    public PlayerData plrData;
    public final ConcurrentHashMap<Ability, Object> backgroundAbilities = new ConcurrentHashMap<>();
    @Nullable
    public Long castTime;
    @Nullable
    public Ability currAbility;
    @Nullable
    public Object abilityData;


    public Bender(PlayerEntity player) {
        this.player = player;
        benders.put(player.getUuid(), this);
        plrData = getData();
    }

    public void bindAbility(Ability ability, int index) {
        if ((plrData.elements.get(plrData.activeElementIndex).contains(ability) || ability == null)
                && index >= 0 && index <= 4) {
            plrData.boundAbilities[index] = ability;
        }
    }


    public void clearBindings() {
        plrData.boundAbilities = new Ability[4];
    }

    public void bend(int index, boolean isStart) {
        if (index >= 0 && index < 5 && plrData.boundAbilities[index] != null) {
            if (currAbility == null && isStart) {
                castTime = System.currentTimeMillis();
                setCurrAbility(plrData.boundAbilities[index]);
                abilityData = null;
                return;
            }
            if (currAbility != null && castTime != null && !isStart) {
                currAbility.onCall(this, System.currentTimeMillis() - castTime);
                castTime = null;
            }
        }
    }

    public static Bender getBender(ServerPlayerEntity player) {
        if(benders.containsKey(player.getUuid())){
            return benders.get(player.getUuid());
        }
        return new Bender(player);
    }


    public void tick() {
        plrData.chi = Math.min(100,
                plrData.chi + (Bender.CHI_REGENERATION_RATE
                        * (safeHasStatusEffect(OVERCHARGED_EFFECT, player) ? 4 : 1)
                        * (safeHasStatusEffect(BURNOUT_EFFECT, player) ? 0.25f : 1)
                ));

        backgroundAbilities.forEach((Ability ability, Object data) -> ability.onBackgroundTick(this, data));

        if(player.age % 20 == 0 && currAbility != null && currAbility.shouldImmobilizePlayer(player)){
            player.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT,20,0,false,false,true));
        }

        Elementals.HAS_ELEMENT.trigger((ServerPlayerEntity) player);
    }

    public void setCurrAbility(Ability ability) {
        if (ability == null) {
            castTime = null;
        }
        this.currAbility = ability;
        syncAbility(this);
    }

    public void setCurrAbility(int i) {
        setCurrAbility(plrData.boundAbilities[i]);
        syncAbility(this);
    }

    /**
     * Adds an ability to be ticked in the background, if the ability was already there, the new data overrides the old.
     * @param ability The ability that will tick in the background
     * @param data Any data needed for it to work or null
     */
    public void addBackgroundAbility(Ability ability, Object data){
        backgroundAbilities.put(ability,data);
    }

    public void setBackgroundAbilityData(Ability ability, Object data){
        backgroundAbilities.put(ability,data);
    }

    /**
     * Gets the ability data of a specific background ability.
     * This should be used carefully since it returns an Object with unknown contents.
     * @param ability The ability whose data we want
     * @return The data in the form of an Object
     */
    public Object getBackgroundAbilityData(Ability ability){
        return backgroundAbilities.get(ability);
    }

    /**
     * Checks if an ability is already in the background for safer use of addBackgroundAbility.
     * @param ability The ability we want to check
     * @return Whether the ability is already in the background or not
     */
    public boolean isAbilityInBackground(Ability ability){
        return backgroundAbilities.containsKey(ability);
    }

    /**
     * Stops an ability from being ticked in the background further
     * @param ability The ability we want to remove
     */
    public void removeAbilityFromBackground(Ability ability){
        if(isAbilityInBackground(ability)){
            backgroundAbilities.remove(ability);
        }
    }

    /**
     * Sets the active element to the one passed,
     * this does not work if the bender doesn't have the element,
     * consider checking if they have it with hasElement and then adding it with addElement
     * @param element The element we want to be active
     * @param sync Whether we send the client this change
     */
    public void setElement(Element element, boolean sync) {
        if (!hasElement(element)) {
            throw new RuntimeException("debugging, tell sap if this is in prod");
        }
        setElement(plrData.elements.indexOf(element), sync);
    }

    /**
     * Sets the active element to the one passed,
     * @param elementIndex The index of the element we want to be active
     * @param sync Whether we send the client this change
     */
    public void setElement(int elementIndex, boolean sync) {
        plrData.activeElementIndex = elementIndex;
        bindDefaultAbilities();
        if (currAbility != null) {
            currAbility.onRemove(this);
            currAbility = null;
            abilityData = null;
        }

        if (sync) {
            syncElements();
        }
    }

    /**
     * Adds an element to the bender
     * @return whether the element was added or not
     */
    public boolean addElement(@NotNull Element element, boolean sync) {
        boolean changed = false;
        if (!hasElement(element)) {
            plrData.elements.add(element);
            changed = true;
            if (hasElement(NoneElement.get())) {
                plrData.elements.remove(NoneElement.get());
                bindDefaultAbilities();
            }
        }

        if (sync && changed) {
            syncElements();
        }
        return changed;
    }

    public void removeElement(Element element, boolean sync) {
        if (hasElement(element)) {
            plrData.elements.remove(element);
            if (plrData.elements.isEmpty()) {
                plrData.elements.add(NoneElement.get());
            }
            if (plrData.activeElementIndex >= plrData.elements.size() - 1) {
                plrData.activeElementIndex = 0;
            }
            bindDefaultAbilities();
        }

        if (sync) {
            syncElements();
        }
    }

    public boolean hasElement(Element element) {
        return plrData.elements.contains(element);
    }

    /**
     * @return The current active element
     */
    public Element getElement() {
        return plrData.getElement();
    }

    public void syncElements() {
        if (player.getWorld().isClient || player.getServer() == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(packageElementsIntoString(plrData.elements));
        buf.writeInt(plrData.activeElementIndex);
        ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_ELEMENT_PACKET_ID, buf);
    }

    public static void syncAbility(Bender bender) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(bender.currAbility != null ? bender.getElement().abilityList.indexOf(bender.currAbility) : -1);
        ServerPlayNetworking.send((ServerPlayerEntity) bender.player, SYNC_CURR_ABILITY_PACKET_ID, buf);
    }

    /**
     * This collects necessary information and outputs it in a stylised manner, the brakets
     * are not yet closed which makes it easy to add more information if necessary, when you are done
     * modifying the builder, simply append "\n}" and it will look good
     *
     * @return A stylised string builder of the information present
     */
    public StringBuilder getStatus() {
        return getStatus(new StringBuilder());
    }

    /**
     * This collects necessary information and outputs it in a stylised manner, the brakets
     * are not yet closed which makes it easy to add more information if necessary, when you are done
     * modifying the builder, simply append "\n}" and it will look good
     *
     * @return A stylised string builder of the information present
     */
    public StringBuilder getStatus(StringBuilder builder) {
        builder.append(player.getGameProfile().getName()).append(" = {");
        builder.append("\n    Element = ").append(getElement().name);
        builder.append("\n    Current ability = ").append(Ability.getName(currAbility));
        builder.append("\n    Cast time = ").append(castTime);
        builder.append("\n    Chi = ").append(plrData.chi);

        PlayerData data = getData();
        for (int i = 0; i < data.boundAbilities.length; i++) {
            builder.append("\n    bind ").append(i).append(" = ").append(Ability.getName(data.boundAbilities[i]));
        }

        return builder;
    }


    public void bindDefaultAbilities() {
        clearBindings();

        int abilitySize = plrData.elements.get(plrData.activeElementIndex).bindableAbilities.size();
        for (int i = 0; i < 4; i++) {
            if (i < abilitySize) {
                bindAbility(plrData.elements.get(plrData.activeElementIndex).getBindableAbility(i), i);
            }
        }
    }

    /**
     * This method also adds xp proportional to the amount of chi used
     * <br>ex:
     * <br>  5 chi -> 0.5
     * <br> 15 chi -> 1.5
     * <br> 30 chi -> 3
     * @param val The amount by which we should reduce the chi level
     * @return True if we were able to reduce the chi without going in the negatives, false if not.
     */
    public boolean reduceChi(float val) {
        return reduceChi(val,true);
    }

    /**
     * This method also adds xp proportional to the amount of chi used
     * <br>ex:
     * <br>  5 chi -> 0.5
     * <br> 15 chi -> 1.5
     * <br> 30 chi -> 3
     * @param val The amount by which we should reduce the chi level
     * @param giveXP Whether we give the player xp for this
     * @return True if we were able to reduce the chi without going in the negatives, false if not.
     */
    public boolean reduceChi(float val, boolean giveXP) {
        ServerPlayerEntity serverPlayer = ((ServerPlayerEntity) player);
        if (serverPlayer.interactionManager.getGameMode().equals(GameMode.CREATIVE)) {
            return true;
        }


        float newChi = plrData.chi - val;
        if (newChi < 0) {
            if(newChi >= -10 && !safeHasStatusEffect(BURNOUT_EFFECT,player)){
                newChi = 0;
                player.addStatusEffect(new StatusEffectInstance(BURNOUT_EFFECT,200,0,false,false,true));
            }else {
                return false;
            }
        }

        if(giveXP){
            addXp(xpAddedByChi(val));
        }

        plrData.chi = newChi;
        syncChi();
        return true;
    }


    public void syncChi() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(plrData.chi);
        ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_CHI_PACKET_ID, buf);
    }

    public float xpAddedByChi(float chi) {
        return 0.1f * chi; //y = ax + b
    }

    /**
     * Adds the specified amount of xp, if the result is equal than the max xp,
     * it adds a level and resets xp. If it is bigger, it adds the remaining xp.
     * @param amount The amount of xp added
     */
    public void addXp(float amount) {
        float maxXp = getMaxXp(plrData.level);
        plrData.xp += amount;
        if (plrData.xp >= maxXp) {
            plrData.level++;
            float remainder = plrData.xp - maxXp;

            if (remainder < maxXp) {
                plrData.chi = 100;
                syncChi();
            }

            plrData.xp = 0;
            addXp(remainder);
        }
    }

    public static float getMaxXp(int level) { //TODO make a scaling max xp function
        return 100;
    }

    public PlayerData getData() {
        this.plrData = PlayerData.get(player);
        return plrData;
    }

    @Override
    public String toString() {
        return getStatus().append("\n}").toString();
    }

    public static String packageElementsIntoString(ArrayList<Element> elements) {
        if (elements.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.size() - 1; i++) {
            builder.append(elements.get(i)).append(",");
        }
        builder.append(elements.get(elements.size() - 1));
        if (BendingCommand.debug) {
            System.out.println(builder);
        }

        return builder.toString();
    }

    public static ArrayList<Element> unpackElementsFromString(String obj) {
        ArrayList<Element> elements = new ArrayList<>();
        String[] eNames = obj.split(",");

        if (eNames.length == 0) {
            elements.add(NoneElement.get());
            return elements;
        }

        for (String eName : eNames) {
            Element element = Element.getElementByName(eName);
            if (!elements.contains(element)) {
                elements.add(element);
            }
        }
        if (BendingCommand.debug) {
            System.out.println(SapsUtils.elementsArrayToString(elements));
        }
        return elements;
    }
}
