package com.majruszsdifficulty.blocks;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.redstone.Orientation;
import com.majruszlibrary.events.OnBlockPlaced;
import com.majruszlibrary.events.base.Condition;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public class InfernalSponge extends Block {
	static {
		OnBlockPlaced.listen( InfernalSponge::absorb )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.blockState.is( MajruszsDifficulty.INFERNAL_SPONGE_BLOCK.get() ) );
	}

	public InfernalSponge( Properties properties ) {
		super( properties.mapColor( MapColor.NETHER ).strength( 0.6f ).sound( SoundType.GRASS ) );
	}

	@Override
	protected void neighborChanged( BlockState blockState, Level level, BlockPos blockPos, Block neighbor, @Nullable Orientation orientation, boolean p_56806_ ) {
		InfernalSponge.absorb( level, blockPos );

		super.neighborChanged( blockState, level, blockPos, neighbor, orientation, p_56806_ );
	}

	private static void absorb( OnBlockPlaced data ) {
		InfernalSponge.absorb( data.getServerLevel(), data.position );
	}

	private static void absorb( Level level, BlockPos spongePos ) {
		int absorbedBlocks = BlockPos.breadthFirstTraversal( spongePos, 6, 65, ( blockPos, consumer )->{
			for( Direction direction : Direction.values() ) {
				consumer.accept( blockPos.relative( direction ) );
			}
		}, blockPos->{
			if( blockPos.equals( spongePos ) ) {
				return BlockPos.TraversalNodeStatus.ACCEPT;
			}

			BlockState blockState = level.getBlockState( blockPos );
			FluidState fluidState = level.getFluidState( blockPos );
			if( !fluidState.is( Fluids.LAVA ) && !fluidState.is( Fluids.FLOWING_LAVA ) ) {
				return BlockPos.TraversalNodeStatus.SKIP;
			}
			if( blockState.getBlock() instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock( null, level, blockPos, blockState ).isEmpty() ) {
				return BlockPos.TraversalNodeStatus.ACCEPT;
			}

			if( blockState.getBlock() instanceof LiquidBlock ) {
				level.setBlock( blockPos, Blocks.AIR.defaultBlockState(), 3 );
			} else {
				BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity( blockPos ) : null;
				dropResources( blockState, level, blockPos, blockentity );
				level.setBlock( blockPos, Blocks.AIR.defaultBlockState(), 3 );
			}

			return BlockPos.TraversalNodeStatus.ACCEPT;
		} ) - 1;

		if( absorbedBlocks > 0 ) {
			level.setBlock( spongePos, MajruszsDifficulty.SOAKED_INFERNAL_SPONGE_BLOCK.get().defaultBlockState(), 2 );
		}
	}

	public static class Item extends BlockItem {
		public Item( Properties properties ) {
			super( MajruszsDifficulty.INFERNAL_SPONGE_BLOCK.get(), properties.useBlockDescriptionPrefix().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}
}
