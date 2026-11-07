package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.survival.SurvivalAids;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Survival aids (per-hit cap, crowd dampening) on the player's final damage (after armor and enchantments, before absorption).
@Mixin( LivingEntity.class )
public abstract class MixinLivingEntity {
	@Inject( method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At( "RETURN" ), cancellable = true )
	private void infernoAids( DamageSource source, float damage, CallbackInfoReturnable< Float > callback ) {
		if( ( Object )this instanceof ServerPlayer player ) {
			callback.setReturnValue( SurvivalAids.modify( player, source, callback.getReturnValue() ) );
		}
	}
}
