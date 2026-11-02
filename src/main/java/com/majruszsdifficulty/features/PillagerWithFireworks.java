package com.majruszsdifficulty.features;

import java.util.List;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityTypes;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class PillagerWithFireworks {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.MASTER_ID );
	private static float CHANCE = 0.15f;
	private static boolean IS_SCALED_BY_CRD = true;
	private static float CROSSBOW_MULTISHOT_CHANCE = 0.25f;
	private static float FIREWORK_DROP_CHANCE = 0.25f;
	private static int FIREWORK_COUNT = 8;

	static {
		OnEntitySpawned.listen( PillagerWithFireworks::giveFireworks )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity.getType().equals( EntityTypes.PILLAGER ) );

		Serializables.getStatic( Config.Features.class )
			.define( "pillager_with_fireworks", PillagerWithFireworks.class );

		Serializables.getStatic( PillagerWithFireworks.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v )
			.define( "crossbow_multishot_chance", Reader.number(), ()->CROSSBOW_MULTISHOT_CHANCE, v->CROSSBOW_MULTISHOT_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "firework_drop_chance", Reader.number(), ()->FIREWORK_DROP_CHANCE, v->FIREWORK_DROP_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "firework_count", Reader.integer(), ()->FIREWORK_COUNT, v->FIREWORK_COUNT = Range.of( 1, 64 ).clamp( v ) );
	}

	private static void giveFireworks( OnEntitySpawned data ) {
		Pillager pillager = ( Pillager )data.entity;
		ItemStack crossbow = pillager.getMainHandItem();
		if( crossbow.is( Items.CROSSBOW ) && Random.check( CROSSBOW_MULTISHOT_CHANCE ) ) {
			crossbow.enchant( pillager.level().registryAccess().lookupOrThrow( Registries.ENCHANTMENT ).getOrThrow( Enchantments.MULTISHOT ), 1 );
		}

		ItemStack fireworks = new ItemStack( Items.FIREWORK_ROCKET, FIREWORK_COUNT );
		// 26.x: fireworks data is a component (was the "Fireworks" NBT tag); flight duration 1 matches the old default
		FireworkExplosion explosion = new FireworkExplosion( FireworkExplosion.Shape.SMALL_BALL, IntList.of( 0x4a2a30, 0x666666 ), IntList.of(), false, false );
		fireworks.set( DataComponents.FIREWORKS, new Fireworks( 1, List.of( explosion ) ) );
		pillager.setItemSlot( EquipmentSlot.OFFHAND, fireworks );
		pillager.setDropChance( EquipmentSlot.OFFHAND, FIREWORK_DROP_CHANCE );
	}
}
