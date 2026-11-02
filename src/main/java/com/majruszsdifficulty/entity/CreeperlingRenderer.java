package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment( EnvType.CLIENT )
public class CreeperlingRenderer extends MobRenderer< Creeperling, CreeperRenderState, CreeperlingModel > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "creeperling" );
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/creeperling.png" );

	public CreeperlingRenderer( EntityRendererProvider.Context context ) {
		super( context, new CreeperlingModel( context.bakeLayer( LAYER ) ), 0.25f );
	}

	@Override
	public Identifier getTextureLocation( CreeperRenderState state ) {
		return TEXTURE;
	}

	@Override
	public CreeperRenderState createRenderState() {
		return new CreeperRenderState();
	}

	@Override
	public void extractRenderState( Creeperling creeperling, CreeperRenderState state, float partialTicks ) {
		super.extractRenderState( creeperling, state, partialTicks );
		state.swelling = creeperling.getSwelling( partialTicks );
	}

	@Override
	protected float getWhiteOverlayProgress( CreeperRenderState state ) {
		float swelling = state.swelling;

		return ( int )( swelling * 10.0f ) % 2 == 0 ? 0.0f : Mth.clamp( swelling, 0.5f, 1.0f );
	}

	@Override
	protected void scale( CreeperRenderState state, PoseStack stack ) {
		float f = state.swelling;
		float f1 = 1.0f + Mth.sin( f * 100.0f ) * f * 0.01f;
		f = Mth.clamp( f, 0.0f, 1.0f );
		f = f * f;
		f = f * f;
		float f2 = ( 1.0f + f * 0.4f ) * f1;
		float f3 = ( 1.0f + f * 0.1f ) / f1;
		stack.scale( f2, f3, f2 );
	}
}
