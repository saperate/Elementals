package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.elements.water.*;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.items.WaterPouchItem;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class BloodElement extends Element {


    public BloodElement() {
        super("Blood", new Upgrade("Blood", new Upgrade[]{
                new Upgrade("bloodPush", new Upgrade[]{
                        new Upgrade("bloodPushPowerI", new Upgrade[]{
                                new Upgrade("bloodControl", new Upgrade[]{
                                        new Upgrade("bloodControlPrecision",4),
                                        new Upgrade("bloodControlPower", 4)
                                } ,true,4),
                                new Upgrade("bloodShield", 4)
                        }, 1)
                },2),
                new Upgrade("blood2", 1),
                new Upgrade("blood3", 1),
                new Upgrade("bloodBag", 1)
        },0));
        addAbility(new AbilityBlood1(), true);
        addAbility(new AbilityBloodPush());
        addAbility(new AbilityBloodControl());
        addAbility(new AbilityBloodShield());
        addAbility(new AbilityBlood2(), true);
        addAbility(new AbilityBloodShot());
        addAbility(new AbilityBlood3(), true);
        addAbility(new AbilityBlood4(), true);
    }

    public static Element get() {
        return elementList.get(6);
    }

    @Override
    public int getColor() {
        return 0xFF7d0b0b;
    }

    @Override
    public int getAccentColor() {
        return 0xFF400303;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return bender.hasElement(this);
    }

    public static boolean isNight(World world){
        long time = world.getTimeOfDay() % 24000;
        return time >= 13500 && time <= 22750;
    }
}
