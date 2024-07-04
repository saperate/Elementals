package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        NbtCompound playersNbt = new NbtCompound();
        players.forEach(((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putString("element", Bender.packageElementsIntoString(playerData.elements));
            playerNbt.putInt("elementIndex", playerData.activeElementIndex);

            playerNbt.putInt("bind1",playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[0]));
            playerNbt.putInt("bind2",playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[1]));
            playerNbt.putInt("bind3",playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[2]));
            playerNbt.putInt("bind4",playerData.getElement().bindableAbilities.indexOf(playerData.boundAbilities[3]));

            playerNbt.put("upgrades",playerData.getElement().onSave(playerData.upgrades));

            playerNbt.putFloat("chi", playerData.chi);

            playerNbt.putFloat("xp", playerData.xp);
            playerNbt.putInt("level", playerData.level);

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

            playerData.elements = Bender.unpackElementsFromString(nbt.getString("element"));
            playerData.activeElementIndex = nbt.getInt("elementIndex");

            Element element = playerData.getElement();
            playerData.boundAbilities[0] = element.getBindableAbility(nbt.getInt("bind1"));
            playerData.boundAbilities[1] = element.getBindableAbility(nbt.getInt("bind2"));
            playerData.boundAbilities[2] = element.getBindableAbility(nbt.getInt("bind3"));
            playerData.boundAbilities[3] = element.getBindableAbility(nbt.getInt("bind4"));

            playerData.getElement().onRead(nbt.getCompound("upgrades"),playerData.upgrades);

            playerData.chi = nbt.getFloat("chi");

            playerData.xp = nbt.getFloat("xp");
            playerData.level = nbt.getInt("level");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static StateDataSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);

        assert world != null;
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        StateDataSaverAndLoader state = persistentStateManager.getOrCreate(
                StateDataSaverAndLoader::createFromNbt,
                StateDataSaverAndLoader::new,
                MODID
        );
        state.markDirty();
        return state;
    }

    public static PlayerData getPlayerState(PlayerEntity player){
        StateDataSaverAndLoader serverState = getServerState(player.getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(),uuid -> new PlayerData());
        return playerState;
    }
}
