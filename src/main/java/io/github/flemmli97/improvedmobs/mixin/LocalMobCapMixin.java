package io.github.flemmli97.improvedmobs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.flemmli97.improvedmobs.common.utils.NightSpawnCap;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts")
public abstract class LocalMobCapMixin {

    @ModifyExpressionValue(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"))
    private int nightCap(int original, @Local(argsOnly = true) MobCategory category) {
        return NightSpawnCap.apply(category, original);
    }
}
