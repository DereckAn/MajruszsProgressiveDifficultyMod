package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;

import java.util.List;

// Step 7: the config screen's difficulty slider and the survival aids of the easier steps.
public class MergeConfig {
	/**
	 * Slider order, easiest first.
	 * MAJRUSZ: game stage bonus only. GENTLE: Improved Mobs scaling with half its damage bonus. STANDARD: Improved Mobs.
	 * HARD: no hit cap. BRUTAL: both mods' scaling stacked. INFERNO: stacked, no aids at all.
	 */
	public enum Difficulty { MAJRUSZ, GENTLE, STANDARD, HARD, BRUTAL, INFERNO }

	public static Difficulty DIFFICULTY = Difficulty.STANDARD;
	public static float HIT_CAP = 0.4f; // MAJRUSZ..STANDARD: a single hit takes at most this share of the player's max health
	public static List< Float > CROWD_DAMAGE = List.of( 1.0f, 0.75f, 0.5f, 0.25f ); // all but INFERNO: 1st, 2nd, 3rd, 4th+ mob hit within 1 s
	public static float GENTLE_DAMAGE_FACTOR = 0.5f; // GENTLE: share of Improved Mobs' damage bonus mobs get
	public static float HEALTH_PER_ARMY = 2.0f; // earned max health on every step (2 = one heart)
	public static float HEALTH_PER_BLOOD_MOON = 1.0f;
	public static float MAX_EARNED_HEALTH = 60.0f; // 30 hearts

	static {
		Serializables.getStatic( MergeConfig.class )
			.define( "difficulty", Reader.enumeration( Difficulty::values ), ()->DIFFICULTY, v->DIFFICULTY = v )
			.define( "hit_cap", Reader.number(), ()->HIT_CAP, v->HIT_CAP = Range.of( 0.05f, 1.0f ).clamp( v ) )
			.define( "crowd_damage", Reader.list( Reader.number() ), ()->CROWD_DAMAGE, v->CROWD_DAMAGE = v.isEmpty() ? List.of( 1.0f ) : v )
			.define( "gentle_damage_factor", Reader.number(), ()->GENTLE_DAMAGE_FACTOR, v->GENTLE_DAMAGE_FACTOR = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "health_per_army", Reader.number(), ()->HEALTH_PER_ARMY, v->HEALTH_PER_ARMY = Range.of( 0.0f, 40.0f ).clamp( v ) )
			.define( "health_per_blood_moon", Reader.number(), ()->HEALTH_PER_BLOOD_MOON, v->HEALTH_PER_BLOOD_MOON = Range.of( 0.0f, 40.0f ).clamp( v ) )
			.define( "max_earned_health", Reader.number(), ()->MAX_EARNED_HEALTH, v->MAX_EARNED_HEALTH = Range.of( 0.0f, 1000.0f ).clamp( v ) );
	}

	/** Improved Mobs' difficulty-based health/damage/etc. (its attributes.json). */
	public static boolean isImprovedMobsScalingOn() {
		return DIFFICULTY != Difficulty.MAJRUSZ;
	}

	/** This mod's Expert/Master bonus (`features.mobs_spawn_stronger`). */
	public static boolean isMajruszScalingOn() {
		return DIFFICULTY == Difficulty.MAJRUSZ || DIFFICULTY.ordinal() >= Difficulty.BRUTAL.ordinal();
	}

	public static boolean hasHitCap() {
		return DIFFICULTY.ordinal() <= Difficulty.STANDARD.ordinal();
	}

	public static boolean hasCrowdDampening() {
		return DIFFICULTY != Difficulty.INFERNO;
	}

	/** Multiplier for Improved Mobs' damage bonus (melee modifier, projectile and explosion multipliers). */
	public static double improvedMobsDamageFactor() {
		return DIFFICULTY == Difficulty.GENTLE ? GENTLE_DAMAGE_FACTOR : 1.0;
	}
}
