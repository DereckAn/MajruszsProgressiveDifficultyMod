package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.data.WorldData;
import io.github.flemmli97.improvedmobs.api.difficulty.DifficultyFetcher;
import net.minecraft.server.level.ServerLevel;

// Step 7: a blood moon starts on the first night after the highest online player's Improved Mobs difficulty passes a multiple of `difficulty_interval`.
public class DifficultyMilestoneTrigger {
	private static int MILESTONE = 0;

	static {
		OnServerTicked.listen( DifficultyMilestoneTrigger::check )
			.addCondition( data->BloodMoonConfig.IS_ENABLED )
			.addCondition( data->BloodMoonConfig.DIFFICULTY_INTERVAL > 0 )
			.addCondition( data->BloodMoonHelper.getRelativeDayTime() == BloodMoonConfig.TIME.from );

		Serializables.getStatic( WorldData.class )
			.define( "blood_moon_difficulty_milestone", Reader.integer(), ()->MILESTONE, v->MILESTONE = v );
	}

	private static void check( OnServerTicked data ) {
		int milestone = Side.getServer().getPlayerList().getPlayers().stream()
			.mapToInt( player->( int )( DifficultyFetcher.getDifficulty( ( ServerLevel )player.level(), player.position() ) / BloodMoonConfig.DIFFICULTY_INTERVAL ) )
			.max()
			.orElse( -1 );
		if( milestone > MILESTONE && ( BloodMoonHelper.start() || BloodMoonHelper.isActive() ) ) {
			MILESTONE = milestone; // skipped milestones give one blood moon; a random-chance moon on the same night counts too
			MajruszsDifficulty.WORLD_DATA.setDirty();
		}
	}
}
