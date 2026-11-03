package io.github.flemmli97.improvedmobs.fabric.mixin;

import com.mojang.authlib.GameProfile;
import io.github.flemmli97.improvedmobs.ImprovedMobs;
import io.github.flemmli97.improvedmobs.common.registry.ImprovedMobsAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import com.mojang.serialization.Codec;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    private ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    // Legacy handling
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void loadData(ValueInput input, CallbackInfo info) {
        CompoundTag data = input.read(ImprovedMobs.MODID + ":difficulty_data", CompoundTag.CODEC).orElse(null);
        Optional<Double> rootDifficulty = input.read("IMDifficulty", Codec.DOUBLE);
        if (rootDifficulty.isPresent()) {
            data = new CompoundTag();
            data.putDouble("IMDifficulty", rootDifficulty.get());
            data.putInt("DifficultyIndex", input.getIntOr("DifficultyIndex", 0));
            data.putBoolean("Paused", input.getBooleanOr("Paused", false));
        }
        if (data != null) {
            ImprovedMobsAttachments.PLAYER_DIFFICULTY.get().get(this).read(data, this.registryAccess());
        }
    }
}
