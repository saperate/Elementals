package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.water.WaterElement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.saperate.elementals.network.ModMessages.*;

public class Bender {
    public static Map<UUID, Bender> benders = new HashMap<>();
    public PlayerEntity player;
    private Element element;
    public Ability[] boundAbilities = new Ability[4];
    public Long castTime;
    @Nullable
    public Ability currAbility;
    @Nullable
    public Object abilityData;
    public static final float CHI_REGENERATION_RATE = 0.1f;//this is per tick (1/20 of a second)


    public Bender(PlayerEntity player, Element element) {
        this.player = player;
        if (element == null) {
            this.element = Element.elementList.get(0);
        } else {
            this.element = element;
        }

        benders.put(player.getUuid(), this);
    }

    public void bindAbility(Ability ability, int index) {
        if (element.contains(ability) && index >= 0 && index <= 4) {
            boundAbilities[index] = ability;
            if (!player.getWorld().isClient) {
                PlayerData.get(player).boundAbilities = boundAbilities;
            }
        }
    }


    public void clearBindings() {
        boundAbilities = new Ability[5];
        PlayerData.get(player).boundAbilities = new Ability[5];
    }

    public void bend(int index) {
        if (index >= 0 && index < 5 && boundAbilities[index] != null) {
            if (castTime == null && currAbility == null) {
                castTime = System.currentTimeMillis();
                setCurrAbility(boundAbilities[index]);
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

    public void tick(){
        PlayerData data = PlayerData.get(player);
        data.chi = Math.min(100, data.chi + Bender.CHI_REGENERATION_RATE);
    }

    public void setCurrAbility(Ability ability) {
        if (ability == null) {
            castTime = null;
        }
        this.currAbility = ability;
        syncAbility(this);
    }

    public void setCurrAbility(int i) {
        setCurrAbility(boundAbilities[i]);
        syncAbility(this);
    }

    public void setElement(Element element, boolean sync) {
        castTime = null;
        currAbility = null;
        if (element == null) {
            this.element = Element.elementList.get(0);
        } else {
            this.element = element;
        }

        if (sync && !player.getWorld().isClient && player.getServer() != null) {
            PlayerData.get(player).element = element;
            syncBending(this);
        }
    }

    public Element getElement() {
        return element;
    }

    public static void syncBending(Bender bender) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(bender.getElement().name);
        ServerPlayNetworking.send((ServerPlayerEntity) bender.player, SYNC_ELEMENT_PACKET_ID, buf);
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
        builder.append("\n    Chi = ").append(PlayerData.get(player).chi);

        for (int i = 0; i < boundAbilities.length; i++) {
            builder.append("\n    bind ").append(i).append(" = ").append(Ability.getName(boundAbilities[i]));
        }

        return builder;
    }


    public void bindDefaultAbilities() {
        int abilitySize = element.bindableAbilities.size();
        if (abilitySize >= 1) {
            bindAbility(element.getBindableAbility(0), 0);
        }
        if (abilitySize >= 2) {
            bindAbility(element.getBindableAbility(1), 1);
        }
        if (abilitySize >= 3) {
            bindAbility(element.getBindableAbility(2), 2);
        }
        if (abilitySize >= 4) {
            bindAbility(element.getBindableAbility(3), 3);
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
        if(serverPlayer.interactionManager.getGameMode().equals(GameMode.CREATIVE)){
            return true;
        }

        addXp(xpAddedByChi(val));
        PlayerData data = PlayerData.get(player);
        float newChi = data.chi - val;
        if(newChi < 0){
            return false;
        }
        data.chi = newChi;
        syncChi();
        return true;
    }

    public void syncChi() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(PlayerData.get(player).chi);
        ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_CHI_PACKET_ID, buf);
    }

    public float xpAddedByChi(float chi){
        return 0.1f * chi; //y = ax + b
    }

    /**
     * Adds the specified amount of xp, if the result is equal than the max xp,
     * it adds a level and resets xp. If it is bigger, it adds the remaining xp.
     * @param amount The amount of xp added
     */
    public void addXp(float amount){
        PlayerData data = PlayerData.get(player);
        float maxXp = getMaxXp(data.level);
        data.xp += amount;
        if(data.xp >= maxXp){
            data.level++;
            float remainder = data.xp - maxXp;

            if(remainder < maxXp){
                data.chi = 100;
                syncChi();
            }

            data.xp = 0;
            addXp(remainder);
        }
    }

    public static float getMaxXp(int level){ //TODO make a scaling max xp function
        return 100;
    }

    @Override
    public String toString() {
        return getStatus().append("\n}").toString();
    }
}
