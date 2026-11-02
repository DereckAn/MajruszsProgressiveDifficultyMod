package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.items.ScrollItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// First-person bob of a scroll while casting (was the library's OnItemRendered event)
@Mixin( FirstPersonHandsAndItemsRenderer.class )
public abstract class MixinFirstPersonHandsAndItemsRenderer {
	@Inject(
		at = @At(
			ordinal = 0,
			shift = At.Shift.AFTER,
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose ()V",
			value = "INVOKE"
		),
		method = "submitArmWithItem"
	)
	private void submitArmWithItem( PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot,
		InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
		CallbackInfo callback
	) {
		LocalPlayer player = Minecraft.getInstance().player;
		if( player != null && player.isUsingItem() && player.getUseItem() == itemStack && itemStack.getItem() instanceof ScrollItem ) {
			poseStack.translate( 0.0, -0.1 * Math.sin( player.getTicksUsingItem() + partialTicks ), 0.0 );
		}
	}
}
