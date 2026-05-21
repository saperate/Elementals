package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;


import java.util.Optional;
import java.util.function.Predicate;


public class UsedAbilityCriterion extends SimpleCriterionTrigger<UsedAbilityCriterion.Conditions> {

    public UsedAbilityCriterion() {
    }

    public static String getName() {
        return "used_ability";
    }

    public void trigger(ServerPlayer player, String usedAbility) {
        trigger(player, conditions -> conditions.requirementsMet(usedAbility));
    }

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }


    public record Conditions(Optional<ContextAwarePredicate> player,
                             Optional<AbilityNamePredicate> abilityName) implements SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player), AbilityNamePredicate.CODEC.optionalFieldOf("abilityName").forGetter(Conditions::abilityName)).apply(instance, Conditions::new);
        });

        public Conditions(Optional<ContextAwarePredicate> player, Optional<AbilityNamePredicate> abilityName) {
            this.player = player;
            this.abilityName = abilityName;
        }

        public static Criterion<Conditions> any() {
            return Elementals.USED_ABILITY.get().createCriterion(new Conditions(Optional.empty(), Optional.empty()));
        }

        boolean requirementsMet(String usedAbility) {
            if(abilityName.isEmpty()){
                return false;
            }
            AbilityNamePredicate name = abilityName.get();
            return usedAbility.equalsIgnoreCase(name.contents());
        }
    }

    public record AbilityNamePredicate(String contents) implements Predicate<Filterable<String>> {
        public static final Codec<AbilityNamePredicate> CODEC;

        public boolean test(Filterable<String> contents) {
            return ((String)contents.raw()).equals(this.contents);
        }

        static {
            CODEC = Codec.STRING.xmap(AbilityNamePredicate::new, AbilityNamePredicate::contents);
        }
    }
}
