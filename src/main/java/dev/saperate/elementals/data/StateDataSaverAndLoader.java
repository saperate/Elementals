package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

import static dev.saperate.elementals.Elementals.MODID;

public class StateDataSaverAndLoader extends PersistentState {
    public HashMap<UUID,PlayerData> players = new HashMap<>();


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        System.err.println("Saving the stuff");
        NbtCompound playersNbt = new NbtCompound();
        players.forEach(((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putString("element", playerData.element.getName());

            playerNbt.putInt("bind1",playerData.element.bindableAbilities.indexOf(playerData.boundAbilities[0]));
            playerNbt.putInt("bind2",playerData.element.bindableAbilities.indexOf(playerData.boundAbilities[1]));
            playerNbt.putInt("bind3",playerData.element.bindableAbilities.indexOf(playerData.boundAbilities[2]));
            playerNbt.putInt("bind4",playerData.element.bindableAbilities.indexOf(playerData.boundAbilities[3]));

            playerNbt.put("upgrades",playerData.element.onSave(playerData));

            playersNbt.put(uuid.toString(), playerNbt);
        }));
        nbt.put("players", playersNbt);


        return nbt;
    }

    public static StateDataSaverAndLoader createFromNbt(NbtCompound tag) {
        StateDataSaverAndLoader state = new StateDataSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {

            PlayerData playerData = new PlayerData();
            NbtCompound nbt = playersNbt.getCompound(key);

            Element element = Element.getElementByName(nbt.getString("element"));

            playerData.element = element;

            playerData.boundAbilities[0] = element.getBindableAbility(nbt.getInt("bind1"));
            playerData.boundAbilities[1] = element.getBindableAbility(nbt.getInt("bind2"));
            playerData.boundAbilities[2] = element.getBindableAbility(nbt.getInt("bind3"));
            playerData.boundAbilities[3] = element.getBindableAbility(nbt.getInt("bind4"));

            playerData.element.onRead(nbt.getCompound("upgrades"),playerData);


            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<StateDataSaverAndLoader> type = new Type<>(
            StateDataSaverAndLoader::new,
            StateDataSaverAndLoader::createFromNbt,
            null
    );

    public static StateDataSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);

        assert world != null;
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        StateDataSaverAndLoader state = persistentStateManager.getOrCreate(type, MODID);
        state.markDirty();
        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player){
        StateDataSaverAndLoader serverState = getServerState(player.getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(),uuid -> new PlayerData());
        return playerState;
    }
}
