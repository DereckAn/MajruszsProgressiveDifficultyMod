package com.majruszsdifficulty.mixin;

import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// 26.x: quality moved from LootPoolSingletonContainer to UniformContainerBase
@Mixin( UniformContainerBase.class )
public interface IMixinUniformContainerBase {
	@Accessor( "quality" )
	int getQuality();
}
