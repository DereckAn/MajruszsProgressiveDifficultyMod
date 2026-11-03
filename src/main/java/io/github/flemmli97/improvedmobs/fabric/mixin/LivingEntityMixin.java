package io.github.flemmli97.improvedmobs.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import io.github.flemmli97.improvedmobs.common.events.EventCalls;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class, Player.class})
public abstract class LivingEntityMixin {

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "HEAD"), argsOnly = true)
    private float onDamage(float amount, ServerLevel level, DamageSource source) {
        return EventCalls.hurtEvent((LivingEntity) (Object) this, source, amount);
    }

    @Inject(method = "hurtServer", at = @At(value = "HEAD"), cancellable = true)
    private void hurtCheck(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (EventCalls.onAttackEvent((LivingEntity) (Object) this, source)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }
}
