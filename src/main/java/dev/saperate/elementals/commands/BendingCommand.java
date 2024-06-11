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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class BendingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("bending")
                .then(CommandManager.literal("get").executes(BendingCommand::getSelfElement))
                .then(CommandManager.literal("set").then(CommandManager.argument("element", ElementArgumentType.element()).executes(BendingCommand::setSelfElement)))
                .then(CommandManager.literal("bind")
                        .then(CommandManager.argument("Ability Index", IntegerArgumentType.integer(1))
                                .then(CommandManager.argument("Bind Index", IntegerArgumentType.integer(1, 4))
                                        .executes(BendingCommand::bindAbility))))
                .then(CommandManager.literal("upgrade")
                        .then(CommandManager.literal("list").executes(BendingCommand::listUpgrades))
                        .then(CommandManager.literal("buy").then(CommandManager.argument("name", StringArgumentType.string()).executes(BendingCommand::buyUpgrade)))
                        .then(CommandManager.literal("clear").executes(BendingCommand::clearUpgrades))
                )
                .then(CommandManager.literal("status")
                        .executes(BendingCommand::status)
                )
                .then(CommandManager.literal("level")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0,Integer.MAX_VALUE))
                                        .executes(BendingCommand::levelSet)
                                )
                        )
                        .then(CommandManager.literal("get")
                                .executes(BendingCommand::levelGet)
                        )
                )

        );
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

    public static int setSelfElement(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (context.getSource().getPlayer().getWorld().isClient) {
            return 1;
        }
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);
        Element element = bender.getElement();
        Element newElement = ElementArgumentType.getElement(context, "element");

        if (element == newElement) {
            context.getSource().sendFeedback((() -> Text.of(
                    "You could already bend: " + bender.getElement().name)
            ), false);
            return 1;
        }

        bender.setElement(newElement, true);

        bender.boundAbilities = new Ability[4];
        plrData.boundAbilities = new Ability[4];
        bender.bindDefaultAbilities();

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getEntityName() + " can now bend: " + bender.getElement().name)
        ), true);
        return 1;
    }

    public static int bindAbility(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();

        int abilityIndex = IntegerArgumentType.getInteger(context, "Ability Index");
        int bindIndex = IntegerArgumentType.getInteger(context, "Bind Index");

        if (abilityIndex <= element.bindableAbilities.size()) {
            bender.bindAbility(element.getBindableAbility(abilityIndex - 1), bindIndex - 1);
            context.getSource().sendFeedback((() -> Text.of(
                    "Ability now bound to: " + bindIndex)
            ), false);
            return 1;
        }
        context.getSource().sendFeedback((() -> Text.of(
                "Invalid ability index: " + abilityIndex)
        ), false);
        return -1;
    }


    private static int listUpgrades(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        context.getSource().sendFeedback((() -> Text.of(
                "Possible upgrades:")
        ), false);
        for (Upgrade upgrade : element.root.children) {
            for (Upgrade u : upgrade.nextUpgrades(plrData.upgrades)) {
                if (u.canBuy(plrData.upgrades)) {
                    context.getSource().sendFeedback((() -> Text.of(
                            "-" + u.name)
                    ), false);
                }
            }
        }


        return 1;
    }


    private static int buyUpgrade(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        String name = StringArgumentType.getString(context, "name");
        Upgrade upgrade = element.root.getUpgradeByNameRecursive(name);

        if (plrData.buyUpgrade(upgrade)) {
            plrData.upgrades.put(upgrade, true);
            StateDataSaverAndLoader.getServerState(bender.player.getServer()).markDirty();
            context.getSource().sendFeedback((() -> Text.of(
                    "Upgrade  \"" + upgrade.name + "\" was bought successfully!")
            ), false);
            return 1;
        }


        context.getSource().sendFeedback((() -> Text.of(
                "Failed to buy upgrade: \"" + name + "\".")
        ), false);
        return -1;
    }

    private static int clearUpgrades(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        PlayerData plrData = StateDataSaverAndLoader.getPlayerState(bender.player);

        plrData.upgrades.clear();

        context.getSource().sendFeedback((() -> Text.of(
                "You no longer have any upgrades!")
        ), false);
        return 1;
    }


    private static int status(CommandContext<ServerCommandSource> context) {
        Bender bender = Bender.getBender(context.getSource().getPlayer());

        System.out.println(bender.toString());
        context.getSource().sendFeedback((() -> Text.of(
                bender.toString())
        ), false);
        return 1;
    }

    private static int levelSet(CommandContext<ServerCommandSource> context) {
        PlayerEntity plr = context.getSource().getPlayer();
        if(plr.getWorld().isClient){
            return 1;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerData.get(plr).level = value;
        context.getSource().sendFeedback((() -> Text.of(
                "Your level is now: " + value)
        ), false);
        return 1;
    }

    private static int levelGet(CommandContext<ServerCommandSource> context) {
        PlayerEntity plr = context.getSource().getPlayer();
        if(plr.getWorld().isClient){
            return 1;
        }
        context.getSource().sendFeedback((() -> Text.of(
                "Your level is: " + PlayerData.get(plr))
        ), false);
        return 1;
    }

}
