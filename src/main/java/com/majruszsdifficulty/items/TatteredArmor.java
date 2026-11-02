package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import com.majruszlibrary.events.OnEntityPowderSnowCheck;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class TatteredArmor extends Item {
	static {
		OnEntityPowderSnowCheck.listen( OnEntityPowderSnowCheck::makeWalkable )
			.addCondition( TatteredArmor::hasBootsEquipped );
	}

	public static Function< Properties, TatteredArmor > boots() {
		return properties->new TatteredArmor( properties, ArmorType.BOOTS );
	}

	public static Function< Properties, TatteredArmor > chestplate() {
		return properties->new TatteredArmor( properties, ArmorType.CHESTPLATE );
	}

	public static Function< Properties, TatteredArmor > helmet() {
		return properties->new TatteredArmor( properties, ArmorType.HELMET );
	}

	public static Function< Properties, TatteredArmor > leggings() {
		return properties->new TatteredArmor( properties, ArmorType.LEGGINGS );
	}

	private static boolean hasBootsEquipped( OnEntityPowderSnowCheck data ) {
		return data.entity instanceof LivingEntity entity
			&& entity.getItemBySlot( EquipmentSlot.FEET ).is( MajruszsDifficulty.TATTERED_BOOTS_ITEM.get() );
	}

	private TatteredArmor( Properties properties, ArmorType type ) {
		super( properties.humanoidArmor( CustomArmorMaterial.TATTERED, type ).rarity( Rarity.UNCOMMON ) );
	}
}
