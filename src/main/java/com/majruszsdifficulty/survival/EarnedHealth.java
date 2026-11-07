package com.majruszsdifficulty.survival;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonFinished;
import com.majruszsdifficulty.data.MergeConfig;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyDefeated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

// Permanent max health earned by defeating Undead Armies and surviving blood moons, on every difficulty step.
// Kept per player UUID in world data, so it survives death.
public class EarnedHealth {
	private static final AttributeHandler HEALTH = new AttributeHandler( "earned_health", ()->Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_VALUE );
	private static Map< String, Float > EARNED = new HashMap<>();

	static {
		OnUndeadArmyDefeated.listen( data->data.undeadArmy.participants.forEach( player->EarnedHealth.earn( player, MergeConfig.HEALTH_PER_ARMY ) ) );

		OnBloodMoonFinished.listen( data->Side.getServer().getPlayerList().getPlayers().stream()
			.filter( player->player.isAlive() && player.level().dimension() == Level.OVERWORLD )
			.forEach( player->EarnedHealth.earn( player, MergeConfig.HEALTH_PER_BLOOD_MOON ) ) );

		OnServerTicked.listen( EarnedHealth::sync )
			.addCondition( data->TimeHelper.haveSecondsPassed( 1.0 ) );

		Serializables.getStatic( WorldData.class )
			.define( "earned_health", Reader.map( Reader.number() ), ()->EARNED, v->EARNED = new HashMap<>( v ) );
	}

	private static void earn( ServerPlayer player, float amount ) {
		float before = EARNED.getOrDefault( player.getStringUUID(), 0.0f );
		float total = Math.min( before + amount, MergeConfig.MAX_EARNED_HEALTH );
		if( total <= before ) {
			return;
		}
		EARNED.put( player.getStringUUID(), total );
		MajruszsDifficulty.WORLD_DATA.setDirty();
		player.sendSystemMessage( Component.translatable( "majruszsdifficulty.earned_health", ( total - before ) / 2.0f, total / 2.0f ).withStyle( ChatFormatting.GOLD ) );
	}

	private static void sync( OnServerTicked data ) {
		for( ServerPlayer player : Side.getServer().getPlayerList().getPlayers() ) {
			HEALTH.setValue( Math.min( EARNED.getOrDefault( player.getStringUUID(), 0.0f ), MergeConfig.MAX_EARNED_HEALTH ) ).apply( player ); // transient, so re-applied after respawn
		}
	}
}
