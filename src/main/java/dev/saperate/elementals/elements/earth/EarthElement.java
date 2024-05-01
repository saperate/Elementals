package dev.saperate.elementals.elements.earth;

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


public class EarthElement extends Element {
    public EarthElement() {
        super("Earth", new Upgrade[]{

        });
    }

    public static Element get(){
        return elementList.get(3);
    }
}
