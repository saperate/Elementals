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
                new Upgrade("airGust", new Upgrade[]{
                        new Upgrade("airShield",2),
                        new Upgrade("airTornado", new Upgrade[]{
                                new Upgrade("airTornadoSpeedI", new Upgrade[]{
                                        new Upgrade("airTornadoSpeedII",1)
                                },1)
                        },2)
                },true,2),
                new Upgrade("airStream", new Upgrade[]{
                        new Upgrade("airBall", new Upgrade[]{
                                new Upgrade("airBallSpeedI", new Upgrade[]{
                                        new Upgrade("airBallSpeedII",1)
                                },1)
                        }, false, -1,2),
                        new Upgrade("airStreamDamageI", new Upgrade[]{
                                new Upgrade("airBullets", new Upgrade[]{
                                        new Upgrade("airBulletsDamageI", new Upgrade[]{
                                                new Upgrade("airBulletsSpeedI", new Upgrade[]{
                                                        new Upgrade("airBulletsSpeedII", new Upgrade[]{
                                                                new Upgrade("airBulletsMastery",2)
                                                        },1)
                                                },1)
                                        },1),
                                        new Upgrade("airBulletsCountI", new Upgrade[]{
                                                new Upgrade("airBulletsCountII",1)
                                        },1)
                                },2),
                                new Upgrade("airSuffocate",2)
                        }, true,1),
                        new Upgrade("airStreamSpeedI", new Upgrade[]{
                                new Upgrade("airStreamSpeedII", new Upgrade[]{
                                        new Upgrade("airStreamEfficiencyI", new Upgrade[]{
                                                new Upgrade("airStreamMastery",2)
                                        },1)
                                },1)
                        }, false, 1,1)
                },2),
                new Upgrade("airJump", new Upgrade[]{
                        new Upgrade("airJumpRangeI", new Upgrade[]{
                                new Upgrade("airJumpRangeII",1),
                        },1),
                        new Upgrade("airScooter", new Upgrade[]{
                                new Upgrade("airScooterSpeedI", new Upgrade[]{
                                        new Upgrade("airScooterSpeedII",1)
                                },1)
                        },2)
                },2),
                new Upgrade("airSpiritProjection", new Upgrade[]{
                        new Upgrade("airSpiritProjectionRangeI", new Upgrade[]{
                                new Upgrade("airSpiritProjectionRangeII", new Upgrade[]{
                                        new Upgrade("airSpiritProjectionRangeIII", new Upgrade[]{
                                                new Upgrade("airSpiritProjectionRangeIV",1)
                                        },1)
                                },1)
                        },1)
                },4)
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
