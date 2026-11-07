package com.majruszsdifficulty.items;

import com.majruszlibrary.events.OnItemTooltip;
import com.majruszlibrary.events.OnPlayerInteracted;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.data.MergeConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import com.majruszsdifficulty.undeadarmy.listeners.DifficultyMilestoneTrigger;
import io.github.flemmli97.improvedmobs.common.difficulty.DifficultyData;
import io.github.flemmli97.improvedmobs.common.registry.ImprovedMobsAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

// Step 7: stops the nearby Undead Army, or (with none nearby) postpones the next one by `totem_postpone_levels`
// Improved Mobs difficulty levels. Costs `totem_difficulty_cost` Improved Mobs difficulty while its scaling is on.
public class BanishingTotem extends Item {
	static {
		OnPlayerInteracted.listen( BanishingTotem::use )
			.addCondition( data->data.itemStack.getItem() instanceof BanishingTotem );

		OnItemTooltip.listen( BanishingTotem::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof BanishingTotem );
	}

	public BanishingTotem( Properties properties ) {
		super( properties.stacksTo( 1 ).rarity( Rarity.EPIC ) );
	}

	private static void use( OnPlayerInteracted data ) {
		if( Side.isLogicalServer() && data.player instanceof ServerPlayer player ) {
			UndeadArmy army = UndeadArmyHelper.findNearestUndeadArmy( player.blockPosition() );
			if( army != null ) {
				army.banish();
				player.sendSystemMessage( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.banished" ).withStyle( ChatFormatting.DARK_PURPLE ) );
			} else {
				DifficultyMilestoneTrigger.postpone( player, UndeadArmyConfig.TOTEM_POSTPONE_LEVELS );
				player.sendSystemMessage( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.postponed", UndeadArmyConfig.TOTEM_POSTPONE_LEVELS ).withStyle( ChatFormatting.DARK_PURPLE ) );
			}
			if( MergeConfig.isImprovedMobsScalingOn() && UndeadArmyConfig.TOTEM_DIFFICULTY_COST > 0 ) {
				BanishingTotem.raiseDifficulty( player, UndeadArmyConfig.TOTEM_DIFFICULTY_COST );
			}

			ServerLevel level = player.level();
			level.playSound( null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 0.8f );
			level.sendParticles( ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY( 0.5 ), player.getZ(), 60, 0.6, 0.8, 0.6, 0.4 );
			ItemHelper.consumeItemOnUse( data.itemStack, player );
		}

		data.finish();
	}

	/** Same as `/improvedmobs difficulty add` plus `... player <name> add`, so it works for every difficulty type. */
	private static void raiseDifficulty( ServerPlayer player, int amount ) {
		DifficultyData global = DifficultyData.get( player.level().getServer() );
		global.addDifficulty( amount, player.level().getServer() );
		var playerData = ImprovedMobsAttachments.PLAYER_DIFFICULTY.get().get( player );
		playerData.setDifficultyLevel( playerData.getDifficultyLevel() + amount );
		player.sendSystemMessage( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.cost", amount ).withStyle( ChatFormatting.RED ) );
	}

	private static void addTooltip( OnItemTooltip data ) {
		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.item_tooltip1" ).withStyle( ChatFormatting.GRAY ) );
		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.item_tooltip2", UndeadArmyConfig.TOTEM_POSTPONE_LEVELS ).withStyle( ChatFormatting.GRAY ) );
		if( MergeConfig.isImprovedMobsScalingOn() && UndeadArmyConfig.TOTEM_DIFFICULTY_COST > 0 ) {
			data.components.add( TextHelper.translatable( "item.majruszsdifficulty.banishing_totem.item_tooltip3", UndeadArmyConfig.TOTEM_DIFFICULTY_COST ).withStyle( ChatFormatting.RED ) );
		}
	}
}
