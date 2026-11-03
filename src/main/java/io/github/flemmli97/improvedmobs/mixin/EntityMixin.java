package io.github.flemmli97.improvedmobs.mixin;

import io.github.flemmli97.improvedmobs.common.utils.EntityFlags;
import io.github.flemmli97.improvedmobs.mixinhelper.EntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityData {

    @Unique
    private final EntityFlags improvedMobs$flags = new EntityFlags();

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"))
    private void readData(ValueInput input, CallbackInfo info) {
        input.read(EntityFlags.TAG_ID, CompoundTag.CODEC).ifPresent(this.improvedMobs$flags::load);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
    private void saveData(ValueOutput output, CallbackInfo info) {
        output.store(EntityFlags.TAG_ID, CompoundTag.CODEC, this.improvedMobs$flags.save());
    }

    @Override
    public EntityFlags improvedMobs$getFlags() {
        return this.improvedMobs$flags;
    }
}
