package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class EnderiumArmor extends Item {
	public static Function< Properties, EnderiumArmor > boots() {
		return properties->new EnderiumArmor( properties, ArmorType.BOOTS );
	}

	public static Function< Properties, EnderiumArmor > chestplate() {
		return properties->new EnderiumArmor( properties, ArmorType.CHESTPLATE );
	}

	public static Function< Properties, EnderiumArmor > helmet() {
		return properties->new EnderiumArmor( properties, ArmorType.HELMET );
	}

	public static Function< Properties, EnderiumArmor > leggings() {
		return properties->new EnderiumArmor( properties, ArmorType.LEGGINGS );
	}

	private EnderiumArmor( Properties properties, ArmorType type ) {
		super( properties.humanoidArmor( CustomArmorMaterial.ENDERIUM, type ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
