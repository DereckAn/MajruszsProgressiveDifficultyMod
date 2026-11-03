package io.github.flemmli97.improvedmobs.fabric.mixin;

import java.util.Optional;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import io.github.flemmli97.improvedmobs.ImprovedMobs;
import io.github.flemmli97.improvedmobs.common.registry.ImprovedMobsAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    // Legacy handling
    @Inject(method = "loadAdditional", at = @At(value = "HEAD"))
    private void loadData(ValueInput input, CallbackInfo info) {
        CompoundTag data = input.read(ImprovedMobs.MODID + ":container", CompoundTag.CODEC).orElse(null);
        Optional<Boolean> rootOpened = input.read("IMHasBeenOpened", Codec.BOOL);
        if (rootOpened.isPresent()) {
            data = new CompoundTag();
            data.putBoolean("IMHasBeenOpened", rootOpened.get());
        }
        if (data != null) {
            ImprovedMobsAttachments.HAS_BEEN_OPENED.get().get(this).read(data, input.lookup());
        }
    }
}
