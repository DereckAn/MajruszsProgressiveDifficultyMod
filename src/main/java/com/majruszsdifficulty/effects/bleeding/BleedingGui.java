package com.majruszsdifficulty.effects.bleeding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.majruszlibrary.events.OnClientTicked;
import com.majruszlibrary.events.OnGuiOverlaysRegistered;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Environment( EnvType.CLIENT )
public class BleedingGui {
	static final List< Particle > PARTICLES = new ArrayList<>();

	static {
		OnGuiOverlaysRegistered.listen( data->data.register( "bleeding", BleedingGui::render ) );

		OnClientTicked.listen( BleedingGui::updateParticles );

		for( int x = 0; x < Particle.GRID_WIDTH; ++x ) {
			for( int y = 0; y < Particle.GRID_HEIGHT; ++y ) {
				PARTICLES.add( new Particle( x, y ) );
			}
		}
	}

	public static void addBloodOnScreen( int count ) {
		List< Integer > x = BleedingGui.randomizedCoordinates( Particle.GRID_WIDTH );
		List< Integer > y = BleedingGui.randomizedCoordinates( Particle.GRID_HEIGHT );

		for( int idx = 0; idx < count; ++idx ) {
			PARTICLES.get( x.get( idx ) * Particle.GRID_HEIGHT + y.get( idx ) ).makeVisible();
		}
	}

	private static List< Integer > randomizedCoordinates( int max ) {
		return Random.next( IntStream.iterate( 0, i->i + 1 ).limit( max ).boxed().collect( Collectors.toList() ), max );
	}

	// 26.x: GUI drawing takes the tint as an ARGB argument instead of RenderSystem shader state
	private static void render( GuiGraphicsExtractor graphics, float partialTick, int screenWidth, int screenHeight ) {
		for( Particle particle : PARTICLES ) {
			if( particle.hasFinished() ) {
				continue;
			}

			float color = particle.getColor();
			Particle.RenderData renderData = particle.buildRenderData( screenWidth, screenHeight );
			int argb = ARGB.colorFromFloat( particle.getAlpha(), color, color, color );
			graphics.blit( RenderPipelines.GUI_TEXTURED, renderData.resource, renderData.x, renderData.y, 0.0f, 0.0f, renderData.size, renderData.size, renderData.size, renderData.size, argb );
		}
	}

	private static void updateParticles( OnClientTicked data ) {
		PARTICLES.forEach( Particle::tick );
	}

	static class Particle {
		static final int ASSETS_COUNT = 7;
		static final int GRID_WIDTH = 6, GRID_HEIGHT = 4;
		static final int LIFETIME = TimeHelper.toTicks( 9.0 );
		static final List< Identifier > ASSETS = new ArrayList<>();

		static {
			for( int idx = 0; idx < ASSETS_COUNT; ++idx ) {
				ASSETS.add( MajruszsDifficulty.HELPER.getLocation( "textures/particle/blood_%d.png".formatted( idx ) ) );
			}
		}

		final int x;
		final int y;
		int ticks = LIFETIME;
		int phase = 0;

		public Particle( int x, int y ) {
			this.x = x;
			this.y = y;
		}

		public void makeVisible() {
			if( !this.hasFinished() ) {
				return;
			}

			this.ticks = Random.nextInt( 0, TimeHelper.toTicks( 2.0 ) );
			this.phase = Random.nextInt( 0, ASSETS_COUNT - 1 );
		}

		public RenderData buildRenderData( int width, int height ) {
			float size = height / ( GRID_HEIGHT * 1.5f );
			float x = this.x * size + ( this.x >= GRID_WIDTH / 2 ? width - GRID_WIDTH * size : 0 );
			float y = ( 1.5f * this.y + ( this.x % 2 == 0 ? 0.0f : 0.5f ) ) * size;

			return new RenderData( ( int )x, ( int )y, ( int )size, ASSETS.get( this.phase ) );
		}

		public boolean hasFinished() {
			return this.ticks >= LIFETIME;
		}

		public float getColor() {
			float ratio = ( float )this.ticks / LIFETIME;

			return Mth.lerp( ratio, 1.0f, 0.6f );
		}

		public float getAlpha() {
			float ratio = ( float )this.ticks / LIFETIME;

			return Mth.clamp( 0.7f * ( 1.0f - ratio * ratio ), 0.0f, 0.7f );
		}

		public void tick() {
			if( Side.getMinecraft().isPaused() ) {
				return;
			}

			++this.ticks;
		}

		public record RenderData( int x, int y, int size, Identifier resource ) {}
	}
}
