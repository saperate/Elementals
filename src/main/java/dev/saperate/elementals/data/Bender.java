package dev.saperate.elementals.data;

import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.saperate.elementals.network.ModMessages.*;

public class Bender {
    public static final float CHI_REGENERATION_RATE = 0.1f;//this is per tick (1/20 of a second)
    public static Map<UUID, Bender> benders = new HashMap<>();
    public PlayerEntity player;
    public PlayerData plrData;
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
        if (plrData.elements.get(plrData.activeElementIndex).contains(ability) && index >= 0 && index <= 4) {
            plrData.boundAbilities[index] = ability;
        }
    }


    public void clearBindings() {
        plrData.boundAbilities = new Ability[5];
    }

    public void bend(int index) {
        if (index >= 0 && index < 5 && plrData.boundAbilities[index] != null) {
            if (castTime == null && currAbility == null) {
                castTime = System.currentTimeMillis();
                setCurrAbility(plrData.boundAbilities[index]);
                abilityData = null;
                return;
            }
            if (currAbility != null && castTime != null) {
                currAbility.onCall(this, System.currentTimeMillis() - castTime);
                castTime = null;
            }
        }
    }

    public static Bender getBender(PlayerEntity player) {
        return benders.get(player.getUuid());
    }

    public static Bender getBender(UUID player) {
        return benders.get(player);
    }

    public void tick() {
        plrData.chi = Math.min(100, plrData.chi + Bender.CHI_REGENERATION_RATE);
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

        if (sync) {
            syncElements();
        }
    }

    public void addElement(@NotNull Element element, boolean sync) {
        if (!hasElement(element)) {
            if(hasElement(NoneElement.get())){
                plrData.elements.remove(NoneElement.get());
            }
            plrData.elements.add(element);
        }

        if (sync){
            syncElements();
        }
    }

    public void removeElement(Element element, boolean sync) {
        if (hasElement(element)) {
            plrData.elements.remove(element);
            if(plrData.elements.isEmpty()){
                plrData.elements.add(NoneElement.get());
            }
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
        if(player.getWorld().isClient || player.getServer() == null){
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
        int abilitySize = plrData.elements.get(plrData.activeElementIndex).bindableAbilities.size();
        if (abilitySize >= 1) {
            bindAbility(plrData.elements.get(plrData.activeElementIndex).getBindableAbility(0), 0);
        }
        if (abilitySize >= 2) {
            bindAbility(plrData.elements.get(plrData.activeElementIndex).getBindableAbility(1), 1);
        }
        if (abilitySize >= 3) {
            bindAbility(plrData.elements.get(plrData.activeElementIndex).getBindableAbility(2), 2);
        }
        if (abilitySize >= 4) {
            bindAbility(plrData.elements.get(plrData.activeElementIndex).getBindableAbility(3), 3);
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
        ServerPlayerEntity serverPlayer = ((ServerPlayerEntity) player);
        if (serverPlayer.interactionManager.getGameMode().equals(GameMode.CREATIVE)) {
            return true;
        }

        addXp(xpAddedByChi(val));
        float newChi = plrData.chi - val;
        if (newChi < 0) {
            return false;
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

    public PlayerData getData(){
        return PlayerData.get(player);
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
        if(BendingCommand.debug){
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
        if(BendingCommand.debug) {
            System.out.println(SapsUtils.elementsArrayToString(elements));
        }
        return elements;
    }
}
