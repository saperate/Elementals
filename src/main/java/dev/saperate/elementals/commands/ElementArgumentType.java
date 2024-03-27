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
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ElementArgumentType implements ArgumentType<Element> {
    private static final Collection<String> EXAMPLES = List.of(
            "water",
            "air",
            "fire"
    );
    public static final DynamicCommandExceptionType INVALID_ELEMENT = new DynamicCommandExceptionType(o -> Text.literal("Invalid element: " + o));
    @Override
    public Element parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        String name = reader.getString().substring(argBeginning).split(" ")[0];
        reader.readString();

        Element e = Element.getElementByName(name);

        if (e == null) {
            throw INVALID_ELEMENT.createWithContext(reader, name);
        }

        return e;
    }


    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final String remaining = builder.getRemaining();
        if (context.getSource() instanceof ServerCommandSource source) {
            return CommandSource.suggestMatching(
                    Lists.transform(Element.elementList, Element::getName),
                    builder
            );
        }
        return Suggestions.empty();
    }


    public static ElementArgumentType element() {
        return new ElementArgumentType();
    }


    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static <S> Element getElement(CommandContext<S> context) {
        return context.getArgument("name", Element.class);
    }
}
