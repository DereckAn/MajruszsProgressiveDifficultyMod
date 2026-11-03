package io.github.flemmli97.improvedmobs.mixin;

import com.mojang.serialization.Codec;
import io.github.flemmli97.improvedmobs.mixinhelper.SpawnReasonAccess;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements SpawnReasonAccess {

    @Unique
    private EntitySpawnReason improvedmobs$spawnreason;

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void onFinalize(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData,
                            CallbackInfoReturnable<SpawnGroupData> info) {
        this.improvedmobs$spawnreason = spawnType;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readData(ValueInput input, CallbackInfo info) {
        input.read("MobSpawnReason", Codec.INT).ifPresent(i -> this.improvedmobs$spawnreason = EntitySpawnReason.values()[i]);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveData(ValueOutput output, CallbackInfo info) {
        if (this.improvedmobs$spawnreason != null)
            output.putInt("MobSpawnReason", this.improvedmobs$spawnreason.ordinal());
    }

    @Override
    public EntitySpawnReason improvedMobs$getSpawnReason() {
        return this.improvedmobs$spawnreason;
    }
}
