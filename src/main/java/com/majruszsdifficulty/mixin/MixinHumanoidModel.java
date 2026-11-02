package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.items.ScrollItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Scroll casting pose: both arms raised while using a scroll (was the library's OnEntityModelSetup event)
@Mixin( HumanoidModel.class )
public abstract class MixinHumanoidModel {
	@Inject(
		at = @At( "TAIL" ),
		method = "setupAnim (Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"
	)
	private void setupAnim( HumanoidRenderState state, CallbackInfo callback ) {
		if( !state.isUsingItem ) {
			return;
		}

		HumanoidArm arm = state.useItemHand == InteractionHand.MAIN_HAND ? state.mainArm : state.mainArm.getOpposite();
		ItemStack itemStack = arm == HumanoidArm.RIGHT ? state.rightHandItemStack : state.leftHandItemStack;
		if( !( itemStack.getItem() instanceof ScrollItem ) ) {
			return;
		}

		HumanoidModel< ? > model = ( HumanoidModel< ? > )( Object )this;
		float ticks = state.ticksUsingItem;
		model.leftArm.x = 5.0f;
		model.leftArm.z = 0.0f;
		model.leftArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
		model.leftArm.yRot = 0.0f;
		model.leftArm.zRot = -2.3561945f;
		model.rightArm.x = -5.0f;
		model.rightArm.z = 0.0f;
		model.rightArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
		model.rightArm.yRot = 0.0f;
		model.rightArm.zRot = 2.3561945f;
	}
}
