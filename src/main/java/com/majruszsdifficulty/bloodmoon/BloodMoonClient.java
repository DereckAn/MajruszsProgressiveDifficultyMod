package com.majruszsdifficulty.bloodmoon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.majruszlibrary.events.OnClientTicked;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

@Environment( EnvType.CLIENT )
public class BloodMoonClient {
	static float COLOR_RATIO = 0.0f;

	static {
		OnClientTicked.listen( BloodMoonClient::tick );
	}

	private static void tick( OnClientTicked data ) {
		ClientLevel level = Minecraft.getInstance().level;
		if( level == null ) {
			return;
		}

		long relativeTime = level.getDayTime() % Level.TICKS_PER_DAY;
		if( BloodMoonHelper.isActive() && BloodMoonConfig.TIME.within( relativeTime ) ) {
			COLOR_RATIO = ( float )( 2.0 * ( relativeTime - BloodMoonConfig.TIME.from ) / ( BloodMoonConfig.TIME.to - BloodMoonConfig.TIME.from ) - 1.0 );
			COLOR_RATIO = 1.0f - Math.abs( COLOR_RATIO * COLOR_RATIO * COLOR_RATIO );
		} else {
			COLOR_RATIO = 0.0f;
		}
	}
}
