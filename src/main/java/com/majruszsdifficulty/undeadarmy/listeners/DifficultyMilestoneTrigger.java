package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import io.github.flemmli97.improvedmobs.api.difficulty.DifficultyFetcher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

// Step 7: an Undead Army starts each time a player's Improved Mobs difficulty passes a multiple of `difficulty_interval`.
public class DifficultyMilestoneTrigger {
	static {
		OnServerTicked.listen( DifficultyMilestoneTrigger::check )
			.addCondition( data->UndeadArmyConfig.DIFFICULTY_INTERVAL > 0 )
			.addCondition( data->TimeHelper.haveSecondsPassed( 5.0 ) );

		Serializables.get( Milestone.class )
			.define( "UndeadArmyDifficultyMilestone", Reader.integer(), s->s.value, ( s, v )->s.value = v );
	}

	private static void check( OnServerTicked data ) {
		for( ServerPlayer player : Side.getServer().getPlayerList().getPlayers() ) {
			int milestone = ( int )( DifficultyFetcher.getDifficulty( ( ServerLevel )player.level(), player.position() ) / UndeadArmyConfig.DIFFICULTY_INTERVAL );
			Serializables.modify( new Milestone(), EntityHelper.getOrCreateExtraTag( player ), stored->{
				if( milestone > stored.value && UndeadArmyHelper.tryToSpawn( player ) ) {
					stored.value = milestone; // skipped milestones give one army, not one each. ponytail: no spawn (underground, other dimension, army nearby) = retried every 5 s until it works
				}
			} );
		}
	}

	/** Banishing Totem: no difficulty-triggered army for this player until their difficulty rises by `levels`. */
	public static void postpone( ServerPlayer player, int levels ) {
		if( UndeadArmyConfig.DIFFICULTY_INTERVAL <= 0 ) {
			return;
		}
		double difficulty = DifficultyFetcher.getDifficulty( player.level(), player.position() );
		int milestone = ( int )( ( difficulty + levels ) / UndeadArmyConfig.DIFFICULTY_INTERVAL );
		Serializables.modify( new Milestone(), EntityHelper.getOrCreateExtraTag( player ), stored->stored.value = Math.max( stored.value, milestone ) );
	}

	public static class Milestone {
		public int value = 0;
	}
}
