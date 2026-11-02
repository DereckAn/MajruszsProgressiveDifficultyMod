package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.Animations;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

// 26.x: the armor layer bakes vanilla's zombie armor meshes (same as the old HumanoidModel.createMesh 0.5/1.0 layers)
// into CursedArmorModel, so the "assemble" animation also moves the worn armor
@Environment( EnvType.CLIENT )
public class CursedArmorRenderer extends HumanoidMobRenderer< CursedArmor, CursedArmorRenderer.State, CursedArmorModel > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cursed_armor" );
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/cursed_armor.png" );

	public CursedArmorRenderer( EntityRendererProvider.Context context ) {
		super( context, new CursedArmorModel( context.bakeLayer( LAYER ) ), 0.0f );

		this.addLayer( new HumanoidArmorLayer<>( this, ArmorModelSet.bake( ModelLayers.ZOMBIE_ARMOR, context.getModelSet(), CursedArmorModel::new ), context.getEquipmentRenderer() ) );
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
	public void extractRenderState( CursedArmor cursedArmor, State state, float partialTicks ) {
		super.extractRenderState( cursedArmor, state, partialTicks );
		state.animations = cursedArmor.getAnimations();
	}

	@Environment( EnvType.CLIENT )
	public static class State extends HumanoidRenderState {
		public @Nullable Animations animations;
	}
}
