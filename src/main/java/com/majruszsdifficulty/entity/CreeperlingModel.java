package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.modhelper.Resource;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.util.Mth;

@Environment( EnvType.CLIENT )
public class CreeperlingModel extends EntityModel< CreeperRenderState > {
	public static Resource< ModelDef > MODEL = MajruszsDifficulty.HELPER.loadClient( "creeperling_model", ModelDef.class );
	public final ModelParts modelParts;
	public final ModelPart head, leftBackFoot, leftFrontFoot, rightBackFoot, rightFrontFoot;

	public CreeperlingModel( ModelPart modelPart ) {
		super( modelPart );

		this.modelParts = new ModelParts( modelPart, MODEL.get() );
		this.head = this.modelParts.get( "head" );
		this.leftBackFoot = this.modelParts.get( "leftBackFoot" );
		this.leftFrontFoot = this.modelParts.get( "leftFrontFoot" );
		this.rightBackFoot = this.modelParts.get( "rightBackFoot" );
		this.rightFrontFoot = this.modelParts.get( "rightFrontFoot" );
	}

	@Override
	public void setupAnim( CreeperRenderState state ) {
		this.modelParts.reset();

		float limbFactor1 = 2.5f * state.walkAnimationPos * 0.6662f, limbFactor2 = 1.4f * state.walkAnimationSpeed;

		this.head.yRot += Math.toRadians( state.yRot );
		this.head.xRot += Math.toRadians( state.xRot );

		this.leftBackFoot.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightBackFoot.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.rightFrontFoot.xRot += Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.leftFrontFoot.xRot += Mth.cos( limbFactor1 ) * limbFactor2;
	}
}
