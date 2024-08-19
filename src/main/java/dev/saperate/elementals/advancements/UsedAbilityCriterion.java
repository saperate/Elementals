package dev.saperate.elementals.advancements;

import com.google.gson.JsonObject;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class UsedAbilityCriterion extends AbstractCriterion<UsedAbilityCriterion.Conditions> {
    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(getId(), playerPredicate, obj.get("ability").getAsString());
    }

    @Override
    public Identifier getId() {
        return new Identifier(MODID+"/"+"used_ability");
    }

    public void trigger(ServerPlayerEntity player,String abilityName) {
        trigger(player, conditions -> conditions.requirementsMet(abilityName));
    }

    public static class Conditions extends AbstractCriterionConditions{
        public String name;
        public Conditions(Identifier id, LootContextPredicate entity, String name) {
            super(id, entity);
            this.name = name;
        }

        boolean requirementsMet(String abilityName) {
            return abilityName.equalsIgnoreCase(name);
        }
    }
}
