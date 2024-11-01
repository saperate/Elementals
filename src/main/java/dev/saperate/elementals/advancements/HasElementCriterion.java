package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.StringRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.CustomDataPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.WritableBookContentPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

import static dev.saperate.elementals.Elementals.MODID;

//TODO make sure this works
public class HasElementCriterion extends AbstractCriterion<HasElementCriterion.Conditions> {

    public HasElementCriterion(){
    }

    public Identifier getId() {
        return Identifier.of(MODID + "/" + "has_element");
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, conditions -> conditions.requirementsMet(Bender.getBender(player)));
    }


    @Override
    public Codec<HasElementCriterion.Conditions> getConditionsCodec() {
        return HasElementCriterion.Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<WritableBookContentPredicate.RawStringPredicate> elementName) implements AbstractCriterion.Conditions {
        public static final Codec<HasElementCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(HasElementCriterion.Conditions::player), WritableBookContentPredicate.RawStringPredicate.CODEC.optionalFieldOf("element").forGetter(HasElementCriterion.Conditions::elementName)).apply(instance, HasElementCriterion.Conditions::new);
        });

        public Conditions(Optional<LootContextPredicate> player, Optional<WritableBookContentPredicate.RawStringPredicate> elementName) {
            this.player = player;
            this.elementName = elementName;
        }

        public static AdvancementCriterion<HasElementCriterion.Conditions> any() {//T
            return Elementals.HAS_ELEMENT.create(new HasElementCriterion.Conditions(Optional.empty(), Optional.empty()));
        }

        boolean requirementsMet(Bender bender) {
            if(elementName.isEmpty()){
                return false;
            }
            WritableBookContentPredicate.RawStringPredicate element = elementName.get();
            return bender.hasElement(Element.getElementByName(element.contents()));
        }
    }
}
