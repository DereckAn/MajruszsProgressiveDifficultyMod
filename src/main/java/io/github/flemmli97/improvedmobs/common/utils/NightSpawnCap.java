package io.github.flemmli97.improvedmobs.common.utils;

import io.github.flemmli97.improvedmobs.api.difficulty.DifficultyFetcher;
import io.github.flemmli97.improvedmobs.common.config.Config;
import io.github.flemmli97.tenshilib.common.utils.math.parser.VariableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;

/**
 * Raises the hostile mob cap at night based on difficulty
 */
public class NightSpawnCap {

    // ponytail: one static value is enough since natural spawning ticks one level at a time on the server thread
    private static double multiplier = 1;

    public static void update(ServerLevel level) {
        multiplier = 1;
        if (!level.isDarkOutside())
            return;
        ServerPlayer hardest = null;
        double difficulty = 0;
        for (ServerPlayer player : level.players()) {
            double playerDifficulty = DifficultyFetcher.getDifficulty(level, player.position());
            if (hardest == null || playerDifficulty > difficulty) {
                hardest = player;
                difficulty = playerDifficulty;
            }
        }
        if (hardest != null) {
            VariableMap map = Config.apply(new VariableMap(), level.getRandom(), level.getRespawnData().pos(), hardest.position(), difficulty);
            multiplier = Math.max(1, Config.CommonConfig.nightSpawnMultiplier.get(map));
        }
    }

    public static int apply(MobCategory category, int max) {
        return category == MobCategory.MONSTER ? (int) (max * multiplier) : max;
    }
}
