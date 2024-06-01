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
                new Upgrade("path1", new Upgrade[]{
                        new Upgrade("airShield"),
                        new Upgrade("airTornado")
                },true),
                new Upgrade("path2", new Upgrade[]{
                        new Upgrade("airBall"),
                        new Upgrade("tempNode", new Upgrade[]{
                                new Upgrade("airBullets"),
                                new Upgrade("airSuffocate")
                        }, true)
                }),
                new Upgrade("path3", new Upgrade[]{
                        new Upgrade("temp")
                }),
                new Upgrade("path4", new Upgrade[]{
                        new Upgrade("spiritProjection")
                })
        });
        addAbility(new AbilityAir1(), true);
        addAbility(new AbilityAirGust());
        addAbility(new AbilityAirShield());
        addAbility(new AbilityAirTornado());
        addAbility(new AbilityAir2(), true);
        addAbility(new AbilityAirStream());
        addAbility(new AbilityAirBall());
        addAbility(new AbilityAirBullets());
        addAbility(new AbilityAirSuffocate());
        addAbility(new AbilityAir3(), true);
        addAbility(new AbilityAirScooter());
        addAbility(new AbilityAirJump());
        addAbility(new AbilityAir4(),true);
    }

    public static Element get(){
        return elementList.get(4);
    }
}
