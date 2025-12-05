package farn.greg_cave.world;

import farn.greg_cave.transform.IReplaceVanillaCave;
import net.minecraft.src.*;
import worldgen.WorldGeneratorOverrides;
import worldgen.api.ChunkProviderGenerateGenerator;

import java.util.Random;

public class WorldGenOverrideGreg extends ChunkProviderGenerateGenerator {
    public WorldGenOverrideGreg() {
        super(StringTranslate.getInstance().translateKey(mod_GregCaveMangoPack.KEY_LANG_GREG));
    }

    public static void register() {
        WorldGeneratorOverrides.addChunkProviderOverride(new WorldGenOverrideGreg());
    }

    public void preTerrain(ChunkProviderGenerate yf1, Random random2, byte[] bs, Chunk lm4, BiomeGenBase[] biomeGenBases, double[] ds) {
         ((IReplaceVanillaCave)yf1).replaceVanillaCave();
    }
}
