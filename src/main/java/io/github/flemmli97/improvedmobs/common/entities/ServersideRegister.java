package io.github.flemmli97.improvedmobs.common.entities;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ServersideRegister {

    private static final Map<Identifier, Function<Level, Entity>> ENTITIES = new HashMap<>();

    public static Optional<Entity> createOf(Identifier id, Level level, ValueInput input) {
        Function<Level, Entity> f = ENTITIES.get(id);
        if (f != null) {
            Entity e = f.apply(level);
            e.load(input);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    static {
        ENTITIES.put(FlyingSummonEntity.SUMMONED_FLYING_ID, FlyingSummonEntity::new);
        ENTITIES.put(AquaticSummonEntity.SUMMONED_AQUATIC_ID, AquaticSummonEntity::new);
    }
}
