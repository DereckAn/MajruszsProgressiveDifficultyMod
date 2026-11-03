package io.github.flemmli97.improvedmobs.fabric.client;

import io.github.flemmli97.improvedmobs.client.ClientCalls;
import io.github.flemmli97.improvedmobs.client.ClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import io.github.flemmli97.improvedmobs.ImprovedMobs;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class ImprovedMobsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(ImprovedMobs.modRes("difficulty"), (graphics, deltaTracker) -> ClientEvents.displayDifficulty(graphics));
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> ClientCalls.disconnect()));
    }
}
