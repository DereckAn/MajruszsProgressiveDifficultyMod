package io.github.flemmli97.improvedmobs.common.config.tags;

import io.github.flemmli97.improvedmobs.ImprovedMobs;
import io.github.flemmli97.tenshilib.loader.event.TagModifyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DynamicTags {

    public static TagKey<EntityType<?>> HOSTILE_MOBS = TagKey.create(Registries.ENTITY_TYPE, ImprovedMobs.modRes("hostile_mobs"));
    public static TagKey<EntityType<?>> NON_HOSTILE_MOBS = TagKey.create(Registries.ENTITY_TYPE, ImprovedMobs.modRes("non_hostile_mobs"));
    public static TagKey<EntityType<?>> NEUTRAL_MOBS = TagKey.create(Registries.ENTITY_TYPE, ImprovedMobs.modRes("neutral_mobs"));
    public static TagKey<EntityType<?>> ANIMAL_MOBS = TagKey.create(Registries.ENTITY_TYPE, ImprovedMobs.modRes("animal_mobs"));

    /**
     * Since 26.1 entity type tags load before the dynamic registries exist, so entities can no longer be
     * instantiated here to inspect their class. Classify by spawn category instead.
     * ponytail: NEUTRAL_MOBS can't be derived from the category and is left for datapacks to fill.
     */
    public static void init() {
        TagModifyEvent.INSTANCE.registerListener(Registries.ENTITY_TYPE, handler ->
                BuiltInRegistries.ENTITY_TYPE.listElements().forEach(type -> {
                    MobCategory category = type.value().getCategory();
                    if (category == MobCategory.MONSTER) {
                        handler.add(HOSTILE_MOBS, type);
                    } else {
                        handler.add(NON_HOSTILE_MOBS, type);
                    }
                    if (category == MobCategory.CREATURE || category == MobCategory.AXOLOTLS) {
                        handler.add(ANIMAL_MOBS, type);
                    }
                }));
    }
}
