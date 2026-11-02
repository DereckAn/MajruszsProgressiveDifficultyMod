package com.majruszsdifficulty.items;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.events.OnItemInventoryClicked;
import com.majruszlibrary.events.OnPlayerInteracted;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.function.Supplier;

public class TreasureBag extends Item {
	private final Identifier lootId;

	static {
		OnPlayerInteracted.listen( TreasureBag::openInHand )
			.addCondition( data->data.itemStack.getItem() instanceof TreasureBag );

		Serializables.get( RightClickAction.class )
			.define( "container_idx", Reader.integer(), s->s.containerIdx, ( s, v )->s.containerIdx = v );

		MajruszsDifficulty.TREASURE_BAG_RIGHT_CLICK_NETWORK.addServerCallback( TreasureBag::openInInventory );
	}

	public static Function< Properties, TreasureBag > angler() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_angler" ) );
	}

	public static Function< Properties, TreasureBag > elderGuardian() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_elder_guardian" ) );
	}

	public static Function< Properties, TreasureBag > enderDragon() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_ender_dragon" ) );
	}

	public static Function< Properties, TreasureBag > pillager() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_pillager" ) );
	}

	public static Function< Properties, TreasureBag > undeadArmy() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_undead_army" ) );
	}

	public static Function< Properties, TreasureBag > warden() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_warden" ) );
	}

	public static Function< Properties, TreasureBag > wither() {
		return properties->new TreasureBag( properties, MajruszsDifficulty.HELPER.getLocation( "gameplay/treasure_bag_wither" ) );
	}

	public TreasureBag( Properties properties, Identifier lootId ) {
		super( properties.stacksTo( 16 ).rarity( Rarity.UNCOMMON ) );

		this.lootId = lootId;
	}

	public Identifier getLootId() {
		return this.lootId;
	}

	private static void openInHand( OnPlayerInteracted data ) {
		if( Side.isLogicalServer() ) {
			TreasureBag.open( data.itemStack, data.player );
		}

		data.finish();
	}

	private static void openInInventory( RightClickAction action, ServerPlayer player ) {
		// delayed on purpose to avoid accessing randoms from 'network' thread
		TimeHelper.nextTick( d->{
			if( !player.containerMenu.isValidSlotIndex( action.containerIdx ) ) {
				return;
			}

			ItemStack itemStack = player.containerMenu.getSlot( action.containerIdx ).getItem();
			if( itemStack.getItem() instanceof TreasureBag ) {
				TreasureBag.open( itemStack, player );
			}
		} );
	}

	private static void open( ItemStack itemStack, Player player ) {
		TreasureBag treasureBag = ( TreasureBag )itemStack.getItem();
		List< ItemStack > loot = LootHelper.getLootTable( treasureBag.lootId ).getRandomItems( LootHelper.toGiftParams( player ) );
		SoundEmitter.of( SoundEvents.ITEM_PICKUP )
			.position( player.position() )
			.emit( player.level() );
		Events.dispatch( new OnTreasureBagOpened( player, treasureBag, loot ) );
		loot.forEach( reward->ItemHelper.giveToPlayer( reward, player ) );
		ItemHelper.consumeItemOnUse( itemStack, player );
	}

	public static class RightClickAction {
		public int containerIdx;

		public RightClickAction( int containerIdx ) {
			this.containerIdx = containerIdx;
		}

		public RightClickAction() {
			this( -1 );
		}
	}

	@Environment( EnvType.CLIENT )
	public static class Client {
		static {
			OnItemInventoryClicked.listen( Client::openInInventory )
				.addCondition( data->data.itemStack.getItem() instanceof TreasureBag )
				.addCondition( data->data.action == 1 );
		}

		private static void openInInventory( OnItemInventoryClicked data ) {
			MajruszsDifficulty.TREASURE_BAG_RIGHT_CLICK_NETWORK.sendToServer( new RightClickAction( data.containerIdx ) );
			data.cancel();
		}
	}
}
