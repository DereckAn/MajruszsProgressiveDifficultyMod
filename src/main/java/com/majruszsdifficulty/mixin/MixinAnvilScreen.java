package com.majruszsdifficulty.mixin;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Step 7: matches MixinAnvilMenu, so the screen shows the XP cost instead of "Too Expensive!".
@Mixin( AnvilScreen.class )
public abstract class MixinAnvilScreen {
	@ModifyConstant( method = "extractLabels", constant = @Constant( intValue = 40 ) )
	private int noCostLimit( int limit ) {
		return Integer.MAX_VALUE;
	}
}
