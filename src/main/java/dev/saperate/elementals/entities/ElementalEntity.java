package dev.saperate.elementals.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public class ElementalEntity extends Entity {
    public UUID ownerUUID;
    public ElementalEntity(EntityType<?> type, World world) {
        super(type, world);
    }


    public void setOwner(LivingEntity owner) {
        this.ownerUUID = owner.getUuid();
    }

    public Entity getOwner() {
        if(this.ownerUUID != null && this.getWorld() instanceof ServerWorld){
            return ((ServerWorld) this.getWorld()).getEntity(this.ownerUUID);
        }
        return null;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.containsUuid("Owner")){
            this.ownerUUID = nbt.getUuid("Owner");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(ownerUUID != null){
            nbt.putUuid("Owner", ownerUUID);
        }
    }
}
