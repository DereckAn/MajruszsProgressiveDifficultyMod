package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.Animations;
import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment( EnvType.CLIENT )
public class CerberusRenderer extends MobRenderer< Cerberus, CerberusRenderer.State, CerberusModel > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cerberus" );
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/cerberus.png" );

	public CerberusRenderer( EntityRendererProvider.Context context ) {
		super( context, new CerberusModel( context.bakeLayer( LAYER ) ), 0.75f );

		this.addLayer( new CerberusEyesLayer( this ) );
	}

	@Override
	public Identifier getTextureLocation( State state ) {
		return TEXTURE;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState( Cerberus cerberus, State state, float partialTicks ) {
		super.extractRenderState( cerberus, state, partialTicks );
		state.animations = cerberus.getAnimations();
		LocalPlayer player = Minecraft.getInstance().player;
		state.playerDistance = player != null && !player.isSpectator() ? AnyPos.from( player.position() ).dist( cerberus.position() ).floatValue() : Float.MAX_VALUE;
	}

	@Environment( EnvType.CLIENT )
	public static class State extends LivingEntityRenderState {
		public @Nullable Animations animations;
		public float playerDistance = Float.MAX_VALUE;
	}

	@Environment( EnvType.CLIENT )
	public static class CerberusEyesLayer extends EyesLayer< State, CerberusModel > {
		static final RenderType EYES = RenderTypes.eyes( MajruszsDifficulty.HELPER.getLocation( "textures/entity/cerberus_eyes.png" ) );

		public CerberusEyesLayer( RenderLayerParent< State, CerberusModel > layer ) {
			super( layer );
		}

		@Override
		public RenderType renderType() {
			return EYES;
		}
	}
}
