package com.majruszsdifficulty.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

// replaces the old ILootPlatform/LootFabric service (Fabric-only now)
@Mixin( LootTable.class )
public interface IMixinLootTable {
	@Accessor( "pools" )
	List< LootPool > getPools();
}
