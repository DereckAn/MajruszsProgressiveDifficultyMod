package com.majruszsdifficulty.gamestage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

// 26.x: codec-based trigger, registered as majruszsdifficulty:game_stage in MajruszsDifficulty
public class GameStageAdvancement extends SimpleCriterionTrigger< GameStageAdvancement.Instance > {
	@Override
	public Codec< Instance > codec() {
		return Instance.CODEC;
	}

	public void trigger( ServerPlayer player, GameStage gameStage ) {
		this.trigger( player, instance->GameStageHelper.find( instance.stageId() ).getOrdinal() <= gameStage.getOrdinal() );
	}

	public record Instance( Optional< Holder< LootItemCondition > > player, String stageId ) implements SimpleCriterionTrigger.SimpleInstance {
		static final Codec< Instance > CODEC = RecordCodecBuilder.create( instance->instance.group(
			LootItemCondition.CODEC.optionalFieldOf( "player" ).forGetter( Instance::player ),
			Codec.STRING.fieldOf( "stage_id" ).forGetter( Instance::stageId )
		).apply( instance, Instance::new ) );
	}
}
