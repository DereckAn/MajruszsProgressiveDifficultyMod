package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import com.majruszlibrary.text.TextHelper;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import com.majruszsdifficulty.events.OnSoulJarMultiplierGet;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Mob;

public class UndeadSet {
	static final ItemSetRequirement HELMET = ItemSetRequirement.of( MajruszsDifficulty.TATTERED_HELMET_ITEM, EquipmentSlot.HEAD );
	static final ItemSetRequirement CHESTPLATE = ItemSetRequirement.of( MajruszsDifficulty.TATTERED_CHESTPLATE_ITEM, EquipmentSlot.CHEST );
	static final ItemSetRequirement LEGGINGS = ItemSetRequirement.of( MajruszsDifficulty.TATTERED_LEGGINGS_ITEM, EquipmentSlot.LEGS );
	static final ItemSetRequirement BOOTS = ItemSetRequirement.of( MajruszsDifficulty.TATTERED_BOOTS_ITEM, EquipmentSlot.FEET );
	static final ItemSetBonus DURABILITY = ItemSetBonus.any( 3 )
		.component( "majruszsdifficulty.sets.undead.bonus_1", 1 );
	static final ItemSetBonus SOUL_JAR = ItemSetBonus.any( 4 )
		.component( "majruszsdifficulty.sets.undead.bonus_2", TextHelper.translatable( "item.majruszsdifficulty.soul_jar" ) );
	static final ItemSet ITEM_SET = ItemSet.create()
		.component( "majruszsdifficulty.sets.undead.name" )
		.format( ChatFormatting.LIGHT_PURPLE )
		.require( HELMET, CHESTPLATE, LEGGINGS, BOOTS )
		.bonus( DURABILITY, SOUL_JAR );

	static {
		OnEntityDied.listen( UndeadSet::restoreDurability )
			.addCondition( data->data.attacker != null )
			.addCondition( data->data.target instanceof Mob )
			.addCondition( data->ITEM_SET.canTrigger( DURABILITY, data.attacker ) );

		OnSoulJarMultiplierGet.listen( UndeadSet::increaseSoulBonuses )
			.addCondition( data->ITEM_SET.canTrigger( SOUL_JAR, data.entity ) );
	}

	private static void restoreDurability( OnEntityDied data ) {
		// 26.x: getArmorSlots() is gone
		EquipmentSlots.ARMOR.forEach( slot->{
			ItemStack itemStack = data.attacker.getItemBySlot( slot );
			if( itemStack.isDamaged() ) {
				itemStack.setDamageValue( itemStack.getDamageValue() - 1 );
			}
		} );
	}

	private static void increaseSoulBonuses( OnSoulJarMultiplierGet data ) {
		data.multiplier *= 2.0f;
	}
}
