package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.Animations;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

// 26.x: renderers extract a render state; the model only sees that state, so it carries the entity's animations
@Environment( EnvType.CLIENT )
public class TankRenderer extends MobRenderer< Tank, TankRenderer.State, TankModel > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "tank" );
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/tank.png" );

	public TankRenderer( EntityRendererProvider.Context context ) {
		super( context, new TankModel( context.bakeLayer( LAYER ) ), 0.5f );
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
	public void extractRenderState( Tank tank, State state, float partialTicks ) {
		super.extractRenderState( tank, state, partialTicks );
		state.animations = tank.getAnimations();
	}

	@Environment( EnvType.CLIENT )
	public static class State extends LivingEntityRenderState {
		public @Nullable Animations animations;
	}
}
