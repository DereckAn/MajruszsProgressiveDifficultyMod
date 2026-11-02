package com.majruszsdifficulty.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import com.majruszlibrary.events.OnExploded;
import com.majruszsdifficulty.mixin.IMixinCreeper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class Creeperling extends Creeper {
	static {
		OnExploded.listen( Creeperling::weakenExplosion )
			.addCondition( data->data.entity instanceof Creeperling );
	}

	public static EntityType< Creeperling > createEntityType( ResourceKey< EntityType< ? > > key ) {
		return EntityType.Builder.of( Creeperling::new, MobCategory.MONSTER )
			.sized( 0.6f, 0.9f )
			.eyeHeight( 0.75f )
			.build( key );
	}

	public static AttributeSupplier createChildAttributes() {
		return Creeper.createAttributes()
			.add( Attributes.MAX_HEALTH, 6.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.35 )
			.build();
	}

	public Creeperling( EntityType< ? extends Creeper > entityType, Level level ) {
		super( entityType, level );

		( ( IMixinCreeper )this ).setExplosionRadius( 2 );
	}

	@Override
	public boolean isPowered() {
		return false; // disables charged Creeperling
	}

	@Override
	protected int getBaseExperienceReward( ServerLevel level ) {
		return 3;
	}

	private static void weakenExplosion( OnExploded data ) {
		data.skipBlockIf( blockPos->true );
		data.skipEntityIf( entity->!( entity instanceof LivingEntity ) );
	}
}
