package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.MoonPhase;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Blood moon: red moon phases (sprites in textures/environment/celestial/blood_moon, picked up by the vanilla
// celestials atlas) and reddish stars. Was MixinLevelRenderer#renderSky in 1.20.1.
@Mixin( SkyRenderer.class )
public abstract class MixinSkyRenderer {
	private @Shadow @Final TextureAtlas celestialsAtlas;
	@Unique private GpuBuffer majruszsdifficulty$bloodMoonBuffer;

	@Inject(
		at = @At( "TAIL" ),
		method = "<init> (Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/sprite/AtlasManager;Lcom/mojang/blaze3d/pipeline/RenderTarget;)V"
	)
	private void buildBloodMoonPhases( CallbackInfo callback ) {
		MoonPhase[] phases = MoonPhase.values();
		VertexFormat format = DefaultVertexFormat.POSITION_TEX;
		try( ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized( phases.length * 4 * format.getVertexSize() ) ) {
			BufferBuilder bufferBuilder = new BufferBuilder( byteBufferBuilder, PrimitiveTopology.QUADS, format );
			for( MoonPhase phase : phases ) { // same layout as vanilla SkyRenderer#buildMoonPhases
				TextureAtlasSprite sprite = this.celestialsAtlas.getSprite( MajruszsDifficulty.HELPER.getLocation( "blood_moon/" + phase.getSerializedName() ) );
				bufferBuilder.addVertex( -1.0f, 0.0f, -1.0f ).setUv( sprite.getU1(), sprite.getV1() );
				bufferBuilder.addVertex( 1.0f, 0.0f, -1.0f ).setUv( sprite.getU0(), sprite.getV1() );
				bufferBuilder.addVertex( 1.0f, 0.0f, 1.0f ).setUv( sprite.getU0(), sprite.getV0() );
				bufferBuilder.addVertex( -1.0f, 0.0f, 1.0f ).setUv( sprite.getU1(), sprite.getV0() );
			}
			try( MeshData mesh = bufferBuilder.buildOrThrow() ) {
				this.majruszsdifficulty$bloodMoonBuffer = RenderSystem.getDevice().createBuffer( ()->"Blood moon phases", 32, mesh.vertexBuffer() );
			}
		}
	}

	@Inject(
		at = @At( "TAIL" ),
		method = "close ()V"
	)
	private void close( CallbackInfo callback ) {
		this.majruszsdifficulty$bloodMoonBuffer.close();
	}

	@ModifyArg(
		at = @At(
			target = "Lnet/minecraft/client/renderer/SkyRenderer;drawCelestialBody (Ljava/util/function/Supplier;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;I)V",
			value = "INVOKE"
		),
		index = 4,
		method = "renderMoon (Lcom/mojang/renderpearl/api/commands/RenderPass;Lnet/minecraft/world/level/MoonPhase;FLcom/mojang/blaze3d/vertex/PoseStack;)V"
	)
	private GpuBuffer useBloodMoonTexture( GpuBuffer moonBuffer ) {
		return BloodMoonHelper.getColorRatio() > 0.0f ? this.majruszsdifficulty$bloodMoonBuffer : moonBuffer;
	}

	@ModifyArg(
		at = @At(
			target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform (Lorg/joml/Matrix4f;Lorg/joml/Vector4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;",
			value = "INVOKE"
		),
		index = 1,
		method = "renderStars (Lcom/mojang/renderpearl/api/commands/RenderPass;FLcom/mojang/blaze3d/vertex/PoseStack;)V"
	)
	private Vector4f tintStars( Vector4f color ) {
		float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, 0.5f );

		return new Vector4f( color.x(), color.y() * ratio, color.z() * ratio, color.w() );
	}
}
