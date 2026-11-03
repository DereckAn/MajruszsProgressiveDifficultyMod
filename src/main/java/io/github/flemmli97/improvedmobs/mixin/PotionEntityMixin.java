package io.github.flemmli97.improvedmobs.mixin;

import io.github.flemmli97.improvedmobs.common.entities.ai.util.AIUtils;
import io.github.flemmli97.improvedmobs.common.utils.EntityFlags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownSplashPotion.class)
public abstract class PotionEntityMixin {

    @Inject(method = "onHitAsPotion", at = @At(value = "HEAD"), cancellable = true)
    private void applyPotion(ServerLevel level, ItemStack potionItem, HitResult hitResult, CallbackInfo info) {
        ThrownSplashPotion potion = (ThrownSplashPotion) (Object) this;
        if (EntityFlags.get(potion).isThrownEntity) {
            info.cancel();
            AIUtils.applyPotion(level, potion, potionItem.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects(),
                    hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity() : null);
        }
    }
}
