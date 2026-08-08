package dev.saperate.elementals.blocks.blockstates;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/// This property uses an expandable ImmutableSet. (only expandable BEFORE creating this property)
/// The use for this is to have expandable properties,
/// you would make a set, add your fields and let addons add theirs before locking it into an immutable set
/// Then, store it into a static variable to reference when you create this property
public abstract class ExpandableStringProperty extends Property<String> {
    private final ImmutableSet<String> values;
    protected ExpandableStringProperty(String name, ImmutableSet<String> values) {
        super(name, String.class);
        this.values = values;
        if(values == null){
            throw new RuntimeException("Tried creating property without first initialising it");
        }
    }

    @Override
    public @NotNull Collection<String> getPossibleValues() {
        return values;
    }

    @Override
    public @NotNull String getName(@NotNull String value) {
        return value;
    }

    @Override
    public @NotNull Optional<String> getValue(@NotNull String name) {
        return values.contains(name) ? Optional.of(name) : Optional.empty();
    }
}
