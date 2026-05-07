package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.StringRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemWritableBookPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;

import java.util.Optional;
import java.util.function.Predicate;

public class HasElementCriterion extends SimpleCriterionTrigger<HasElementCriterion.Conditions> {

    public HasElementCriterion(){
    }

    public ResourceLocation getLocation() {//TODO test if this works
        return ResourceLocation.withDefaultNamespace(Constants.MODID + "/" + "has_element");
    }

    public void trigger(ServerPlayer player) {
        trigger(player, conditions -> conditions.requirementsMet(Bender.getBender(player)));
    }

    
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<ElementNamePredicate> elementName) implements SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ElementNamePredicate.CODEC.optionalFieldOf("element").forGetter(Conditions::elementName)).apply(instance, Conditions::new);
        });

        public Conditions(Optional<ContextAwarePredicate> player, Optional<ElementNamePredicate> elementName) {
            this.player = player;
            this.elementName = elementName;
        }

        public static Criterion<Conditions> any() {
            return Elementals.HAS_ELEMENT.create(new Conditions(Optional.empty(), Optional.empty()));
        }

        boolean requirementsMet(Bender bender) {
            if(elementName.isEmpty()){
                return false;
            }
            ElementNamePredicate element = elementName.get();
            return bender.hasElement(Element.getElement(element.contents()));
        }
    }

    public record ElementNamePredicate(String contents) implements Predicate<Filterable<String>> {
        public static final Codec<ElementNamePredicate> CODEC;

        public boolean test(Filterable<String> contents) {
            return ((String)contents.raw()).equals(this.contents);
        }

        static {
            CODEC = Codec.STRING.xmap(ElementNamePredicate::new, ElementNamePredicate::contents);
        }
    }
}
