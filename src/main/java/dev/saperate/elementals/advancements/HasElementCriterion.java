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

public class HasElementCriterion extends AbstractCriterion<HasElementCriterion.Conditions> {
    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(getId(), playerPredicate, obj.get("element").getAsString());
    }

    @Override
    public Identifier getId() {
        return new Identifier(MODID+"/"+"has_element");
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, conditions -> {
            return conditions.requirementsMet(Bender.getBender(player));
        });
    }

    public static class Conditions extends AbstractCriterionConditions{
        public String elementName;
        public Conditions(Identifier id, LootContextPredicate entity, String elementName) {
            super(id, entity);
            this.elementName = elementName;
        }

        boolean requirementsMet(Bender bender) {
            return bender.hasElement(Element.getElementByName(elementName));
        }
    }
}
