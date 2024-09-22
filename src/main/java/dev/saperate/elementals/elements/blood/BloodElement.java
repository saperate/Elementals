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
        super("Blood");
        addAbility(new AbilityBlood1(), true);
        addAbility(new AbilityBloodPush());
    }

    public static Element get() {
        return elementList.get(6);
    }

    @Override
    public int getColor() {
        return 0xFF40BEFF;
    }

    @Override
    public int getAccentColor() {
        return 0xFF0053F3;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                ;
    }
}
