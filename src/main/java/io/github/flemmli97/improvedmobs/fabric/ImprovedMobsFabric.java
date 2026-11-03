package io.github.flemmli97.improvedmobs.fabric;

import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import io.github.flemmli97.improvedmobs.ImprovedMobs;
import io.github.flemmli97.improvedmobs.api.datapack.EntityOverridesManager;
import io.github.flemmli97.improvedmobs.api.datapack.ItemUseLookupManager;
import io.github.flemmli97.improvedmobs.api.difficulty.DifficultyFetcher;
import io.github.flemmli97.improvedmobs.api.item.ItemUseRegistry;
import io.github.flemmli97.improvedmobs.common.commands.ImprovedMobsCommand;
import io.github.flemmli97.improvedmobs.common.config.holder.ConfigLoader;
import io.github.flemmli97.improvedmobs.common.config.holder.ConfigSpecs;
import io.github.flemmli97.improvedmobs.common.config.tags.DynamicTags;
import io.github.flemmli97.improvedmobs.common.datapack.DifficultyAttributeConfig;
import io.github.flemmli97.improvedmobs.common.events.EventCalls;
import io.github.flemmli97.improvedmobs.common.network.S2CDiffcultyValue;
import io.github.flemmli97.improvedmobs.common.network.S2CShowDifficulty;
import io.github.flemmli97.improvedmobs.common.registry.ImprovedMobsAttachments;
import io.github.flemmli97.improvedmobs.fabric.events.EventHandler;
import io.github.flemmli97.tenshilib.fabric.loader.events.CommonSetupEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.fml.config.ModConfig;


public class ImprovedMobsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ImprovedMobsAttachments.ATTACHMENTS.registerContent();
        ItemUseRegistry.initBuiltin();
        DynamicTags.init();
        ServerTickEvents.END_LEVEL_TICK.register(EventCalls::tick);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> ImprovedMobsCommand.register(dispatcher));
        ServerEntityEvents.ENTITY_LOAD.register(EventHandler::onEntityLoad);
        UseBlockCallback.EVENT.register(EventHandler::openTile);
        UseEntityCallback.EVENT.register(EventHandler::equipPet);
        ServerPlayConnectionEvents.JOIN.register(EventHandler::worldJoin);
        CommonLifecycleEvents.TAGS_LOADED.register((reg, client) -> {
            if (!client) {
                if (ItemUseLookupManager.getInstance() != null)
                    ItemUseLookupManager.getInstance().onReloadFinished();
                EventCalls.onTagReloaded();
            }
        });

        registerPacket();
        ModConfigEvents.loading(ImprovedMobs.MODID).register(config -> {
            if (config.getSpec() == ConfigSpecs.CLIENT_SPEC)
                ConfigLoader.loadClient();
            if (config.getSpec() == ConfigSpecs.COMMON_SPEC)
                ConfigLoader.loadCommon();
        });
        ModConfigEvents.reloading(ImprovedMobs.MODID).register(config -> {
            if (config.getSpec() == ConfigSpecs.CLIENT_SPEC)
                ConfigLoader.loadClient();
            if (config.getSpec() == ConfigSpecs.COMMON_SPEC)
                ConfigLoader.loadCommon();
        });
        ConfigRegistry.INSTANCE.register(ImprovedMobs.MODID, ModConfig.Type.CLIENT, ConfigSpecs.CLIENT_SPEC, "improvedmobs/client.toml");
        ConfigRegistry.INSTANCE.register(ImprovedMobs.MODID, ModConfig.Type.COMMON, ConfigSpecs.COMMON_SPEC, "improvedmobs/common.toml");
        DifficultyFetcher.register();

        DataResourceLoader.get().registerReloadListener(DifficultyAttributeConfig.ID, DifficultyAttributeConfig::create);
        DataResourceLoader.get().registerReloadListener(EntityOverridesManager.ID, EntityOverridesManager::create);
        // Defer this so this gets reloaded with lower priority... prob?
        CommonSetupEvent.EVENT.register(l -> DataResourceLoader.get().registerReloadListener(ItemUseLookupManager.ID, ItemUseLookupManager::create));
    }

    public static void registerPacket() {
        PayloadTypeRegistry.clientboundPlay().register(S2CDiffcultyValue.TYPE, S2CDiffcultyValue.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(S2CShowDifficulty.TYPE, S2CShowDifficulty.STREAM_CODEC);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(S2CDiffcultyValue.TYPE, (pkt, ctx) -> S2CDiffcultyValue.handle(pkt));
            ClientPlayNetworking.registerGlobalReceiver(S2CShowDifficulty.TYPE, (pkt, ctx) -> S2CShowDifficulty.handle(pkt));
        }
    }
}
