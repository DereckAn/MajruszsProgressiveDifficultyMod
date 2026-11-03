package io.github.flemmli97.improvedmobs.mixin;

import io.github.flemmli97.improvedmobs.common.entities.ServersideRegister;
import io.github.flemmli97.improvedmobs.common.utils.EntityFlags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {

    @Inject(method = "create(Lnet/minecraft/world/level/storage/ValueInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnRequest;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private static void injectServerEntityOnLoad(ValueInput input, Level level, EntitySpawnRequest request, CallbackInfoReturnable<Optional<Entity>> info) {
        CompoundTag data = input.read(EntityFlags.TAG_ID, CompoundTag.CODEC).orElseGet(CompoundTag::new);
        if (data.contains(EntityFlags.SERVER_ENTITY_TAG_ID)) {
            Identifier id = Identifier.parse(data.getStringOr(EntityFlags.SERVER_ENTITY_TAG_ID, ""));
            Optional<Entity> opt = ServersideRegister.createOf(id, level, input);
            if (opt.isPresent())
                info.setReturnValue(opt);
        }
    }
}
