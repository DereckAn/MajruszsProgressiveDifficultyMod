package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnderiumIngot extends Item {
	public EnderiumIngot( Properties properties ) {
		super( properties.rarity( Rarity.UNCOMMON ) );
	}
}
