package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;

public class BloodMoonConfig {
	public static boolean IS_ENABLED = true;
	public static final Range< Long > TIME = Range.of( 12300L, 23600L );
	public static float NIGHT_TRIGGER_CHANCE = 0.0666f;
	public static float SPAWN_RATE_MULTIPLIER = 2.0f;
	public static float CRD_PENALTY = 0.5f;
	public static int DIFFICULTY_INTERVAL = 20;

	static {
		Serializables.getStatic( BloodMoonConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "night_trigger_chance", Reader.number(), ()->NIGHT_TRIGGER_CHANCE, v->NIGHT_TRIGGER_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "spawn_rate_multiplier", Reader.number(), ()->SPAWN_RATE_MULTIPLIER, v->SPAWN_RATE_MULTIPLIER = Range.of( 1.0f, 10.0f ).clamp( v ) )
			.define( "difficulty_interval", Reader.integer(), ()->DIFFICULTY_INTERVAL, v->DIFFICULTY_INTERVAL = Range.of( 0, 500 ).clamp( v ) )
			.define( "crd_penalty", Reader.number(), ()->CRD_PENALTY, v->CRD_PENALTY = Range.of( 0.0f, 1.0f ).clamp( v ) );
	}
}
