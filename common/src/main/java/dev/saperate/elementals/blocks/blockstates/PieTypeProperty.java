package dev.saperate.elementals.blocks.blockstates;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.HashSet;
import java.util.Set;

public class PieTypeProperty extends ExpandableStringProperty{
    public static ImmutableSet<String> values;
    public PieTypeProperty(String name) {
        super(name, values);
    }
    public static PieTypeProperty create() {
        return new PieTypeProperty("pie_type");
    }
    
    public static void register_property() {
        Set<String> values_set = new HashSet<>();
        add_values(values_set);
        values = ImmutableSet.copyOf(values_set);
    }
    
    ///If you want to add values, mixin into this method
    private static void add_values(Set<String> values_set) {
        values_set.add("plain");
        values_set.add("moonpeach");
        values_set.add("starberry");
        values_set.add("sweetberry");
    }
}
