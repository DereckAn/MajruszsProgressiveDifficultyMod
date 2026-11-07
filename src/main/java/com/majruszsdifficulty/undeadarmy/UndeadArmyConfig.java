package com.majruszsdifficulty.undeadarmy;

import net.minecraft.world.entity.EntityTypes;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UndeadArmyConfig {
	public static boolean IS_ENABLED = true;
	public static boolean RESET_ALL_PARTICIPANTS_KILLS = true;
	public static float WAVE_DURATION = 1200.0f;
	public static float PREPARING_DURATION = 10.0f;
	public static float HIGHLIGHT_DELAY = 300.0f;
	public static float EXTRA_PLAYER_RATIO = 0.5f;
	public static int AREA_RADIUS = 70;
	public static int KILL_REQUIREMENT = 0; // Step 7: 0 = off, armies come from Improved Mobs difficulty milestones
	public static int DIFFICULTY_INTERVAL = 10;
	public static boolean ALWAYS_HIGHLIGHT = false; // Step 7: army mobs glow for the whole wave, not only after highlight_delay
	public static int TOTEM_POSTPONE_LEVELS = 30;
	public static int TOTEM_DIFFICULTY_COST = 5;
	public static int KILL_REQUIREMENT_FIRST = 25;
	public static int KILL_REQUIREMENT_WARNING = 3;
	public static List< WaveDef > WAVE_DEFS = List.of(
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2 )
			),
			null,
			GameStage.NORMAL_ID,
			8
		),
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 2 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 3 ),
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 3 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 2 )
			),
			null,
			GameStage.NORMAL_ID,
			16
		),
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_3_mob", 3 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_3_skeleton", 3 ),
				new MobDef( EntityTypes.HUSK, "majruszsdifficulty:undead_army/wave_3_mob", 3 ),
				new MobDef( EntityTypes.STRAY, "majruszsdifficulty:undead_army/wave_3_skeleton", 3 )
			),
			new MobDef( MajruszsDifficulty.TANK_ENTITY ),
			GameStage.NORMAL_ID,
			24
		),
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_4_mob", 2 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_4_skeleton", 2 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityTypes.HUSK, "majruszsdifficulty:undead_army/wave_4_mob", 3 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( EntityTypes.STRAY, "majruszsdifficulty:undead_army/wave_4_skeleton", 3 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 )
			),
			null,
			GameStage.EXPERT_ID,
			32
		),
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_5_mob", 1 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_5_skeleton", 1 ),
				new MobDef( EntityTypes.STRAY, "majruszsdifficulty:undead_army/wave_5_skeleton", 3 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityTypes.HUSK, "majruszsdifficulty:undead_army/wave_5_mob", 3 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2 )
			),
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY ),
			GameStage.EXPERT_ID,
			40
		),
		new WaveDef(
			List.of(
				new MobDef( EntityTypes.ZOMBIE, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
				new MobDef( EntityTypes.SKELETON, "majruszsdifficulty:undead_army/wave_6_skeleton", 1 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityTypes.STRAY, "majruszsdifficulty:undead_army/wave_6_skeleton", 1 ),
				new MobDef( EntityTypes.HUSK, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
				new MobDef( EntityTypes.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_6_wither_skeleton", 1 ),
				new MobDef( MajruszsDifficulty.CERBERUS_ENTITY )
			),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
			GameStage.MASTER_ID,
			48
		)
	);

	static {
		Serializables.getStatic( UndeadArmyConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "reset_all_participants_kills", Reader.bool(), ()->RESET_ALL_PARTICIPANTS_KILLS, v->RESET_ALL_PARTICIPANTS_KILLS = v )
			.define( "wave_duration", Reader.number(), ()->WAVE_DURATION, v->WAVE_DURATION = Range.of( 300.0f, 3600.0f ).clamp( v ) )
			.define( "preparing_duration", Reader.number(), ()->PREPARING_DURATION, v->PREPARING_DURATION = Range.of( 1.0f, 60.0f ).clamp( v ) )
			.define( "highlight_delay", Reader.number(), ()->HIGHLIGHT_DELAY, v->HIGHLIGHT_DELAY = Range.of( 30.0f, 3600.0f ).clamp( v ) )
			.define( "extra_size_ratio_per_player", Reader.number(), ()->EXTRA_PLAYER_RATIO, v->EXTRA_PLAYER_RATIO = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "area_radius", Reader.integer(), ()->AREA_RADIUS, v->AREA_RADIUS = Range.of( 35, 140 ).clamp( v ) )
			.define( "kill_requirement", Reader.integer(), ()->KILL_REQUIREMENT, v->KILL_REQUIREMENT = Range.of( 0, 1000 ).clamp( v ) )
			.define( "difficulty_interval", Reader.integer(), ()->DIFFICULTY_INTERVAL, v->DIFFICULTY_INTERVAL = Range.of( 0, 500 ).clamp( v ) )
			.define( "always_highlight", Reader.bool(), ()->ALWAYS_HIGHLIGHT, v->ALWAYS_HIGHLIGHT = v )
			.define( "totem_postpone_levels", Reader.integer(), ()->TOTEM_POSTPONE_LEVELS, v->TOTEM_POSTPONE_LEVELS = Range.of( 0, 500 ).clamp( v ) )
			.define( "totem_difficulty_cost", Reader.integer(), ()->TOTEM_DIFFICULTY_COST, v->TOTEM_DIFFICULTY_COST = Range.of( 0, 500 ).clamp( v ) )
			.define( "kill_requirement_first", Reader.integer(), ()->KILL_REQUIREMENT_FIRST, v->KILL_REQUIREMENT_FIRST = Range.of( 1, 1000 ).clamp( v ) )
			.define( "kill_requirement_warning", Reader.integer(), ()->KILL_REQUIREMENT_WARNING, v->KILL_REQUIREMENT_WARNING = Range.of( 1, 1000 ).clamp( v ) )
			.define( "waves", Reader.list( Reader.custom( WaveDef::new ) ), ()->WAVE_DEFS, v->WAVE_DEFS = v );

		Serializables.get( WaveDef.class )
			.define( "mobs", Reader.list( Reader.custom( MobDef::new ) ), s->s.mobDefs, ( s, v )->s.mobDefs = v )
			.define( "boss", Reader.optional( Reader.custom( MobDef::new ) ), s->s.bossDef, ( s, v )->s.bossDef = v )
			.define( "game_stage", Reader.string(), s->s.gameStage.getId(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.define( "exp", Reader.integer(), s->s.experience, ( s, v )->s.experience = Range.of( 0, 1000 ).clamp( v ) );

		Serializables.get( MobDef.class )
			.define( "type", Reader.location(), MobDef::getId, MobDef::setId )
			.define( "equipment", Reader.optional( Reader.location() ), s->s.equipment, ( s, v )->s.equipment = v )
			.define( "count", Reader.integer(), s->s.count, ( s, v )->s.count = Range.of( 1, 20 ).clamp( v ) )
			.define( "scale", Reader.optional( Reader.number() ), s->s.scale, ( s, v )->s.scale = v != null ? Range.of( 0.1f, 16.0f ).clamp( v ) : null )
			.define( "health_multiplier", Reader.optional( Reader.number() ), s->s.healthMultiplier, ( s, v )->s.healthMultiplier = v != null ? Range.of( 0.1f, 100.0f ).clamp( v ) : null );
	}

	public static class WaveDef {
		public List< MobDef > mobDefs = new ArrayList<>();
		public MobDef bossDef;
		public GameStage gameStage;
		public int experience = 0;

		public WaveDef( List< MobDef > mobDefs, MobDef bossDef, String gameStageId, int experience ) {
			this.mobDefs = mobDefs;
			this.bossDef = bossDef;
			this.gameStage = GameStageHelper.find( gameStageId );
			this.experience = experience;
		}

		public WaveDef() {}
	}

	public static class MobDef {
		public Supplier< ? extends EntityType< ? > > type; // returns null when the mob's mod is not installed
		public Identifier equipment;
		public int count = 1;
		public Float scale; // Step 7: optional body size (minecraft:scale), e.g. giant skeletons
		public Float healthMultiplier;
		private Identifier id; // kept so a missing mod's id survives a config save instead of becoming minecraft:pig

		/** A mob from another mod, spawned only when that mod is installed. */
		public static MobDef optional( String id, int count ) {
			MobDef def = new MobDef( ()->null, null, count );
			def.setId( Identifier.parse( id ) );

			return def;
		}

		public static void applyBody( Entity entity, Float scale, Float healthMultiplier ) {
			if( !( entity instanceof LivingEntity living ) ) {
				return;
			}
			if( scale != null && living.getAttribute( Attributes.SCALE ) != null ) {
				living.getAttribute( Attributes.SCALE ).setBaseValue( scale );
			}
			if( healthMultiplier != null && living.getAttribute( Attributes.MAX_HEALTH ) != null ) {
				AttributeInstance health = living.getAttribute( Attributes.MAX_HEALTH );
				health.setBaseValue( health.getBaseValue() * healthMultiplier );
				living.setHealth( living.getMaxHealth() );
			}
		}

		public MobDef body( float scale, float healthMultiplier ) {
			this.scale = scale;
			this.healthMultiplier = healthMultiplier;

			return this;
		}

		public @Nullable EntityType< ? > resolve() {
			return this.type.get();
		}

		Identifier getId() {
			return this.id != null ? this.id : BuiltInRegistries.ENTITY_TYPE.getKey( this.type.get() );
		}

		void setId( Identifier id ) {
			this.id = id;
			this.type = ()->BuiltInRegistries.ENTITY_TYPE.getOptional( id ).orElse( null );
		}

		public MobDef( Supplier< ? extends EntityType< ? > > type, String equipment, int count ) {
			this.type = type;
			this.equipment = equipment != null ? Identifier.parse( equipment ) : null;
			this.count = count;
		}

		public MobDef( EntityType< ? > type, String equipment, int count ) {
			this( ()->type, equipment, count );
		}

		public MobDef( Supplier< ? extends EntityType< ? > > type ) {
			this( type, null, 1 );
		}

		public MobDef() {}
	}
}
