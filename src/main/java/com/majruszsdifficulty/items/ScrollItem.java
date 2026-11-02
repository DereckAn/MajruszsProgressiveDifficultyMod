package com.majruszsdifficulty.items;

import net.minecraft.world.item.ItemUseAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class ScrollItem extends Item {
	protected static final int MIN_DURATION = TimeHelper.toTicks( 1.0 );

	public ScrollItem( Properties properties ) {
		super( properties.rarity( Rarity.UNCOMMON ).stacksTo( 1 ) );
	}

	@Override
	public InteractionResult use( Level level, Player player, InteractionHand hand ) {
		this.playSound( this::getPrepareSound, level, player, 1.0f );

		return ItemUtils.startUsingInstantly( level, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity entity ) {
		this.useScroll( itemStack, level, entity, 1.0f );

		return itemStack;
	}

	@Override
	public boolean releaseUsing( ItemStack itemStack, Level level, LivingEntity entity, int ticksLeft ) {
		if( ( this.getUseDuration( itemStack, entity ) - ticksLeft ) < MIN_DURATION ) {
			this.disableItem( itemStack, entity, TimeHelper.toTicks( 1.0 ) );
			return false;
		}

		float useRatio = Mth.clamp( 1.0f - ( float )ticksLeft / ( this.getUseDuration( itemStack, entity ) - MIN_DURATION ), 0.0f, 1.0f );
		this.useScroll( itemStack, level, entity, useRatio );

		return true;
	}

	@Override
	public ItemUseAnimation getUseAnimation( ItemStack itemStack ) {
		return ItemUseAnimation.NONE;
	}

	@Override
	public int getUseDuration( ItemStack itemStack, LivingEntity user ) {
		return TimeHelper.toTicks( 3.0 );
	}

	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		this.disableItem( itemStack, entity, TimeHelper.toTicks( 16.0 ) );
		this.playSound( this::getCastSound, level, entity, 2.0f );
	}

	protected void disableItem( ItemStack itemStack, LivingEntity entity, int ticks ) {
		if( entity instanceof Player player ) {
			ItemHelper.addCooldown( player, ticks, itemStack.getItem() );
		}
	}

	protected void playSound( Supplier< SoundEvent > sound, Level level, LivingEntity entity, float volumeScale ) {
		SoundEmitter.of( sound )
			.source( SoundSource.PLAYERS )
			.volume( SoundEmitter.randomized( volumeScale ) )
			.position( entity.position() )
			.emit( level );
	}

	protected abstract SoundEvent getPrepareSound();

	protected abstract SoundEvent getCastSound();

	// TODO(step 4): ScrollItem.Client (arm pose + first-person bob while casting) needs the 26.x humanoid render state;
	// the 1.20.1 code is in deferred/step4/ScrollItemClient.java
}
