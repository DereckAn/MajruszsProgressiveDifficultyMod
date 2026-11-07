package com.majruszsdifficulty.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

// Step 7: extra pools (Totem of Banishing, enchanted books VI-VIII) for vanilla chests, from majruszsdifficulty/extra_chest_loot.json.
// The pools are JSON because 26.x loot builders need registry holders; LootPool.CODEC with the event's registries handles that.
public class ExtraChestLoot {
	public static void register() {
		JsonObject pools;
		try( var reader = new InputStreamReader( Objects.requireNonNull( ExtraChestLoot.class.getResourceAsStream( "/majruszsdifficulty/extra_chest_loot.json" ) ), StandardCharsets.UTF_8 ) ) {
			pools = JsonParser.parseReader( reader ).getAsJsonObject();
		} catch( Exception exception ) {
			throw new IllegalStateException( "Missing extra_chest_loot.json", exception );
		}

		LootTableEvents.MODIFY.register( ( key, builder, source, registries )->{
			if( !source.isBuiltin() || !pools.has( key.identifier().toString() ) ) {
				return;
			}
			for( JsonElement pool : pools.getAsJsonArray( key.identifier().toString() ) ) {
				LootPool.CODEC.parse( registries.createSerializationContext( JsonOps.INSTANCE ), pool )
					.resultOrPartial( error->MajruszsDifficulty.HELPER.logError( "Extra chest loot for %s: %s", key.identifier(), error ) )
					.ifPresent( builder::pool );
			}
		} );
	}
}
