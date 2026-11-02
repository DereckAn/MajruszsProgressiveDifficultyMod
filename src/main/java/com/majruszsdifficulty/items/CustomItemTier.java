package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;

// 26.x: Tier became the ToolMaterial record; harvest level 3 = diamond, 4 = netherite; repair items are tags
public interface CustomItemTier {
	ToolMaterial WITHER = new ToolMaterial( BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 360, 5.0f, 3.0f, 15,
		TagKey.create( Registries.ITEM, MajruszsDifficulty.HELPER.getLocation( "wither_tool_materials" ) ) );
	ToolMaterial ENDERIUM = new ToolMaterial( BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2137, 10.0f, 5.0f, 15,
		TagKey.create( Registries.ITEM, MajruszsDifficulty.HELPER.getLocation( "enderium_tool_materials" ) ) );
}
