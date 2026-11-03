package io.github.flemmli97.improvedmobs.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.flemmli97.improvedmobs.common.events.EventCalls;
import io.github.flemmli97.improvedmobs.fabric.events.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerExplosion.class)
public abstract class ExplosionMixin {

    @Final
    @Shadow
    private Entity source;

    @ModifyExpressionValue(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> removeUnaffected(List<Entity> list) {
        return EventHandler.explosion((ServerExplosion) (Object) this, this.source, new ArrayList<>(list));
    }

    @ModifyExpressionValue(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;calculateExplodedPositions()Ljava/util/List;"))
    private List<BlockPos> removeBlocks(List<BlockPos> toBlow) {
        List<BlockPos> list = new ArrayList<>(toBlow);
        EventCalls.explosionBlocks(this.source, list);
        return list;
    }
}
