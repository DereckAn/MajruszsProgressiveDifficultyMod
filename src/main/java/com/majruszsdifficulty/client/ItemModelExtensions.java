package com.majruszsdifficulty.client;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.EnderiumShardLocator;
import com.majruszsdifficulty.items.SoulJar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 26.x: item model overrides and ItemColors became item model JSON (assets/<ns>/items/*.json) with registered
 * property and tint types. Used by enderium_shard_locator.json and soul_jar.json.
 */
@Environment( EnvType.CLIENT )
public class ItemModelExtensions {
	public static void register() {
		RangeSelectItemModelProperties.ID_MAPPER.put( MajruszsDifficulty.HELPER.getLocation( "shard_distance" ), ShardDistance.MAP_CODEC );
		ItemTintSources.ID_MAPPER.put( MajruszsDifficulty.HELPER.getLocation( "soul_color" ), SoulColor.MAP_CODEC );
	}

	/** 0 = shard right here, 1 = nothing nearby (was the "shard_distance" item property). */
	public record ShardDistance() implements RangeSelectItemModelProperty {
		public static final MapCodec< ShardDistance > MAP_CODEC = MapCodec.unit( new ShardDistance() );

		@Override
		public float get( ItemStack itemStack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed ) {
			return EnderiumShardLocator.Client.getShardDistance( itemStack, level, owner != null ? owner.asLivingEntity() : null, seed );
		}

		@Override
		public MapCodec< ShardDistance > type() {
			return MAP_CODEC;
		}
	}

	/** Color of the n-th soul in a Soul Jar (was OnItemRenderColorsGet). */
	public record SoulColor( int index ) implements ItemTintSource {
		public static final MapCodec< SoulColor > MAP_CODEC = RecordCodecBuilder.mapCodec( instance->instance.group(
			Codec.INT.fieldOf( "index" ).forGetter( SoulColor::index )
		).apply( instance, SoulColor::new ) );

		@Override
		public int calculate( ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner ) {
			int color = SoulJar.BonusInfo.read( itemStack ).getBonus( this.index ).map( SoulJar.BonusType::getColor ).orElse( 0xeeeeee - this.index * 0x111111 );

			return ARGB.opaque( color );
		}

		@Override
		public MapCodec< SoulColor > type() {
			return MAP_CODEC;
		}
	}
}
