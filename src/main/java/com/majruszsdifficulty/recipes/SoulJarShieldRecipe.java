package com.majruszsdifficulty.recipes;

import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.SoulJar;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.MapCodec;
import com.majruszlibrary.item.ItemHelper;
import net.minecraft.world.level.Level;


// 26.x: special recipes are codec-based singletons and read a CraftingInput
public class SoulJarShieldRecipe extends CustomRecipe {
	public static final SoulJarShieldRecipe INSTANCE = new SoulJarShieldRecipe();
	public static final RecipeSerializer< SoulJarShieldRecipe > SERIALIZER = new RecipeSerializer<>( MapCodec.unit( INSTANCE ), StreamCodec.unit( INSTANCE ) );

	@Override
	public boolean matches( CraftingInput container, Level level ) {
		RecipeData recipeData = SoulJarShieldRecipe.convert( container );

		return !recipeData.shield.isEmpty() && !recipeData.soulJar.isEmpty();
	}

	@Override
	public ItemStack assemble( CraftingInput container ) {
		RecipeData recipeData = SoulJarShieldRecipe.convert( container );
		ItemStack shield = recipeData.shield.copy();
		ItemHelper.updateCustomTag( shield, tag->Serializables.write( SoulJar.BonusInfo.read( recipeData.soulJar ), tag ) );

		return shield;
	}

	@Override
	public RecipeSerializer< SoulJarShieldRecipe > getSerializer() {
		return SERIALIZER;
	}

	private static RecipeData convert( CraftingInput container ) {
		ItemStack soulJar = ItemStack.EMPTY;
		ItemStack shield = ItemStack.EMPTY;
		for( int i = 0; i < container.size(); ++i ) {
			ItemStack itemStack = container.getItem( i );
			if( itemStack.isEmpty() ) {
				continue;
			}

			if( itemStack.getItem() instanceof SoulJar && soulJar.isEmpty() ) {
				soulJar = itemStack;
			} else if( itemStack.getItem() instanceof ShieldItem && shield.isEmpty() ) {
				shield = itemStack;
			} else {
				return new RecipeData( ItemStack.EMPTY, ItemStack.EMPTY );
			}
		}

		return new RecipeData( soulJar, shield );
	}

	record RecipeData( ItemStack soulJar, ItemStack shield ) {}
}
