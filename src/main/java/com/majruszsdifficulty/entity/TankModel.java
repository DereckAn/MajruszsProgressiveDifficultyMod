package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.modhelper.Resource;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

@Environment( EnvType.CLIENT )
public class TankModel extends EntityModel< TankRenderer.State > {
	public static Resource< ModelDef > MODEL = MajruszsDifficulty.HELPER.loadClient( "tank_model", ModelDef.class );
	public final ModelParts modelParts;
	public final ModelPart head, leftLeg, rightLeg, body;

	public TankModel( ModelPart modelPart ) {
		super( modelPart );

		this.modelParts = new ModelParts( modelPart, MODEL.get() );
		this.head = this.modelParts.get( "head" );
		this.leftLeg = this.modelParts.get( "leftLeg" );
		this.rightLeg = this.modelParts.get( "rightLeg" );
		this.body = this.modelParts.get( "body" );
	}

	@Override
	public void setupAnim( TankRenderer.State state ) {
		this.modelParts.reset();

		float limbFactor1 = state.walkAnimationPos * 0.3333f, limbFactor2 = 0.9f * state.walkAnimationSpeed, bodyFactor = 0.2f * state.walkAnimationSpeed;

		// head rotation when looking around
		this.head.yRot += Math.toRadians( state.yRot );
		this.head.xRot += Math.toRadians( state.xRot ) + 0.0873f;

		// leg and body rotation while moving
		this.leftLeg.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightLeg.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.body.zRot += Mth.cos( limbFactor1 ) * bodyFactor;

		if( state.animations != null ) {
			state.animations.forEach( animation->animation.apply( this.modelParts, state.ageInTicks ) );
		}
	}
}
