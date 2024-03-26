package dev.saperate.elementals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class BendingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("bending")
                .then(CommandManager.literal("get").executes(BendingCommand::getSelfElement))
                //.then(CommandManager.literal("set").then(CommandManager.argument("name", ArgumentTypes.get()).executes(BendingCommand::run)))
        );
    }

    public static int getSelfElement(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Bender bender = Bender.getBender(context.getSource().getPlayer());
        Element element = bender.element;
        if(element.name.equals("None")){
            context.getSource().sendFeedback((() -> Text.of("You do not have any bending element!")),false);
        }else{
            context.getSource().sendFeedback((() -> Text.of("You can bend " + bender.element.name.toLowerCase() + "!")),false);
        }
        return 1;


    }
}
