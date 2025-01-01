package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.WritableBookContentPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static dev.saperate.elementals.Elementals.MODID;

public class UsedAbilityCriterion extends AbstractCriterion<UsedAbilityCriterion.Conditions> {

    public UsedAbilityCriterion() {
    }

    public Identifier getId() {
        return Identifier.of(MODID + "/" + "usedAbility");
    }

    public void trigger(ServerPlayerEntity player, String usedAbility) {
        trigger(player, conditions -> conditions.requirementsMet(usedAbility));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return UsedAbilityCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<LootContextPredicate> player,
                             Optional<WritableBookContentPredicate.RawStringPredicate> abilityName) implements AbstractCriterion.Conditions {
        public static final Codec<UsedAbilityCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedAbilityCriterion.Conditions::player), WritableBookContentPredicate.RawStringPredicate.CODEC.optionalFieldOf("abilityName").forGetter(UsedAbilityCriterion.Conditions::abilityName)).apply(instance, UsedAbilityCriterion.Conditions::new);
        });

        public Conditions(Optional<LootContextPredicate> player, Optional<WritableBookContentPredicate.RawStringPredicate> abilityName) {
            this.player = player;
            this.abilityName = abilityName;
        }

        public static AdvancementCriterion<UsedAbilityCriterion.Conditions> any() {
            return Elementals.USED_ABILITY.create(new UsedAbilityCriterion.Conditions(Optional.empty(), Optional.empty()));
        }

        boolean requirementsMet(String usedAbility) {
            if(abilityName.isEmpty()){
                return false;
            }
            WritableBookContentPredicate.RawStringPredicate name = abilityName.get();
            return usedAbility.equalsIgnoreCase(name.contents());
        }
    }
}
