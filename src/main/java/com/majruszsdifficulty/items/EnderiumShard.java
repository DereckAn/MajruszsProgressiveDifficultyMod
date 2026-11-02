package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnderiumShard extends Item {
	public EnderiumShard( Properties properties ) {
		super( properties.rarity( Rarity.UNCOMMON ) );
	}
}
