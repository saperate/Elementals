package dev.saperate.elementals.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.saperate.elementals.elements.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<PlayerEntity> {

    public static final DynamicCommandExceptionType INVALID_PLAYER = new DynamicCommandExceptionType(o -> Text.literal("Invalid player: " + o));
    @Override
    public PlayerEntity parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        String name = reader.getString().substring(argBeginning).split(" ")[0];
        reader.readString();

        List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();
        for (PlayerEntity plr : players){
            System.out.println(plr.getName().getString());
            if(plr.getName().getString().equals(name)){
                return plr;
            }
        }
        throw INVALID_PLAYER.createWithContext(reader,name);

    }


    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final String remaining = builder.getRemaining();
        if (context.getSource() instanceof ClientCommandSource source) {
            return CommandSource.suggestMatching(
                    Lists.transform(Element.elementList, Element::getName),
                    builder
            );
        }
        return Suggestions.empty();
    }


    public static PlayerArgumentType player() {
        return new PlayerArgumentType();
    }



    public static <S> PlayerEntity getPlayer(CommandContext<S> context, String name) {
        return context.getArgument(name, PlayerEntity.class);
    }
}
