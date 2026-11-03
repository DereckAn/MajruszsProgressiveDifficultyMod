package io.github.flemmli97.improvedmobs.mixinhelper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public interface NodeEvaluatorExtension {

    boolean improvedMobs$canBreakBlocks();

    default Identifier improvedMobs$pathTypeOf(BlockPos pos, Identifier... only) {
        return null;
    }
}
