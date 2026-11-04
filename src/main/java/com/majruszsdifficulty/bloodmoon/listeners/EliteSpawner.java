package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.math.Random;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.tiers.TierConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig.MobDef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;

// Step 7: during a blood moon, a naturally spawned monster may bring an elite from the current tier with it.
public class EliteSpawner {
	static {
		OnEntitySpawned.listen( EliteSpawner::spawn )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->BloodMoonHelper.isActive() )
			.addCondition( data->data.mobSpawnType == EntitySpawnReason.NATURAL ) // elites spawn with EVENT, so no chain reaction
			.addCondition( data->data.entity instanceof Enemy );
	}

	private static void spawn( OnEntitySpawned data ) {
		ServerLevel level = ( ServerLevel )data.getLevel();
		TierConfig.TierDef tier = TierConfig.get( level, data.entity.position() );
		if( tier.elites.isEmpty() || Random.nextFloat() >= tier.eliteChance ) {
			return;
		}

		MobDef elite = Random.next( tier.elites );
		EntityType< ? > type = elite.type.get();
		for( int idx = 0; idx < elite.count; ++idx ) {
			Entity entity = EntityHelper.createSpawner( ()->type, level ).position( data.entity.position() ).spawn();
			if( entity instanceof Mob mob ) {
				TierConfig.applyBonus( mob, tier );
				if( elite.equipment != null ) {
					LootHelper.getLootTable( elite.equipment ).getRandomItems( LootHelper.toGiftParams( mob ) ).forEach( itemStack->ItemHelper.equip( mob, itemStack ) );
				}
			}
		}
	}
}
