package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityEffectCheck;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

public class BleedingArmorProtection {
	private static float BASE_BONUS = 0.09f;
	private static float ARMOR_BONUS = 0.03f;
	private static float TOUGHNESS_BONUS = 0.03f;

	static {
		OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.effect.equals( MajruszsDifficulty.BLEEDING_EFFECT.get() ) )
			.addCondition( data->Random.check( BleedingArmorProtection.calculateCancelChance( data ) ) );

		OnBleedingTooltip.listen( BleedingArmorProtection::addTooltip )
			.addCondition( data->BleedingArmorProtection.isArmor( data.itemStack ) );

		Serializables.getStatic( BleedingConfig.class )
			.define( "chance_multiplier_base", Reader.number(), ()->BASE_BONUS, v->BASE_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "chance_multiplier_per_armor", Reader.number(), ()->ARMOR_BONUS, v->ARMOR_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "chance_multiplier_per_armor_toughness", Reader.number(), ()->TOUGHNESS_BONUS, v->TOUGHNESS_BONUS = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}

	private static float calculateCancelChance( OnEntityEffectCheck data ) {
		float chance = 1.0f;
		for( EquipmentSlot slot : EquipmentSlots.ARMOR ) {
			ItemStack itemStack = data.entity.getItemBySlot( slot );
			if( BleedingArmorProtection.isArmor( itemStack ) ) {
				chance *= 1.0f - BleedingArmorProtection.getArmorMultiplier( itemStack );
			}
		}

		return 1.0f - chance;
	}

	private static void addTooltip( OnBleedingTooltip data ) {
		data.addArmor( data.itemStack.get( DataComponents.EQUIPPABLE ).slot(), BleedingArmorProtection.getArmorMultiplier( data.itemStack ) );
	}

	// 26.x: ArmorItem is gone; armor pieces are items with an armor-slot Equippable, defense/toughness are attribute modifiers
	private static boolean isArmor( ItemStack itemStack ) {
		Equippable equippable = itemStack.get( DataComponents.EQUIPPABLE );

		return equippable != null && equippable.slot().isArmor();
	}

	private static float getArmorMultiplier( ItemStack itemStack ) {
		double defense = 0.0, toughness = 0.0;
		for( ItemAttributeModifiers.Entry entry : itemStack.getOrDefault( DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY ).modifiers() ) {
			if( entry.modifier().operation() != AttributeModifier.Operation.ADD_VALUE ) {
				continue;
			}
			if( entry.attribute().is( Attributes.ARMOR ) ) {
				defense += entry.modifier().amount();
			} else if( entry.attribute().is( Attributes.ARMOR_TOUGHNESS ) ) {
				toughness += entry.modifier().amount();
			}
		}

		return 1.0f - Range.of( 0.0f, 0.9f ).clamp( ( float )( BASE_BONUS + ARMOR_BONUS * defense + TOUGHNESS_BONUS * toughness ) );
	}
}
