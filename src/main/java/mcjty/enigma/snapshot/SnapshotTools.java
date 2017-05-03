package mcjty.enigma.snapshot;

import mcjty.enigma.parser.StringPointer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnapshotTools {

    private static class Loc {
        private int x = 0;
        private int y = 0;
        private int z = 0;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public void inc() {
            z++;
            if (z > 15) {
                z = 0;
                x++;
                if (x > 15) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    public static void restoreChunkSnapshot(World world, byte[] input) {
        ByteArrayInputStream stream = new ByteArrayInputStream(input);
        NBTTagCompound tag;
        try {
            tag = CompressedStreamTools.readCompressed(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        restoreChunkSnapshotTag(world, tag);
    }

    public static void restoreChunkSnapshot(World world, File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
        restoreChunkSnapshotTag(world, tag);
    }

    public static void restoreChunkSnapshotTag(World world, NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("chunks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = list.getCompoundTagAt(i);
            int x = tc.getInteger("x");
            int z = tc.getInteger("z");
            int id = tc.getInteger("dimension");
            World w = DimensionManager.getWorld(id);
            if (w == null) {
                w = world.getMinecraftServer().worldServerForDimension(id);
            }
            Chunk chunk = w.getChunkFromChunkCoords(x, z);
            restoreChunkSnapshotTag(world, chunk, tc.getCompoundTag("data"));
        }
    }

    public static void restoreChunkSnapshotTag(World world, Chunk curchunk, NBTTagCompound tag) {
        List<IBlockState> differentBlocks = new ArrayList<>();
        NBTTagList blocks = tag.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < blocks.tagCount() ; i++) {
            NBTTagCompound tc = blocks.getCompoundTagAt(i);
            String n = tc.getString("n");
            int meta = tc.getByte("m");
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(n));
            differentBlocks.add(block.getStateFromMeta(meta));
        }

        String line = tag.getString("chunk");

        int cnt = 0;
        Loc loc = new Loc();
        StringPointer s = new StringPointer(line);
        s.inc();
        while (s.hasMore()) {
            int count = uncompress(s);
            int index = uncompress(s);
            IBlockState state = differentBlocks.get(index);
            for (int i = 0 ; i < count ; i++) {
                int x = curchunk.getPos().chunkXPos * 16 + loc.getX();
                int y = loc.getY();
                int z = curchunk.getPos().chunkZPos * 16 + loc.getZ();
                loc.inc();
                BlockPos p = new BlockPos(x, y, z);
                IBlockState curstate = curchunk.getBlockState(p);
                if (!curstate.equals(state)) {
                    world.setBlockState(p, state, 3);
                    cnt++;
                }
            }
        }

        NBTTagList telist = tag.getTagList("te", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < telist.tagCount() ; i++) {
            NBTTagCompound tc = telist.getCompoundTagAt(i);
            int x = tc.getShort("x");
            int y = tc.getShort("y");
            int z = tc.getShort("z");
            NBTTagCompound nbt = tc.getCompoundTag("nbt");
            BlockPos pos = new BlockPos(curchunk.getPos().chunkXPos * 16 + x, y, curchunk.getPos().chunkZPos * 16 + z);
            TileEntity te = world.getTileEntity(pos);
            if (te != null) {
                nbt.setInteger("x", pos.getX());
                nbt.setInteger("y", pos.getY());
                nbt.setInteger("z", pos.getZ());
                te.readFromNBT(nbt);
            }
        }

        System.out.println("cnt = " + cnt);
    }

    public static void makeChunkSnapshot(World world, List<Chunk> chunks, File file) throws IOException {
        NBTTagCompound tag = makeChunkSnapshotTag(world, chunks);
        FileOutputStream stream = new FileOutputStream(file);
        CompressedStreamTools.writeCompressed(tag, stream);
    }

    public static byte[] makeChunkSnapshot(World world, List<Chunk> chunks) {
        NBTTagCompound tag = makeChunkSnapshotTag(world, chunks);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            CompressedStreamTools.writeCompressed(tag, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }

    public static NBTTagCompound makeChunkSnapshotTag(World world, List<Chunk> chunks) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Chunk chunk : chunks) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("x", chunk.getPos().chunkXPos);
            tc.setInteger("z", chunk.getPos().chunkZPos);
            tc.setInteger("dimension", chunk.getWorld().provider.getDimension());
            tc.setTag("data", makeChunkSnapshotTag(world, chunk));
            list.appendTag(tc);
        }
        tag.setTag("chunks", list);
        return tag;
    }

    public static NBTTagCompound makeChunkSnapshotTag(World world, Chunk curchunk) {
        Map<Pair<String,Integer>, Integer> differentBlocks = new HashMap<>();
        StringBuffer output = new StringBuffer();
        int maxindex = 0;

        Map<BlockPos, NBTTagCompound> tileEntityData = new HashMap<>();

        Pair<String, Integer> prev = null;
        int cnt = 0;

        for (int y = 0 ; y < world.getHeight() ; y++) {
            for (int x = 0 ; x < 16 ; x++) {
                for (int z = 0 ; z < 16 ; z++) {
                    IBlockState curstate = curchunk.getBlockState(x, y, z);
                    String n = curstate.getBlock().getRegistryName().toString();
                    int meta = curstate.getBlock().getMetaFromState(curstate);
                    Pair<String, Integer> p = Pair.of(n, meta);
                    int blockindex;
                    if (!differentBlocks.containsKey(p)) {
                        blockindex = maxindex;
                        differentBlocks.put(p, blockindex);
                        maxindex++;
                    }

                    if (p.equals(prev)) {
                        cnt++;
                    } else {
                        if (cnt > 0) {
                            compress(cnt, output);
                            compress(differentBlocks.get(prev), output);
                        }
                        cnt = 1;
                        prev = p;
                    }

                    TileEntity te = world.getTileEntity(new BlockPos(curchunk.getPos().chunkXPos * 16 + x, y, curchunk.getPos().chunkZPos * 16 + z));
                    if (te != null) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        te.writeToNBT(nbt);
                        tileEntityData.put(new BlockPos(x, y, z), nbt);
                    }
                }
            }
        }
        if (cnt > 0) {
            compress(cnt, output);
            compress(differentBlocks.get(prev), output);
        }

        System.out.println("differentBlocks = " + differentBlocks.size());
        System.out.println("tileEntityData = " + tileEntityData.size());
        System.out.println("bytes = " + output.length());
        System.out.println("output = " + output);

        Map<Integer, Pair<String, Integer>> reverseBlockMap = new HashMap<>();
        for (Map.Entry<Pair<String, Integer>, Integer> entry : differentBlocks.entrySet()) {
            reverseBlockMap.put(entry.getValue(), entry.getKey());
        }

        NBTTagCompound tag = new NBTTagCompound();

        NBTTagList list = new NBTTagList();
        for (int i = 0 ; i < maxindex ; i++) {
            String name = reverseBlockMap.get(i).getKey();
            Integer meta = reverseBlockMap.get(i).getValue();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("n", name);
            tc.setByte("m", (byte) (int) meta);
            list.appendTag(tc);
        }
        tag.setTag("blocks", list);
        tag.setString("chunk", output.toString());

        list = new NBTTagList();
        for (Map.Entry<BlockPos, NBTTagCompound> entry : tileEntityData.entrySet()) {
            BlockPos p = entry.getKey();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setShort("x", (short) p.getX());
            tc.setShort("y", (short) p.getY());
            tc.setShort("z", (short) p.getZ());
            tc.setTag("nbt", entry.getValue());
            list.appendTag(tc);
        }
        tag.setTag("te", list);
        return tag;
    }

    private static void compress(int i, StringBuffer buf) {
        if (i < 10) {
            buf.append((char) ('0' + i));
        } else if (i < 10+26) {
            buf.append((char) ('A' + i - 10));
        } else if (i < 10+26+26) {
            buf.append((char) ('a' + i - 10 - 26));
        } else {
            buf.append('.');
            compress(i / (10+26+26), buf);
            compress(i % (10+26+26), buf);
        }
    }

    private static int uncompress(StringPointer s) {
        char c = s.current();
        s.inc();
        if (c >= '0' && c <= '9') {
            return c-'0';
        } else if (c >= 'A' && c <= 'Z') {
            return c-'A'+10;
        } else if (c >= 'a' && c <= 'z') {
            return c-'a'+10+26;
        } else if (c == '.') {
            if (!s.hasMore()) {
                throw new RuntimeException("Impossible!");
            }
            int j1 = uncompress(s);
            int j2 = uncompress(s);
            return (j1 * (10+26+26)) + j2;
        } else {
            throw new RuntimeException("Impossible!");
        }
    }

    public static void main(String[] args) {
        StringBuffer compressed = new StringBuffer();
        compress(1, compressed);
        compress(2, compressed);
        compress(3, compressed);
        compress(100, compressed);
        compress(500, compressed);
        compress(1, compressed);

        StringPointer p = new StringPointer(compressed.toString());
        p.inc();
        while (p.hasMore()) {
            int i = uncompress(p);
            System.out.println("i = " + i);
        }
    }

}
