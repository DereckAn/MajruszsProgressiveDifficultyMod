package io.github.flemmli97.improvedmobs.common.config.equipment;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

/**
 * Hook for scoring items from other mods. The Supplementaries and TACZ integrations lived here
 * but were removed for 26.3 since neither mod is available for it yet.
 */
public class OtherScores {

    public static ItemScore score(Item item, double durability, double damage, double armor, double toughness, double knockbackResistance, int enchantmentValue) {
        return null;
    }

    public static void addItems(Map<EquipmentSlot, List<Pair<ItemScore, OptionalItemStack>>> scores, HolderLookup.Provider provider) {
    }
}
