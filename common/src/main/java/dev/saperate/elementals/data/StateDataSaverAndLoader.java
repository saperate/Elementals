package dev.saperate.elementals.data;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateDataSaverAndLoader extends SavedData {
    private static final SavedData.Factory<StateDataSaverAndLoader> type = new SavedData.Factory<>(
            StateDataSaverAndLoader::new,
            StateDataSaverAndLoader::load,
            null
    );
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    
    public static StateDataSaverAndLoader load(CompoundTag tag, HolderLookup.Provider provider) {
        StateDataSaverAndLoader state = new StateDataSaverAndLoader();

        CompoundTag playersNbt = tag.getCompound("players");
        playersNbt.getAllKeys().forEach(key -> {

            PlayerData playerData = new PlayerData();
            CompoundTag nbt = playersNbt.getCompound(key);

            playerData.elements = Bender.unpackElementsFromString(nbt.getString("element"));
            playerData.activeElementIndex = nbt.getInt("elementIndex");

            Element element = playerData.getElement();
            playerData.boundAbilities[0] = element.getBindableAbility(nbt.getInt("bind1"));
            playerData.boundAbilities[1] = element.getBindableAbility(nbt.getInt("bind2"));
            playerData.boundAbilities[2] = element.getBindableAbility(nbt.getInt("bind3"));
            playerData.boundAbilities[3] = element.getBindableAbility(nbt.getInt("bind4"));
            playerData.boundAbilities[4] = element.getBindableAbility(nbt.getInt("bind5"));
            playerData.boundAbilities[5] = element.getBindableAbility(nbt.getInt("bind6"));
            playerData.boundAbilities[6] = element.getBindableAbility(nbt.getInt("bind7"));
            playerData.boundAbilities[7] = element.getBindableAbility(nbt.getInt("bind8"));
            playerData.boundAbilities[8] = element.getBindableAbility(nbt.getInt("bind9"));
            playerData.boundAbilities[9] = element.getBindableAbility(nbt.getInt("bind10"));

            //Legacy system, this is here for compatibility
            if (!nbt.getCompound("upgrades").isEmpty()) {
                playerData.getElement().onRead(nbt.getCompound("upgrades"), playerData.upgrades);
            } else {
                CompoundTag upgradesList = nbt.getCompound("upgradeList");
                int uCount = upgradesList.getInt("upgradesCount");
                for (int i = 0; i < uCount; i++) {
                    CompoundTag upgradeNbt = upgradesList.getCompound("upgrade" + i);
                    playerData.upgrades.put(new Upgrade(upgradeNbt.getString("name"), -1), upgradeNbt.getBoolean("active"));
                }
            }


            playerData.chi = nbt.getFloat("chi");

            playerData.xp = nbt.getFloat("xp");
            playerData.level = nbt.getInt("level");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static StateDataSaverAndLoader getServerState(MinecraftServer server) {
        ServerLevel world = server.getLevel(Level.OVERWORLD);

        assert world != null;
        DimensionDataStorage persistentStateManager = world.getDataStorage();

        StateDataSaverAndLoader state = persistentStateManager.computeIfAbsent(type, Constants.MODID);
        state.setDirty();
        return state;
    }

    public static PlayerData getPlayerState(Player player) {
        StateDataSaverAndLoader serverState = getServerState(player.getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerData());
        return playerState;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach(((uuid, playerData) -> {
            CompoundTag playerNbt = new CompoundTag();

            playerNbt.putString("element", Bender.packageElementsIntoString(playerData.elements));
            playerNbt.putInt("elementIndex", playerData.activeElementIndex);

            // no save (por volta da linha 98)
            playerNbt.putInt("bind1", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[0]));
            playerNbt.putInt("bind2", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[1]));
            playerNbt.putInt("bind3", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[2]));
            playerNbt.putInt("bind4", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[3]));
            playerNbt.putInt("bind5", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[4]));
            playerNbt.putInt("bind6", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[5]));
            playerNbt.putInt("bind7", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[6]));
            playerNbt.putInt("bind8", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[7]));
            playerNbt.putInt("bind9", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[8]));
            playerNbt.putInt("bind10", playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[9]));


            CompoundTag upgradesNbt = new CompoundTag();
            upgradesNbt.putInt("upgradesCount", playerData.upgrades.size());

            int i = 0;
            for (Map.Entry<Upgrade, Boolean> entry : playerData.upgrades.entrySet()) {
                CompoundTag upgradeNbt = new CompoundTag();
                upgradeNbt.putString("name", entry.getKey().name);
                upgradeNbt.putBoolean("active", entry.getValue());
                upgradesNbt.put("upgrade" + i, upgradeNbt);
                i++;
            }
            playerNbt.put("upgradeList", upgradesNbt);


            playerNbt.putFloat("chi", playerData.chi);

            playerNbt.putFloat("xp", playerData.xp);
            playerNbt.putInt("level", playerData.level);

            playersNbt.put(uuid.toString(), playerNbt);
        }));
        compoundTag.put("players", playersNbt);


        return compoundTag;
    }
}
