package com.majruszsdifficulty.tiers;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig.MobDef;
import io.github.flemmli97.improvedmobs.api.difficulty.DifficultyFetcher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

// Step 7: progression tiers (game stage + Improved Mobs difficulty) that make Undead Armies and blood moons harder.
public class TierConfig {
	private static final AttributeHandler HEALTH = new AttributeHandler( "tier_health_bonus", ()->Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_MULTIPLIED_BASE );
	private static final AttributeHandler DAMAGE = new AttributeHandler( "tier_damage_bonus", ()->Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE );
	private static final String ARMY_GEAR = "majruszsdifficulty:undead_army/wave_5_mob";
	public static List< TierDef > TIERS = List.of(
		new TierDef( GameStage.NORMAL_ID, 0.0f, 1.0f, 0.0f, 0.0f, List.of(), 0.0f, List.of() ),
		new TierDef( GameStage.EXPERT_ID, 0.0f, 1.25f, 0.0f, 0.0f, List.of(
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY ),
			new MobDef( MajruszsDifficulty.TANK_ENTITY )
		), 0.05f, List.of(
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY ),
			new MobDef( MajruszsDifficulty.TANK_ENTITY ),
			new MobDef( EntityTypes.WITHER_SKELETON, null, 1 )
		) ),
		new TierDef( GameStage.MASTER_ID, 0.0f, 1.5f, 0.5f, 0.25f, List.of(
			new MobDef( EntityTypes.ZOMBIE, ARMY_GEAR, 4 ),
			new MobDef( EntityTypes.EVOKER, null, 1 ),
			new MobDef( MajruszsDifficulty.CURSED_ARMOR_ENTITY, null, 3 ),
			new MobDef( EntityTypes.WITCH, null, 2 ),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY )
		), 0.1f, List.of(
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY ),
			new MobDef( MajruszsDifficulty.TANK_ENTITY ),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY ),
			new MobDef( MajruszsDifficulty.CURSED_ARMOR_ENTITY, null, 2 ),
			new MobDef( EntityTypes.EVOKER, null, 1 ),
			new MobDef( EntityTypes.WITCH, null, 1 )
		) ),
		new TierDef( GameStage.MASTER_ID, 300.0f, 2.5f, 1.0f, 0.5f, List.of(
			new MobDef( EntityTypes.ZOMBIE, ARMY_GEAR, 6 ),
			new MobDef( EntityTypes.EVOKER, null, 2 ),
			new MobDef( MajruszsDifficulty.CURSED_ARMOR_ENTITY, null, 4 ),
			new MobDef( EntityTypes.WITCH, null, 3 ),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY, null, 2 ),
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY, null, 2 ),
			new MobDef( MajruszsDifficulty.TANK_ENTITY, null, 2 )
		), 0.2f, List.of(
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY, null, 2 ),
			new MobDef( MajruszsDifficulty.TANK_ENTITY ),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY ),
			new MobDef( MajruszsDifficulty.CURSED_ARMOR_ENTITY, null, 3 ),
			new MobDef( EntityTypes.EVOKER, null, 2 ),
			new MobDef( EntityTypes.WITCH, null, 2 )
		) )
	);

	static {
		Serializables.getStatic( TierConfig.class )
			.define( "list", Reader.list( Reader.custom( TierDef::new ) ), ()->TIERS, v->TIERS = v );

		Serializables.get( TierDef.class )
			.define( "game_stage", Reader.string(), s->s.gameStage.getId(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.define( "min_improved_mobs_difficulty", Reader.number(), s->s.minDifficulty, ( s, v )->s.minDifficulty = Range.of( 0.0f, 10000.0f ).clamp( v ) )
			.define( "health_bonus", Reader.number(), s->s.healthBonus, ( s, v )->s.healthBonus = Range.of( 0.0f, 10.0f ).clamp( v ) )
			.define( "damage_bonus", Reader.number(), s->s.damageBonus, ( s, v )->s.damageBonus = Range.of( 0.0f, 10.0f ).clamp( v ) )
			.define( "undead_army_size_multiplier", Reader.number(), s->s.armySizeMultiplier, ( s, v )->s.armySizeMultiplier = Range.of( 0.0f, 10.0f ).clamp( v ) )
			.define( "undead_army_extra_mobs", Reader.list( Reader.custom( MobDef::new ) ), s->s.armyExtraMobs, ( s, v )->s.armyExtraMobs = v )
			.define( "blood_moon_elite_chance", Reader.number(), s->s.eliteChance, ( s, v )->s.eliteChance = Range.CHANCE.clamp( v ) )
			.define( "blood_moon_elites", Reader.list( Reader.custom( MobDef::new ) ), s->s.elites, ( s, v )->s.elites = v );
	}

	/** The last tier in the list whose game stage and difficulty are reached at this position. */
	public static TierDef get( ServerLevel level, Vec3 position ) {
		int stage = GameStageHelper.determineGameStage( level, position ).getOrdinal();
		double difficulty = DifficultyFetcher.getDifficulty( level, position );
		TierDef result = TIERS.get( 0 );
		for( TierDef tier : TIERS ) {
			if( stage >= tier.gameStage.getOrdinal() && difficulty >= tier.minDifficulty ) {
				result = tier;
			}
		}

		return result;
	}

	public static void applyBonus( Mob mob, TierDef tier ) {
		if( HEALTH.hasAttribute( mob ) ) {
			HEALTH.setValue( tier.healthBonus ).apply( mob );
			mob.setHealth( mob.getMaxHealth() );
		}
		if( DAMAGE.hasAttribute( mob ) ) {
			DAMAGE.setValue( tier.damageBonus ).apply( mob );
		}
	}

	public static class TierDef {
		public GameStage gameStage;
		public float minDifficulty;
		public float armySizeMultiplier = 1.0f;
		public float healthBonus;
		public float damageBonus;
		public List< MobDef > armyExtraMobs = new ArrayList<>();
		public float eliteChance;
		public List< MobDef > elites = new ArrayList<>();

		public TierDef( String gameStageId, float minDifficulty, float armySizeMultiplier, float healthBonus, float damageBonus, List< MobDef > armyExtraMobs,
			float eliteChance, List< MobDef > elites
		) {
			this.gameStage = GameStageHelper.find( gameStageId );
			this.minDifficulty = minDifficulty;
			this.armySizeMultiplier = armySizeMultiplier;
			this.healthBonus = healthBonus;
			this.damageBonus = damageBonus;
			this.armyExtraMobs = armyExtraMobs;
			this.eliteChance = eliteChance;
			this.elites = elites;
		}

		public TierDef() {}
	}
}
