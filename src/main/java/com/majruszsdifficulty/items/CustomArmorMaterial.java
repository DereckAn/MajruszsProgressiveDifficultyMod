package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

// 26.x: armor materials are records; repair items are tags and the worn texture is an equipment asset (assets/<ns>/equipment/<name>.json)
public interface CustomArmorMaterial {
	ArmorMaterial TATTERED = new ArmorMaterial(
		5,
		Map.of( ArmorType.BOOTS, 1, ArmorType.LEGGINGS, 2, ArmorType.CHESTPLATE, 3, ArmorType.HELMET, 1, ArmorType.BODY, 3 ),
		15,
		SoundEvents.ARMOR_EQUIP_LEATHER,
		0.0f,
		0.0f,
		TagKey.create( Registries.ITEM, MajruszsDifficulty.HELPER.getLocation( "repairs_tattered_armor" ) ),
		asset( "tattered" )
	);
	ArmorMaterial ENDERIUM = new ArmorMaterial(
		39,
		Map.of( ArmorType.BOOTS, 4, ArmorType.LEGGINGS, 6, ArmorType.CHESTPLATE, 8, ArmorType.HELMET, 4, ArmorType.BODY, 8 ),
		15,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		4.0f,
		0.1f,
		TagKey.create( Registries.ITEM, MajruszsDifficulty.HELPER.getLocation( "repairs_enderium_armor" ) ),
		asset( "enderium" )
	);

	private static ResourceKey< EquipmentAsset > asset( String name ) {
		return ResourceKey.create( EquipmentAssets.ROOT_ID, MajruszsDifficulty.HELPER.getLocation( name ) );
	}
}
