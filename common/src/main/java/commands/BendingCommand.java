package commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;


import java.util.Locale;
import java.util.function.Supplier;

public class BendingCommand {
    public static boolean debug = false;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(Commands.literal("bending")
                        .then(Commands.literal("get").executes(BendingCommand::getSelfElement)).requires(source -> source.hasPermission(2))
                        .then(Commands.literal("upgrade")
                                .then(Commands.literal("list").then(
                                        Commands.argument("player", EntityArgument.player()).executes(BendingCommand::listUpgrades)
                                ).executes(BendingCommand::listSelfUpgrades))
                                .then(Commands.literal("clear").then(
                                        Commands.argument("player", EntityArgument.player()).executes(BendingCommand::clearUpgrades)
                                ).executes(BendingCommand::clearSelfUpgrades))
                                .then(Commands.literal("remove").then(
                                        Commands.argument("upgradeName", StringArgumentType.string())
                                                .then(Commands.argument("player", EntityArgument.player()).executes(BendingCommand::removeUpgrade))
                                ).executes(BendingCommand::removeSelfUpgrade))
                        )
                        .then(Commands.literal("status")
                                .then(Commands.argument("player", EntityArgument.player()).executes(BendingCommand::status))
                                .executes(BendingCommand::statusSelf)
                        )
                        .then(Commands.literal("level")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(BendingCommand::levelSet)
                                                )
                                                .executes(BendingCommand::levelSetSelf)
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(BendingCommand::levelAdd)
                                                )
                                        )
                                )
                                .then(Commands.literal("get").then(
                                        Commands.argument("player", EntityArgument.player()).executes(BendingCommand::levelGet)
                                ).executes(BendingCommand::levelSelfGet))
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(BendingCommand::reset)
                                )
                                .executes(BendingCommand::resetSelf)
                        )
                        .then(Commands.literal("element")
                                        .then(Commands.literal("add").then(
                                                Commands.argument("element", ElementArgumentType.element())
                                                        .then(Commands.argument("player", EntityArgument.player())
                                                                .executes(BendingCommand::addElement))
                                                        .executes(BendingCommand::addSelfElement))

                                        )
                                        .then(Commands.literal("remove").then(
                                                Commands.argument("element", ElementArgumentType.element())
                                                        .then(Commands.argument("player", EntityArgument.player())
                                                                .executes(BendingCommand::removeElement))
                                                        .executes(BendingCommand::removeSelfElement))
                                        )
                        )
                .then(Commands.literal("debug").executes(BendingCommand::debug)).requires(source -> source.hasPermissionLevel(2))

        );
    }

    private static int debug(CommandContext<CommandSourceStack> context) {
        debug = !debug;
        context.getSource().sendSuccess((
                        () -> Component.literal("debug mode is now " + (debug ? "on" : "off")))
                , false);
        return 1;
    }

    public static int getSelfElement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();
        if (element.getName().equals("None")) {
            context.getSource().sendSuccess((() -> Component.literal("You do not have any bending element!")), false);
        } else {
            context.getSource().sendSuccess((() -> Component.literal("You can bend " + bender.getElement().getName().toLowerCase(Locale.CANADA) + "!")), false);
        }
        return 1;
    }

    public static int addSelfElement(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getPlayer().level().isClientSide) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = ElementArgumentType.getElement(context, "element");

        if (bender.hasElement(element)) {
            context.getSource().sendFailure((() -> Component.literal(
                    "You could already bend: " + element.name)
            ), false);
            return 1;
        }

        bender.addElement(element, true);

        context.getSource().sendSuccess((() -> Component.literal(
                bender.player.getScoreboardName() + " can now bend: " + element.name)
        ), true);
        return 1;
    }

    public static int addElement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (EntityArgument.getPlayer(context, "player").level().isClientSide) {
            return 1;
        }
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));
        Element element = ElementArgumentType.getElement(context, "element");

        if (bender.hasElement(element)) {
            context.getSource().sendFailure((() -> Component.literal(
                    "You could already bend: " + element.name)
            ), false);
            return 1;
        }

        bender.addElement(element, true);

        context.getSource().sendSuccess((() -> Component.literal(
                bender.player.getScoreboardName() + " can now bend: " + element.name)
        ), true);
        return 1;
    }

    public static int removeSelfElement(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getPlayer().level().isClientSide) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = ElementArgumentType.getElement(context, "element");

        if (!bender.hasElement(element)) {
            context.getSource().sendFailure((() -> Component.literal(
                    "You couldn't bend: " + element.name)
            ), false);
            return 1;
        }

        bender.removeElement(element, true);

        context.getSource().sendSuccess((() -> Component.literal(
                bender.player.getScoreboardName() + " can no longer bend: " + element.name)
        ), true);
        return 1;
    }


    public static int removeElement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (EntityArgument.getPlayer(context, "player").level().isClientSide) {
            return 1;
        }
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));
        Element element = ElementArgumentType.getElement(context, "element");

        if (!bender.hasElement(element)) {
            context.getSource().sendFailure((() -> Component.literal(
                    "You couldn't bend: " + element.name)
            ), false);
            return 1;
        }

        bender.removeElement(element, true);

        context.getSource().sendSuccess((() -> Component.literal(
                bender.player.getScoreboardName() + " can no longer bend: " + element.name)
        ), true);
        return 1;
    }

    private static int listUpgrades(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        context.getSource().sendSuccess((() -> Component.literal(
                "Upgrades owned by " + bender.player.getScoreboardName() + ":")
        ), false);
        for (Upgrade upgrade : plrData.upgrades.keySet()) {
            context.getSource().sendSuccess((() -> Component.literal(
                    "-" + upgrade.name)
            ), false);
        }

        return 1;
    }

    private static int listSelfUpgrades(CommandContext<CommandSourceStack> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        context.getSource().sendSuccess((() -> Component.literal(
                "Upgrades owned by " + bender.player.getScoreboardName() + ":")
        ), false);
        for (Upgrade upgrade : plrData.upgrades.keySet()) {
            context.getSource().sendSuccess((() -> Component.literal(
                    "-" + upgrade.name)
            ), false);
        }

        return 1;
    }

    private static int clearUpgrades(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        plrData.upgrades.clear();

        context.getSource().sendSuccess((() -> Component.literal(
                bender.player.getScoreboardName() + " no longer has any upgrades!")
        ), true);
        return 1;
    }

    private static int clearSelfUpgrades(CommandContext<CommandSourceStack> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        plrData.upgrades.clear();

        context.getSource().sendSuccess((() -> Component.literal(
                "You no longer have any upgrades!")
        ), false);
        return 1;
    }

    private static int removeUpgrade(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        String upgradeName = StringArgumentType.getString(context, "upgradeName");

        Upgrade temp = new Upgrade(upgradeName, -1);
        if (plrData.upgrades.remove(temp) == null) {//true if the player didn't have the specified upgrade
            context.getSource().sendFailure(Component.literal(
                    bender.player.getScoreboardName() + " did not have the specified upgrade (" + upgradeName + ")!")
            ));
            return -1;
        } else {
            context.getSource().sendSuccess((() -> Component.literal(
                    bender.player.getScoreboardName() + " no longer has upgrade " + upgradeName + "!")
            ), true);
            return 1;
        }
    }

    private static int removeSelfUpgrade(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        String upgradeName = StringArgumentType.getString(context, "upgradeName");

        Upgrade temp = new Upgrade(upgradeName, -1);
        if (plrData.upgrades.remove(temp) == null) {//true if the player didn't have the specified upgrade
            context.getSource().sendFailure(Component.literal(
                    bender.player.getScoreboardName() + " did not have the specified upgrade (" + upgradeName + ")!")
            ));
            return -1;
        } else {
            context.getSource().sendSuccess((() -> Component.literal(
                    bender.player.getScoreboardName() + " no longer has upgrade " + upgradeName + "!")
            ), true);
            return 1;
        }
    }

    private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(EntityArgument.getPlayer(context, "player"));

        context.getSource().sendSuccess((() -> Component.literal(
                bender.toString())
        ), true);
        return 1;
    }

    private static int statusSelf(CommandContext<CommandSourceStack> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());

        context.getSource().sendSuccess((() -> Component.literal(
                bender.toString())
        ), true);
        return 1;
    }

    private static int levelSetSelf(CommandContext<CommandSourceStack> context) {
        Player plr = context.getSource().getPlayer();
        if (plr.level().isClientSide) {
            return 1;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerData.get(plr).level = value;
        context.getSource().sendSuccess((() -> Component.literal(
                plr.getScoreboardName() + "'s level is now: " + value)
        ), true);
        return 1;
    }

    private static int levelSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player plr = EntityArgument.getPlayer(context, "player");
        if (plr.level().isClientSide) {
            return 1;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerData.get(plr).level = value;
        context.getSource().sendSuccess((() -> Component.literal(
                plr.getScoreboardName() + "'s level is now: " + value)
        ), false);
        return 1;
    }

    private static int levelAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player plr = EntityArgument.getPlayer(context, "player");
        if (plr.level().isClientSide) {
            return 1;
        }
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerData.get(plr).level += amount;
        return 1;
    }

    private static int levelGet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player plr = EntityArgument.getPlayer(context, "player");
        if (plr.level().isClientSide) {
            return 1;
        }
        context.getSource().sendSuccess((() -> Component.literal(
                plr.getScoreboardName() + "'s level is: " + PlayerData.get(plr).level)
        ), false);
        return 1;
    }

    private static int levelSelfGet(CommandContext<CommandSourceStack> context) {
        Player plr = context.getSource().getPlayer();
        if (plr.level().isClientSide) {
            return 1;
        }
        context.getSource().sendSuccess((() -> Component.literal(
                "Your level is: " + PlayerData.get(plr).level)
        ), false);
        return 1;
    }

    private static int resetSelf(CommandContext<CommandSourceStack> context) {
        Player plr = context.getSource().getPlayer();
        if (plr.level().isClientSide) {
            return 1;
        }

        Bender bender = Bender.getBender((ServerPlayer) plr);
        bender.abilityData = null;
        bender.setCurrAbility(null);
        PlayerData.get(plr).chi = 100;
        bender.syncChi();

        context.getSource().sendSuccess((() -> Component.literal(
                plr.getScoreboardName() + " has been reset")
        ), true);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer plr = EntityArgument.getPlayer(context, "player");
        if (plr.level().isClientSide) {
            return 1;
        }

        Bender bender = Bender.getBender(plr);
        bender.abilityData = null;
        bender.setCurrAbility(null);
        PlayerData.get(plr).chi = 100;
        bender.syncChi();

        context.getSource().sendSuccess((() -> Component.literal(
                plr.getScoreboardName() + " has been reset")
        ), true);
        return 1;
    }

}
