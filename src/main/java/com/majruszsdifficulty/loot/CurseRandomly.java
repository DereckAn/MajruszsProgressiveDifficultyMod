package com.majruszsdifficulty.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

// 26.x: loot functions are codec-based; enchantments come from the dynamic registry of the loot context's level.
// "Discoverable" became the on_random_loot enchantment tag, "isCurse" the curse tag.
public class CurseRandomly extends LootItemConditionalFunction {
	public static final MapCodec< CurseRandomly > CODEC = RecordCodecBuilder.mapCodec( instance->commonFields( instance ).apply( instance, CurseRandomly::new ) );

	public CurseRandomly( Optional< Holder< LootItemCondition > > condition ) {
		super( condition );
	}

	@Override
	public MapCodec< CurseRandomly > codec() {
		return CODEC;
	}

	@Override
	public ItemStack run( ItemStack itemStack, LootContext context ) {
		List< Holder.Reference< Enchantment > > curses = this.generate( itemStack, context, true );
		if( !curses.isEmpty() ) {
			itemStack = CurseRandomly.enchantItem( itemStack, context.getRandom(), curses );
		}

		List< Holder.Reference< Enchantment > > enchantments = this.generate( itemStack, context, false );
		if( !enchantments.isEmpty() ) {
			itemStack = CurseRandomly.enchantItem( itemStack, context.getRandom(), enchantments );
		}

		return itemStack;
	}

	private List< Holder.Reference< Enchantment > > generate( ItemStack itemStack, LootContext context, boolean isCurse ) {
		return context.getLevel()
			.registryAccess()
			.lookupOrThrow( Registries.ENCHANTMENT )
			.listElements()
			.filter( enchantment->enchantment.is( EnchantmentTags.ON_RANDOM_LOOT ) )
			.filter( enchantment->enchantment.is( EnchantmentTags.CURSE ) == isCurse )
			.filter( enchantment->itemStack.is( Items.BOOK ) || enchantment.value().canEnchant( itemStack ) )
			.toList();
	}

	private static ItemStack enchantItem( ItemStack itemStack, RandomSource randomSource, List< Holder.Reference< Enchantment > > enchantments ) {
		Holder< Enchantment > enchantment = enchantments.get( randomSource.nextInt( enchantments.size() ) );
		int level = Mth.nextInt( randomSource, enchantment.value().getMinLevel(), enchantment.value().getMaxLevel() );
		if( itemStack.is( Items.BOOK ) ) {
			itemStack = new ItemStack( Items.ENCHANTED_BOOK );
		}
		itemStack.enchant( enchantment, level );

		return itemStack;
	}
}
