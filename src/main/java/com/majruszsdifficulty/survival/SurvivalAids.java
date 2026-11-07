package com.majruszsdifficulty.survival;

import com.majruszsdifficulty.data.MergeConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Per-hit cap and crowd dampening of the easier difficulty steps, applied to the player's final damage
// (after armor and enchantments) by MixinLivingEntity.
public class SurvivalAids {
	private static final int CROWD_WINDOW_TICKS = 20;
	private static final Map< UUID, long[] > CROWD = new HashMap<>(); // player -> {window start tick, hits in window}. ponytail: never pruned, one entry per player

	public static float modify( ServerPlayer player, DamageSource source, float damage ) {
		if( damage <= 0.0f || source.is( DamageTypeTags.BYPASSES_INVULNERABILITY ) ) { // void, /kill
			return damage;
		}
		if( MergeConfig.hasCrowdDampening() && source.getEntity() instanceof Mob ) { // melee and mob projectiles
			long tick = player.level().getGameTime();
			long[] window = CROWD.computeIfAbsent( player.getUUID(), uuid->new long[]{ tick, 0 } );
			if( tick - window[ 0 ] > CROWD_WINDOW_TICKS ) {
				window[ 0 ] = tick;
				window[ 1 ] = 0;
			}
			int hit = ( int )Math.min( window[ 1 ]++, MergeConfig.CROWD_DAMAGE.size() - 1 );
			damage *= MergeConfig.CROWD_DAMAGE.get( hit );
		}
		if( MergeConfig.hasHitCap() ) {
			damage = Math.min( damage, player.getMaxHealth() * MergeConfig.HIT_CAP );
		}

		return damage;
	}
}
