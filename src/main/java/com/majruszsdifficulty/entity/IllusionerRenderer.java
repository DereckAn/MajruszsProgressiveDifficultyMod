package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.IllusionerRenderState;
import net.minecraft.resources.Identifier;

// the 1.20.1 version was a copy of vanilla's renderer with the mod's texture; 26.x vanilla does the rest
@Environment( EnvType.CLIENT )
public class IllusionerRenderer extends net.minecraft.client.renderer.entity.IllusionerRenderer {
	public static final Identifier TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/illusioner.png" );

	public IllusionerRenderer( EntityRendererProvider.Context context ) {
		super( context );
	}

	@Override
	public Identifier getTextureLocation( IllusionerRenderState state ) {
		return TEXTURE;
	}
}
