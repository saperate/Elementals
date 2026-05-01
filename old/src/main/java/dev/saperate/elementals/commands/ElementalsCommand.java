package dev.saperate.elementals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.data.Bender;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ElementalsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("elementals")
                        .then(CommandManager.literal("status")
                                .executes(ElementalsCommand::statusSelf)
                        )
        );
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

}
