package io.github.flemmli97.improvedmobs.common.utils;

import io.github.flemmli97.improvedmobs.common.config.Config;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import com.mojang.serialization.Codec;
import io.github.flemmli97.improvedmobs.ImprovedMobs;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockRestorationData extends SavedData {

    // ponytail: wraps the old NBT load/save in a codec. Save files from 1.21.1 used a different name and are not migrated
    private static final Codec<BlockRestorationData> CODEC = CompoundTag.CODEC.xmap(tag -> new BlockRestorationData(tag, null), data -> data.save(new CompoundTag(), null));
    private static final SavedDataType<BlockRestorationData> TYPE = new SavedDataType<>(ImprovedMobs.modRes("restoration"), BlockRestorationData::new, CODEC, DataFixTypes.LEVEL);

    private final Long2ObjectMap<Long2ObjectMap<SavedBlock>> toRestore = new Long2ObjectOpenHashMap<>();

    public BlockRestorationData() {
    }

    private BlockRestorationData(CompoundTag tag, HolderLookup.Provider provider) {
        this.load(tag);
    }

    public static BlockRestorationData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void restore(ServerLevel level, BlockState state, BlockPos pos, @Nullable Entity entity) {
        Long2ObjectMap<SavedBlock> chunk = this.toRestore.computeIfAbsent(ChunkPos.pack(pos), o -> new Long2ObjectOpenHashMap<>());
        SavedBlock current = chunk.get(pos.asLong());
        if (current != null) {
            current.toDrop.forEach(stack -> Block.popResource(level, pos, stack));
        }
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, entity, ItemStack.EMPTY);
        chunk.put(pos.asLong(), new SavedBlock(state, level.getGameTime(), drops));
        this.setDirty();
    }

    public void tick(ServerLevel level) {
        LongSet toRemove = new LongArraySet();
        long time = level.getGameTime();
        boolean particle = time % 5 == 0;
        this.toRestore.forEach((chunk, data) -> {
            ChunkPos chunkPos = ChunkPos.unpack(chunk);
            if (level.hasChunk(chunkPos.x(), chunkPos.z())) {
                LongSet toRemoveBlocks = new LongArraySet();
                LevelChunk levelChunk = level.getChunk(chunkPos.x(), chunkPos.z());
                data.forEach((packedPos, replace) -> {
                    BlockPos pos = BlockPos.of(packedPos);
                    BlockState current = levelChunk.getBlockState(pos);
                    if (current.getBlock() == Blocks.AIR) {
                        if (Math.abs(time - replace.time) > Config.CommonConfig.restoreDelay) {
                            level.setBlock(pos, replace.state, Block.UPDATE_ALL);
                            toRemoveBlocks.add(packedPos);
                        } else if (particle)
                            level.sendParticles(DustParticleOptions.REDSTONE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0, 0, 0);
                    } else {
                        replace.toDrop.forEach(stack -> Block.popResource(level, pos, stack));
                        toRemoveBlocks.add(packedPos);
                    }
                });
                toRemoveBlocks.forEach(data::remove);
                if (data.isEmpty())
                    toRemove.add(chunk);
                if (!toRemoveBlocks.isEmpty())
                    this.setDirty();
            }
        });
        toRemove.forEach(this.toRestore::remove);
    }

    public void load(CompoundTag data) {
        data.keySet().forEach((chunk) -> {
            Long2ObjectMap<SavedBlock> blocksMap = new Long2ObjectOpenHashMap<>();
            ListTag blocks = data.getListOrEmpty(chunk);
            blocks.forEach(t -> {
                CompoundTag blockSave = (CompoundTag) t;
                List<ItemStack> stacks = new ArrayList<>();
                blockSave.getListOrEmpty("Drops")
                        .forEach(st -> stacks.add(ItemStack.CODEC.parse(NbtOps.INSTANCE, st).getOrThrow()));
                blocksMap.put(blockSave.getLongOr("Pos", 0L),
                        new SavedBlock(BlockState.CODEC.parse(NbtOps.INSTANCE, blockSave.get("State")).getOrThrow(), blockSave.getLongOr("Time", 0L), stacks));
            });
            this.toRestore.put(Long.parseLong(chunk), blocksMap);
        });
    }

    public CompoundTag save(CompoundTag data, HolderLookup.Provider provider) {
        this.toRestore.forEach((chunk, blocks) -> {
            ListTag list = new ListTag();
            blocks.forEach((pos, d) -> {
                CompoundTag blockSave = new CompoundTag();
                blockSave.putLong("Pos", pos);
                blockSave.put("State", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, d.state).getOrThrow());
                blockSave.putLong("Time", d.time);
                ListTag stacks = new ListTag();
                d.toDrop.forEach(s -> stacks.add(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, s).getOrThrow()));
                blockSave.put("Drops", stacks);
                list.add(blockSave);
            });
            data.put(String.valueOf(chunk), list);
        });
        return data;
    }

    public record SavedBlock(BlockState state, long time, List<ItemStack> toDrop) {

    }
}
