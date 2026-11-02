package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

// 26.x: AxeItem/PickaxeItem/... are gone; tools are plain items configured by Item.Properties (also tagged in data/minecraft/tags/item)
public class EnderiumTool {
	public static class Axe extends Item {
		public Axe( Properties properties ) {
			super( properties.axe( CustomItemTier.ENDERIUM, 6.0f, -3.1f ).rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Hoe extends Item {
		public Hoe( Properties properties ) {
			super( properties.hoe( CustomItemTier.ENDERIUM, -5.0f, 0.0f ).rarity( Rarity.UNCOMMON ).fireResistant() );
		}

		// TODO: till 3x3 area
	}

	public static class Pickaxe extends Item {
		public Pickaxe( Properties properties ) {
			super( properties.pickaxe( CustomItemTier.ENDERIUM, 1.0f, -2.8f ).rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Shovel extends Item {
		public Shovel( Properties properties ) {
			super( properties.shovel( CustomItemTier.ENDERIUM, 1.5f, -3.0f ).rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Sword extends Item {
		public Sword( Properties properties ) {
			super( properties.sword( CustomItemTier.ENDERIUM, 4.0f, -2.6f ).rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}
}
