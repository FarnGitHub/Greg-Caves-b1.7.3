package farn.greg_cave.mixin;

import com.itselix99.betterworldoptions.config.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.greg_cave.BetterWorldOptionCompat;
import farn.greg_cave.world.NoiseCaveGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.SandBlock;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.carver.CaveCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(CaveCarver.class)
public class CaveCarverMixin extends CarverStub {
    @Unique
    private double[] greg_caveNoise;
    @Unique
    private float[] greg_biomeWeightTable;
    @Unique
    private NoiseCaveGenerator greg_noiseCaves;
    @Unique
    public OctavePerlinNoiseSampler greg_noiseGen6;
    @Unique
    private OctavePerlinNoiseSampler greg_noiseGen_j;
    @Unique
    private OctavePerlinNoiseSampler greg_noiseGen_k;
    @Unique
    private OctavePerlinNoiseSampler greg_interpolationNoise;
    @Unique
    private double[] greg_interpolationNoises;
    @Unique
    private double[] greg_lowerInterpolatedNoises;
    @Unique
    private double[] greg_upperInterpolatedNoises;
    @Unique
    private double[] greg_depthNoises;
    @Unique
    private World greg_world;
    @Unique
    private int greg_worldHeight = 128;
    @Unique
    private int greg_shiftNoiseX = 11;
    @Unique
    private int greg_shiftNoiseZ = 7;

    public void greg_wrapGeneration(ChunkSource source, World world, int chunkX, int chunkZ, byte[] blocks, Operation<Void> original) {
        if(world.dimension.id == 0) {
            this.random.setSeed(world.getSeed());
            if (greg_world != world) {
                greg_CalculateWorldHeight();
                greg_world = world;
                this.greg_caveNoise = new double[825];
                this.greg_biomeWeightTable = new float[25];
                this.greg_noiseGen_j = new OctavePerlinNoiseSampler(this.random, 16);
                this.greg_noiseGen_k = new OctavePerlinNoiseSampler(this.random, 16);
                this.greg_interpolationNoise = new OctavePerlinNoiseSampler(this.random, 8);
                this.greg_noiseGen6 = new OctavePerlinNoiseSampler(this.random, 16);
                this.greg_noiseCaves = new NoiseCaveGenerator(this.random);
                for (int j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        float f = 10.0F / MathHelper.sqrt((float) (j * j + k * k) + 0.2F);
                        this.greg_biomeWeightTable[j + 2 + (k + 2) * 5] = f;
                    }
                }
            }
            SandBlock.fallInstantly = true;
            original.call(source, world, chunkX, chunkZ, blocks);
            this.greg_generateNoiseCaves(chunkX, chunkZ, blocks);
            SandBlock.fallInstantly = false;
        } else {
            original.call(source, world, chunkX, chunkZ, blocks);
        }
    }

    @Unique
    private void greg_generateNoiseCaves(int chunkX, int chunkZ, byte[] blocks) {
        greg_generateNoiseCavesNoise(chunkX, chunkZ);

        for (int noiseX = 0; noiseX < 4; ++noiseX) {
            int ix0 = noiseX * 5;
            int ix1 = (noiseX + 1) * 5;

            for (int noiseZ = 0; noiseZ < 4; ++noiseZ) {
                int ix0z0 = (ix0 + noiseZ) * 33;
                int ix0z1 = (ix0 + noiseZ + 1) * 33;
                int ix1z0 = (ix1 + noiseZ) * 33;
                int ix1z1 = (ix1 + noiseZ + 1) * 33;

                for (int noiseY = 0; noiseY < 32; ++noiseY) {
                    double x0z0 = this.greg_caveNoise[ix0z0 + noiseY];
                    double x0z1 = this.greg_caveNoise[ix0z1 + noiseY];
                    double x1z0 = this.greg_caveNoise[ix1z0 + noiseY];
                    double x1z1 = this.greg_caveNoise[ix1z1 + noiseY];
                    double x0z0Add = (this.greg_caveNoise[ix0z0 + noiseY + 1] - x0z0) * 0.125D;
                    double x0z1Add = (this.greg_caveNoise[ix0z1 + noiseY + 1] - x0z1) * 0.125D;
                    double x1z0Add = (this.greg_caveNoise[ix1z0 + noiseY + 1] - x1z0) * 0.125D;
                    double x1z1Add = (this.greg_caveNoise[ix1z1 + noiseY + 1] - x1z1) * 0.125D;

                    for (int pieceY = 0; pieceY < 8; ++pieceY) {
                        double z0 = x0z0;
                        double z1 = x0z1;
                        double z0Add = (x1z0 - x0z0) * 0.25D;
                        double z1Add = (x1z1 - x0z1) * 0.25D;

                        for (int pieceX = 0; pieceX < 4; ++pieceX) {
                            int index = pieceX + noiseX * 4 << this.greg_shiftNoiseX | noiseZ * 4 << this.greg_shiftNoiseZ | noiseY * 8 + pieceY;
                            index -= this.greg_worldHeight;
                            double densityAdd = (z1 - z0) * 0.25D;
                            double density = z0 - densityAdd;

                            for (int pieceZ = 0; pieceZ < 4; ++pieceZ) {
                                index += this.greg_worldHeight;
                                if ((density += densityAdd) < 0) {
                                    int y = noiseY * 8 + pieceY;
                                    if (y > 0) {
                                        if (blocks[index] == Block.BEDROCK.id) {
                                            blocks[index] = (byte) Block.STONE.id;
                                        } else {
                                            if(y < 11) {
                                                blocks[index] = (byte) Block.LAVA.id;
                                            } else {
                                                blocks[index] = 0;
                                            }
                                        }
                                    }
                                }
                            }

                            z0 += z0Add;
                            z1 += z1Add;
                        }

                        x0z0 += x0z0Add;
                        x0z1 += x0z1Add;
                        x1z0 += x1z0Add;
                        x1z1 += x1z1Add;
                    }
                }
            }
        }
    }

    private void greg_generateNoiseCavesNoise(int chunkX, int chunkZ) {
        int cx = chunkX * 4, cz = chunkZ * 4;
        this.greg_depthNoises = greg_noiseGen6.create(this.greg_depthNoises, cx, cz, 5, 5, 200.0D, 200.0D, 0.5D);
        this.greg_interpolationNoises = this.greg_interpolationNoise.create(this.greg_interpolationNoises, cx, 0, cz, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.greg_lowerInterpolatedNoises = this.greg_noiseGen_j.create(this.greg_lowerInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.greg_upperInterpolatedNoises = this.greg_noiseGen_k.create(this.greg_upperInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        int i = 0, j = 0;

        for (int x = 0; x < 5; ++x) {
            for (int z = 0; z < 5; ++z) {
                float scale = 0.0F;
                float depth = 0.0F;
                float weight = 0.0F;
                double lowestScaledDepth = 0;


                for (int x1 = -2; x1 <= 2; ++x1) {
                    for (int z1 = -2; z1 <= 2; ++z1) {
                        float depthHere = 0.1F;
                        float scaleHere = 0.2F;

                        float weightHere = this.greg_biomeWeightTable[x1 + 2 + (z1 + 2) * 5] / (depthHere + 2.0F);

                        scale += scaleHere * weightHere;
                        depth += depthHere * weightHere;
                        weight += weightHere;
                        lowestScaledDepth = Math.min(lowestScaledDepth, 0.2F);
                    }
                }
                scale /= weight;
                depth /= weight;
                scale = scale * 0.9F + 0.1F;
                depth = (depth * 4.0F - 1.0F) / 8.0F;
                double depthNoise = this.greg_depthNoises[j] / 8000;

                if (depthNoise < 0.0D) {
                    depthNoise = -depthNoise * 0.3D;
                }

                depthNoise = depthNoise * 3.0D - 2.0D;

                if (depthNoise < 0.0D) {
                    depthNoise /= 2.0D;

                    if (depthNoise < -1.0D) {
                        depthNoise = -1.0D;
                    }

                    depthNoise /= 1.4D;
                    depthNoise /= 2.0D;
                } else {
                    if (depthNoise > 1.0D) {
                        depthNoise = 1.0D;
                    }

                    depthNoise /= 8.0D;
                }

                ++j;
                double scaledDepth = depth;
                double scaledScale = scale;
                scaledDepth += depthNoise * 0.2D;
                scaledDepth = scaledDepth * 8.5D / 8.0D;
                double terrainHeight = 8.5D + scaledDepth * 4.0D;
                double startLevel = 56 + (lowestScaledDepth * 20);
                int sub = (int) (startLevel / 8);

                for (int y = 0; y < 33; y++) {
                    double falloff = ((double) y - terrainHeight) * 12.0D * 128.0D / 256.0D / scaledScale;

                    if (falloff < 0.0D) {
                        falloff *= 4.0D;
                    }

                    double lowerNoise = this.greg_lowerInterpolatedNoises[i] / 512.0D;
                    double upperNoise = this.greg_upperInterpolatedNoises[i] / 512.0D;
                    double interpolation = (this.greg_interpolationNoises[i] / 10.0D + 1.0D) / 2.0D;
                    double noise = farn.greg_cave.util.MathHelper.denormalizeClamp(lowerNoise, upperNoise, interpolation) - falloff;
                    if (y > 29) {
                        double lerp = (float) (y - 29) / 3.0F;
                        noise = noise * (1.0D - lerp) + -10.0D * lerp;
                    }

                    double caveNoise = this.greg_noiseCaves.sample(noise, y * 8, chunkZ * 16 + (z * 4), chunkX * 16 + (x * 4));
                    caveNoise = farn.greg_cave.util.MathHelper.clampedLerp(caveNoise, (lowestScaledDepth * -30) + 20, (y - sub + 2) / 2.0);
                    this.greg_caveNoise[i] = caveNoise;
                    i++;
                }
            }
        }
    }
    @Unique
    private void greg_CalculateWorldHeight() {
        int baseHeight = 128;
        if(FabricLoader.getInstance().isModLoaded("betterworldoptions")) {
            try {
                baseHeight = BetterWorldOptionCompat.getBWO_WorldHeight();
            } catch (Exception e) {
                baseHeight = 128;
            }
        }

        this.greg_worldHeight = baseHeight;
        this.greg_shiftNoiseX = 10 + (baseHeight / 128);
        this.greg_shiftNoiseZ = 6 + (baseHeight / 128);
    }

}
