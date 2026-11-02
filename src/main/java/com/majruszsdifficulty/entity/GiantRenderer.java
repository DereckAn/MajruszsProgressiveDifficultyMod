package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;

// 26.x: uses vanilla's zombie model/armor layers scaled up (the old GiantModel was a zombie model with its own layer);
// GiantModel's tweaks (slower limbs, never "aggressive" arms) are applied while extracting the render state
@Environment( EnvType.CLIENT )
public class GiantRenderer extends MobRenderer< Giant, ZombieRenderState, ZombieModel< ZombieRenderState > > {
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/giant.png" );
	public static final float SCALE = 5.0f;

	public GiantRenderer( EntityRendererProvider.Context context ) {
		super( context, new ZombieModel<>( context.bakeLayer( ModelLayers.ZOMBIE ) ), 0.4f * SCALE );

		this.addLayer( new ItemInHandLayer<>( this ) );
		this.addLayer( new HumanoidArmorLayer<>( this, ArmorModelSet.bake( ModelLayers.ZOMBIE_ARMOR, context.getModelSet(), ZombieModel::new ), context.getEquipmentRenderer() ) );
	}

	@Override
	public Identifier getTextureLocation( ZombieRenderState state ) {
		return TEXTURE;
	}

	@Override
	public ZombieRenderState createRenderState() {
		return new ZombieRenderState();
	}

	@Override
	public void extractRenderState( Giant giant, ZombieRenderState state, float partialTicks ) {
		super.extractRenderState( giant, state, partialTicks );
		HumanoidMobRenderer.extractHumanoidRenderState( giant, state, partialTicks, this.itemModelResolver );
		state.walkAnimationPos *= 0.25f;
		state.walkAnimationSpeed *= 0.5f;
		state.isAggressive = false;
	}

	@Override
	protected void scale( ZombieRenderState state, PoseStack stack ) {
		stack.scale( SCALE, SCALE, SCALE );
	}
}
