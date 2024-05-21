package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.elements.fire.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;


public class AirElement extends Element {
    public AirElement() {
        super("Air", new Upgrade[]{

        });
        addAbility(new AbilityAir1(), true);
        addAbility(new AbilityAirGust());
    }

    public static Element get(){
        return elementList.get(4);
    }
}
