package com.majruszsdifficulty.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Step 7: no "Too Expensive!" limit, so enchantments can be combined up to level X if the player has the XP.
@Mixin( AnvilMenu.class )
public abstract class MixinAnvilMenu {
	// The third 40 in createResult() is `if (cost >= 40 && !player.hasInfiniteMaterials()) result = EMPTY`.
	@ModifyConstant( method = "createResult()V", constant = @Constant( intValue = 40, ordinal = 2 ) )
	private int noCostLimit( int limit ) {
		return Integer.MAX_VALUE;
	}
}
