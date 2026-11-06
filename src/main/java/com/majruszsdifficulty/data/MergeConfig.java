package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;

// Step 7: settings for the Improved Mobs merge.
public class MergeConfig {
	public enum StatScaling { IMPROVED_MOBS, MAJRUSZ, BOTH }

	public static StatScaling STAT_SCALING = StatScaling.IMPROVED_MOBS;

	static {
		Serializables.getStatic( MergeConfig.class )
			.define( "mob_stat_scaling", Reader.enumeration( StatScaling::values ), ()->STAT_SCALING, v->STAT_SCALING = v );
	}

	/** Improved Mobs' difficulty-based health/damage/etc. (its attributes.json). */
	public static boolean isImprovedMobsScalingOn() {
		return STAT_SCALING != StatScaling.MAJRUSZ;
	}

	/** This mod's Expert/Master bonus (`features.mobs_spawn_stronger`). */
	public static boolean isMajruszScalingOn() {
		return STAT_SCALING != StatScaling.IMPROVED_MOBS;
	}
}
