package com.majruszsdifficulty.mixin;

import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.SoulJar;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// Soul Jar shields: pulsing soul overlays on top of the shield (26.x: shields are a special item model renderer,
// was BlockEntityWithoutLevelRenderer#renderByItem)
@Mixin( ShieldSpecialRenderer.class )
public abstract class MixinShieldSpecialRenderer {
	private @Shadow @Final ShieldModel model;

	@Inject(
		at = @At( "TAIL" ),
		method = "submit (Lnet/minecraft/core/component/DataComponentMap;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V"
	)
	private void submit( @Nullable DataComponentMap components, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords,
		boolean hasFoil, int outlineColor, CallbackInfo callback
	) {
		CustomData customData = components != null ? components.get( DataComponents.CUSTOM_DATA ) : null;
		if( customData == null ) {
			return;
		}

		SoulJar.BonusInfo bonusInfo = Serializables.read( new SoulJar.BonusInfo(), customData.copyTag() );
		List< SoulJar.BonusType > bonusTypes = bonusInfo.getBonusTypes();
		for( int idx = 0; idx < bonusTypes.size(); ++idx ) {
			float alpha = ( float )Math.max( Math.cos( Math.PI * ( TimeHelper.getClientTime() - 0.6667f * idx ) ), 0.0f );
			int color = ARGB.color( ARGB.as8BitChannel( alpha ), bonusTypes.get( idx ).getColor() );
			collector.submitModel( this.model, Unit.INSTANCE, poseStack, RenderTypes.entityTranslucent( MajruszsDifficulty.HELPER.getLocation( "textures/entity/shield_soul_jar_overlay_%d.png".formatted( idx + 1 ) ) ),
				lightCoords, overlayCoords, color, null, outlineColor );
		}
	}
}
