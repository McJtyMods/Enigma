package mcjty.enigma.snapshot;

import mcjty.enigma.parser.StringPointer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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

    public static void restoreChunkSnapshot(World world, Chunk curchunk, String input) {
        List<IBlockState> differentBlocks = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(input));
        int cnt = 0;
        try {
            String line = reader.readLine();
            while (line != null && line.startsWith(":")) {
                String[] split = StringUtils.split(line.substring(1), "@");
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0]));
                int meta = Integer.parseInt(split[1]);
                differentBlocks.add(block.getStateFromMeta(meta));
                line = reader.readLine();
            }
            if (line != null) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("cnt = " + cnt);
    }

    public static String makeChunkSnapshot(World world, Chunk curchunk) {
        Map<Pair<String,Integer>, Integer> differentBlocks = new HashMap<>();
        StringBuffer output = new StringBuffer();
        int maxindex = 0;

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
                        cnt = 0;
                        prev = p;
                    }
                }
            }
        }
        if (cnt > 0) {
            compress(cnt, output);
            compress(differentBlocks.get(prev), output);
        }

        System.out.println("differentBlocks = " + differentBlocks.size());
        System.out.println("bytes = " + output.length());
        System.out.println("output = " + output);

        Map<Integer, Pair<String, Integer>> reverseBlockMap = new HashMap<>();
        for (Map.Entry<Pair<String, Integer>, Integer> entry : differentBlocks.entrySet()) {
            reverseBlockMap.put(entry.getValue(), entry.getKey());
        }

        StringBuffer fullOut = new StringBuffer();
        for (int i = 0 ; i < maxindex ; i++) {
            String name = reverseBlockMap.get(i).getKey();
            Integer meta = reverseBlockMap.get(i).getValue();
            fullOut.append(":").append(name).append("@").append(meta).append('\n');
        }
        fullOut.append(output);
        return fullOut.toString();
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
