package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Blood moon tint. 26.x: sky, fog and light colors are environment attributes, so one time-based layer per color
// replaces the old FogRenderer, LevelRenderer (sky color) and LightTexture mixins. Ratios are the 1.20.1 ones.
@Mixin( ClientLevel.class )
public abstract class MixinClientLevel {
	@Inject(
		at = @At( "RETURN" ),
		method = "addEnvironmentAttributeLayers (Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;)Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;"
	)
	private void addBloodMoonLayers( EnvironmentAttributeSystem.Builder builder, CallbackInfoReturnable< EnvironmentAttributeSystem.Builder > callback ) {
		majruszsdifficulty$tint( builder, EnvironmentAttributes.FOG_COLOR, 0.25f );
		majruszsdifficulty$tint( builder, EnvironmentAttributes.SKY_COLOR, 0.5f );
		majruszsdifficulty$tint( builder, EnvironmentAttributes.SKY_LIGHT_COLOR, 0.2f );
	}

	private static void majruszsdifficulty$tint( EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute< Vector3fc > attribute, float minRatio ) {
		builder.addTimeBasedLayer( attribute, ( color, cacheTickId )->{
			float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, minRatio );

			return ratio < 1.0f ? new Vector3f( color.x(), color.y() * ratio, color.z() * ratio ) : color;
		} );
	}
}
