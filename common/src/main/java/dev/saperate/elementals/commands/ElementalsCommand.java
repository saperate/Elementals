package dev.saperate.elementals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class ElementalsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(Commands.literal("elementals")
                        .then(Commands.literal("status")
                                .executes(ElementalsCommand::statusSelf)
                        )
        );
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

}
