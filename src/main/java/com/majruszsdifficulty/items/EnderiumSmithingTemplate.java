package com.majruszsdifficulty.items;

import com.majruszlibrary.events.OnLootGenerated;
import com.majruszlibrary.item.LootHelper;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public class EnderiumSmithingTemplate extends SmithingTemplateItem {
	private static final Identifier ID = MajruszsDifficulty.HELPER.getLocation( "gameplay/enderium_upgrade_smithing_template" );

	static {
		OnLootGenerated.listen( EnderiumSmithingTemplate::addToChest )
			.addCondition( data->data.lootId.equals( BuiltInLootTables.END_CITY_TREASURE ) );
	}

	public EnderiumSmithingTemplate( Properties properties ) {
		super(
			TextHelper.translatable( "majruszsdifficulty.smithing.enderium.applies_to" ).withStyle( ChatFormatting.BLUE ),
			TextHelper.translatable( "majruszsdifficulty.smithing.enderium.ingredients" ).withStyle( ChatFormatting.BLUE ),
			TextHelper.translatable( "majruszsdifficulty.smithing.enderium.base_slot" ).withStyle( ChatFormatting.GRAY ),
			TextHelper.translatable( "majruszsdifficulty.smithing.enderium.additions_slot" ).withStyle( ChatFormatting.GRAY ),
			List.of(
				Identifier.withDefaultNamespace( "container/slot/helmet" ),
				Identifier.withDefaultNamespace( "container/slot/sword" ),
				Identifier.withDefaultNamespace( "container/slot/chestplate" ),
				Identifier.withDefaultNamespace( "container/slot/pickaxe" ),
				Identifier.withDefaultNamespace( "container/slot/leggings" ),
				Identifier.withDefaultNamespace( "container/slot/axe" ),
				Identifier.withDefaultNamespace( "container/slot/boots" ),
				Identifier.withDefaultNamespace( "container/slot/hoe" ),
				Identifier.withDefaultNamespace( "container/slot/shovel" )
			),
			List.of(
				Identifier.withDefaultNamespace( "container/slot/ingot" )
			),
			properties
		);
	}

	private static void addToChest( OnLootGenerated data ) {
		LootHelper.getLootTable( ID )
			.getRandomItems( LootHelper.toGiftParams( data.entity ) ) // tochestparams
			.forEach( data.generatedLoot::add );
	}
}
