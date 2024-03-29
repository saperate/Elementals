package dev.saperate.elementals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
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
                                .then(CommandManager.argument("Bind Index", IntegerArgumentType.integer(1, 3))
                                        .executes(BendingCommand::bindAbility))))

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
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();
        Element newElement = ElementArgumentType.getElement(context, "element");

        if (element == newElement) {
            context.getSource().sendFeedback((() -> Text.of(
                    "You could already bend: " + bender.getElement().name)
            ), false);
            return 1;
        }

        bender.setElement(newElement,true);

        StateDataSaverAndLoader.getPlayerState(bender.player).boundAbilities = new Ability[5];

        //Very temporary stuff, will get removed once I get a GUI working
        int abilitySize = newElement.abilityList.size();
        if (abilitySize >= 1) {
            bender.bindAbility(newElement.getAbility(0), 0);
        }
        if (abilitySize >= 2) {
            bender.bindAbility(newElement.getAbility(1), 1);
        }

        context.getSource().sendFeedback((() -> Text.of(
                bender.player.getNameForScoreboard() + " can now bend: " + bender.getElement().name)
        ), true);
        return 1;
    }

    public static int bindAbility(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.getElement();

        int abilityIndex = IntegerArgumentType.getInteger(context, "Ability Index");
        int bindIndex = IntegerArgumentType.getInteger(context, "Bind Index");

        if (abilityIndex <= element.abilityList.size()) {
            bender.bindAbility(element.getAbility(abilityIndex - 1), bindIndex - 1);
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


}
