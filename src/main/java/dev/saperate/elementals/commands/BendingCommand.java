package dev.saperate.elementals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class BendingCommand {
    public static boolean debug = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("bending")
                        .then(CommandManager.literal("get").executes(BendingCommand::getSelfElement))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("element", ElementArgumentType.element())
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(BendingCommand::setElement)
                                        )
                                        .executes(BendingCommand::setSelfElement))
                        ).requires(source -> source.hasPermissionLevel(2)
                        )
                        .then(CommandManager.literal("upgrade")
                                .then(CommandManager.literal("list").then(
                                        CommandManager.argument("player", EntityArgumentType.player()).executes(BendingCommand::listUpgrades)
                                ).executes(BendingCommand::listSelfUpgrades))
                                .then(CommandManager.literal("clear").then(
                                        CommandManager.argument("player", EntityArgumentType.player()).executes(BendingCommand::clearUpgrades)
                                ).executes(BendingCommand::clearSelfUpgrades))
                                .then(CommandManager.literal("remove").then(
                                        CommandManager.argument("upgradeName", StringArgumentType.string())
                                                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(BendingCommand::removeUpgrade))
                                ).executes(BendingCommand::removeSelfUpgrade))
                        )
                        .then(CommandManager.literal("status")
                                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(BendingCommand::status))
                                .executes(BendingCommand::statusSelf)
                        )
                        .then(CommandManager.literal("level")
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                                        .executes(BendingCommand::levelSet)
                                                )
                                                .executes(BendingCommand::levelSetSelf)
                                        )
                                ).requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.literal("get").then(
                                        CommandManager.argument("player", EntityArgumentType.player()).executes(BendingCommand::levelGet)
                                ).executes(BendingCommand::levelSelfGet))
                        )
                        .then(CommandManager.literal("reset")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(BendingCommand::reset)
                                )
                                .executes(BendingCommand::resetSelf)
                        )
                        .then(CommandManager.literal("element")
                                        .then(CommandManager.literal("add").then(
                                                CommandManager.argument("element", ElementArgumentType.element()).executes(BendingCommand::addElement))
                                        )
                                        .then(CommandManager.literal("remove").then(
                                                CommandManager.argument("element", ElementArgumentType.element()).executes(BendingCommand::removeElement))
                                        )
                                //.then(CommandManager.literal("set").then()) TODO add this command
                        )
                .then(CommandManager.literal("debug").executes(BendingCommand::debug))

        );
    }

    private static int debug(CommandContext<ServerCommandSource> context) {
        debug = !debug;
        context.getSource().sendFeedback((
                        () -> Text.of("debug mode is now " + (debug ? "on" : "off")))
                , false);
        return 1;
    }

    public static int getSelfElement(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();
        if (element.getName().equals("None")) {
            context.getSource().sendFeedback((() -> Text.of("You do not have any bending element!")), false);
        } else {
            context.getSource().sendFeedback((() -> Text.of("You can bend " + bender.getElement().getName().toLowerCase() + "!")), false);
        }
        return 1;
    }

    public static int setSelfElement(CommandContext<ServerCommandSource> context) {
        if (context.getSource().getPlayer().getWorld().isClient) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);
        Element element = bender.getElement();
        Element newElement = ElementArgumentType.getElement(context, "element");

        if (element == newElement) {
            context.getSource().sendFeedback((() -> Text.of(
                    "You could already bend: " + newElement.name)
            ), false);
            return 1;
        }

        bender.setElement(newElement, true);

        plrData.boundAbilities = new Ability[4];
        bender.bindDefaultAbilities();

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getEntityName() + " can now bend: " + newElement.name)
        ), true);
        return 1;
    }

    public static int setElement(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity plr = EntityArgumentType.getPlayer(context, "player");
        if (plr.getWorld().isClient) {
            return 1;
        }
        Bender bender = Bender.getBender(plr);
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(plr);
        Element element = bender.getElement();
        Element newElement = ElementArgumentType.getElement(context, "element");

        if (element == newElement) {
            return 1;
        }

        bender.setElement(newElement, true);

        plrData.boundAbilities = new Ability[4];
        bender.bindDefaultAbilities();

        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + " can now bend: " + newElement.name)
        ), true);
        return 1;
    }

    public static int addElement(CommandContext<ServerCommandSource> context) {
        if (context.getSource().getPlayer().getWorld().isClient) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = ElementArgumentType.getElement(context, "element");

        if (bender.hasElement(element)) {
            context.getSource().sendFeedback((() -> Text.of(
                    "You could already bend: " + element.name)
            ), false);
            return 1;
        }

        bender.addElement(element, true);

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getEntityName() + " can now bend: " + element.name)
        ), true);
        return 1;
    }

    public static int removeElement(CommandContext<ServerCommandSource> context) {
        if (context.getSource().getPlayer().getWorld().isClient) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = ElementArgumentType.getElement(context, "element");

        if (!bender.hasElement(element)) {
            context.getSource().sendFeedback((() -> Text.of(
                    "You couldn't bend: " + element.name)
            ), false);
            return 1;
        }

        bender.removeElement(element, true);

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getEntityName() + " can no longer bend: " + element.name)
        ), true);
        return 1;
    }

    private static int listUpgrades(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgumentType.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        context.getSource().sendFeedback((() -> Text.of(
                "Upgrades owned by " + bender.player.getEntityName() + ":")
        ), false);
        for (Upgrade upgrade : plrData.upgrades.keySet()) {
            context.getSource().sendFeedback((() -> Text.of(
                    "-" + upgrade.name)
            ), false);
        }

        return 1;
    }

    private static int listSelfUpgrades(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        context.getSource().sendFeedback((() -> Text.of(
                "Upgrades owned by " + bender.player.getEntityName() + ":")
        ), false);
        for (Upgrade upgrade : plrData.upgrades.keySet()) {
            context.getSource().sendFeedback((() -> Text.of(
                    "-" + upgrade.name)
            ), false);
        }

        return 1;
    }

    private static int clearUpgrades(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgumentType.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        plrData.upgrades.clear();

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getEntityName() + " no longer has any upgrades!")
        ), true);
        return 1;
    }

    private static int clearSelfUpgrades(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        plrData.upgrades.clear();

        context.getSource().sendFeedback((() -> Text.of(
                "You no longer have any upgrades!")
        ), false);
        return 1;
    }

    private static int removeUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgumentType.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        String upgradeName = StringArgumentType.getString(context, "upgradeName");

        Upgrade temp = new Upgrade(upgradeName, -1);
        if (plrData.upgrades.remove(temp) == null) {//true if the player didn't have the specified upgrade
            context.getSource().sendFeedback((() -> Text.of(
                    bender.player.getEntityName() + " did not have the specified upgrade (" + upgradeName + ")!")
            ), false);
            return -1;
        } else {
            context.getSource().sendFeedback((() -> Text.of(
                    bender.player.getEntityName() + " no longer has upgrade " + upgradeName + "!")
            ), true);
            return 1;
        }
    }

    private static int removeSelfUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        String upgradeName = StringArgumentType.getString(context, "upgradeName");

        Upgrade temp = new Upgrade(upgradeName, -1);
        if (plrData.upgrades.remove(temp) == null) {//true if the player didn't have the specified upgrade
            context.getSource().sendFeedback((() -> Text.of(
                    bender.player.getEntityName() + " did not have the specified upgrade (" + upgradeName + ")!")
            ), false);
            return -1;
        } else {
            context.getSource().sendFeedback((() -> Text.of(
                    bender.player.getEntityName() + " no longer has upgrade " + upgradeName + "!")
            ), true);
            return 1;
        }
    }

    private static int status(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgumentType.getPlayer(context, "player"));

        context.getSource().sendFeedback((() -> Text.of(
                bender.toString())
        ), true);
        return 1;
    }

    private static int statusSelf(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());

        context.getSource().sendFeedback((() -> Text.of(
                bender.toString())
        ), true);
        return 1;
    }

    private static int levelSetSelf(CommandContext<ServerCommandSource> context) {
        PlayerEntity plr = context.getSource().getPlayer();
        if (plr.getWorld().isClient) {
            return 1;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerData.get(plr).level = value;
        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + "'s level is now: " + value)
        ), true);
        return 1;
    }

    private static int levelSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity plr = EntityArgumentType.getPlayer(context, "player");
        if (plr.getWorld().isClient) {
            return 1;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerData.get(plr).level = value;
        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + "'s level is now: " + value)
        ), false);
        return 1;
    }

    private static int levelGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity plr = EntityArgumentType.getPlayer(context, "player");
        if (plr.getWorld().isClient) {
            return 1;
        }
        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + "'s level is: " + PlayerData.get(plr))
        ), false);
        return 1;
    }

    private static int levelSelfGet(CommandContext<ServerCommandSource> context) {
        PlayerEntity plr = context.getSource().getPlayer();
        if (plr.getWorld().isClient) {
            return 1;
        }
        context.getSource().sendFeedback((() -> Text.of(
                "Your level is: " + PlayerData.get(plr))
        ), false);
        return 1;
    }

    private static int resetSelf(CommandContext<ServerCommandSource> context) {
        PlayerEntity plr = context.getSource().getPlayer();
        if (plr.getWorld().isClient) {
            return 1;
        }

        Bender bender = Bender.getBender(plr);
        bender.abilityData = null;
        bender.setCurrAbility(null);
        PlayerData.get(plr).chi = 100;
        bender.syncChi();

        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + " has been reset")
        ), true);
        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity plr = EntityArgumentType.getPlayer(context, "player");
        if (plr.getWorld().isClient) {
            return 1;
        }

        Bender bender = Bender.getBender(plr);
        bender.abilityData = null;
        bender.setCurrAbility(null);
        PlayerData.get(plr).chi = 100;
        bender.syncChi();

        context.getSource().sendFeedback((() -> Text.of(
                plr.getEntityName() + " has been reset")
        ), true);
        return 1;
    }

}
