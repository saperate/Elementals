package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static dev.saperate.elementals.Elementals.MODID;

public class UsedAbilityCriterion extends AbstractCriterion<UsedAbilityCriterion.Conditions> {
    //TODO fix this
    public UsedAbilityCriterion() {
    }

    public Identifier getId() {
        return Identifier.of(MODID + "/" + "usedAbility");
    }

    public void trigger(ServerPlayerEntity player, String abilityName) {
        trigger(player, conditions -> conditions.requirementsMet(abilityName));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return null;
    }

    public record Conditions(Optional<LootContextPredicate> player,
                             String abilityName) implements AbstractCriterion.Conditions {
        public Conditions(Optional<LootContextPredicate> player, String abilityName) {
            this.player = player;
            this.abilityName = abilityName;
        }

        boolean requirementsMet(String otherAbility) {
            return otherAbility.equalsIgnoreCase(abilityName);
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }
}
