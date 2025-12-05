package farn.greg_cave.world;

import net.minecraft.src.*;

public class MapGenGregCaves extends MapGenCaves {
    private double[] caveNoise;
    private float[] biomeWeightTable;
    private NoiseCaveGenerator noiseCaves;
    public NoiseGeneratorOctaves noiseGen6;
    private NoiseGeneratorOctaves field_147431_j;
    private NoiseGeneratorOctaves field_147432_k;
    private NoiseGeneratorOctaves interpolationNoise;
    private double[] interpolationNoises;
    private double[] lowerInterpolatedNoises;
    private double[] upperInterpolatedNoises;
    private double[] depthNoises;
    private World worldObj;
    public static boolean mgPack = false;
    private final int worldHeight = 128;
    private final int shiftNoiseX = 11;
    private final int shiftNoiseZ = 7;

    @Override
    public void func_867_a(IChunkProvider c, World w, int chunkX, int chunkZ, byte[] blocks) {
        this.rand.setSeed(w.getRandomSeed());
        if (worldObj != w) {
            this.caveNoise = new double[825];
            this.biomeWeightTable = new float[25];
            this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
            this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
            this.interpolationNoise = new NoiseGeneratorOctaves(this.rand, 8);
            this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
            this.noiseCaves = new NoiseCaveGenerator(this.rand);
            for (int j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
                    this.biomeWeightTable[j + 2 + (k + 2) * 5] = f;
                }
            }
        }
        this.worldObj = w;
        int k = this.field_1306_a;
        long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        long var9 = this.rand.nextLong() / 2L * 2L + 1L;

        for (int var11 = chunkX - k; var11 <= chunkX + k; var11++) {
            for (int var12 = chunkZ - k; var12 <= chunkZ + k; var12++) {
                this.rand.setSeed(var11 * var7 + var12 * var9 ^ w.getRandomSeed());
                this.func_868_a(w, var11, var12, chunkX, chunkZ, blocks);
            }
        }
        this.generateNoiseCaves(chunkX, chunkZ, blocks);
        BlockSand.fallInstantly = false;
    }

    private void generateNoiseCaves(int chunkX, int chunkZ, byte[] blocks) {
        generateNoiseCavesNoise(chunkX, chunkZ);

        for (int noiseX = 0; noiseX < 4; ++noiseX) {
            int ix0 = noiseX * 5;
            int ix1 = (noiseX + 1) * 5;

            for (int noiseZ = 0; noiseZ < 4; ++noiseZ) {
                int ix0z0 = (ix0 + noiseZ) * 33;
                int ix0z1 = (ix0 + noiseZ + 1) * 33;
                int ix1z0 = (ix1 + noiseZ) * 33;
                int ix1z1 = (ix1 + noiseZ + 1) * 33;

                for (int noiseY = 0; noiseY < 32; ++noiseY) {
                    double x0z0 = this.caveNoise[ix0z0 + noiseY];
                    double x0z1 = this.caveNoise[ix0z1 + noiseY];
                    double x1z0 = this.caveNoise[ix1z0 + noiseY];
                    double x1z1 = this.caveNoise[ix1z1 + noiseY];
                    double x0z0Add = (this.caveNoise[ix0z0 + noiseY + 1] - x0z0) * 0.125D;
                    double x0z1Add = (this.caveNoise[ix0z1 + noiseY + 1] - x0z1) * 0.125D;
                    double x1z0Add = (this.caveNoise[ix1z0 + noiseY + 1] - x1z0) * 0.125D;
                    double x1z1Add = (this.caveNoise[ix1z1 + noiseY + 1] - x1z1) * 0.125D;

                    for (int pieceY = 0; pieceY < 8; ++pieceY) {
                        double z0 = x0z0;
                        double z1 = x0z1;
                        double z0Add = (x1z0 - x0z0) * 0.25D;
                        double z1Add = (x1z1 - x0z1) * 0.25D;

                        for (int pieceX = 0; pieceX < 4; ++pieceX) {
                            int index = pieceX + noiseX * 4 << this.shiftNoiseX | noiseZ * 4 << this.shiftNoiseZ | noiseY * 8 + pieceY;
                            index -= this.worldHeight;
                            double densityAdd = (z1 - z0) * 0.25D;
                            double density = z0 - densityAdd;

                            for (int pieceZ = 0; pieceZ < 4; ++pieceZ) {
                                index += this.worldHeight;
                                if ((density += densityAdd) < 0) {
                                     int y = noiseY * 8 + pieceY;
                                     if (y > 0) {
                                        if (blocks[index] == Block.bedrock.blockID) {
                                            blocks[index] = (byte) Block.stone.blockID;
                                        } else {
                                            if(y < 11) {
                                                blocks[index] = (byte) Block.lavaStill.blockID;
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

    private void generateNoiseCavesNoise(int chunkX, int chunkZ) {
        int cx = chunkX * 4, cz = chunkZ * 4;
        this.depthNoises = noiseGen6.func_4109_a(this.depthNoises, cx, cz, 5, 5, 200.0D, 200.0D, 0.5D);
        this.interpolationNoises = this.interpolationNoise.generateNoiseOctaves(this.interpolationNoises, cx, 0, cz, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.lowerInterpolatedNoises = this.field_147431_j.generateNoiseOctaves(this.lowerInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.upperInterpolatedNoises = this.field_147432_k.generateNoiseOctaves(this.upperInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
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

                        float weightHere = this.biomeWeightTable[x1 + 2 + (z1 + 2) * 5] / (depthHere + 2.0F);

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
                double depthNoise = this.depthNoises[j] / 8000;

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

                    double lowerNoise = this.lowerInterpolatedNoises[i] / 512.0D;
                    double upperNoise = this.upperInterpolatedNoises[i] / 512.0D;
                    double interpolation = (this.interpolationNoises[i] / 10.0D + 1.0D) / 2.0D;
                    double noise = farn.greg_cave.util.MathHelper.denormalizeClamp(lowerNoise, upperNoise, interpolation) - falloff;
                    if (y > 29) {
                        double lerp = (float) (y - 29) / 3.0F;
                        noise = noise * (1.0D - lerp) + -10.0D * lerp;
                    }

                    double caveNoise = this.noiseCaves.sample(noise, y * 8, chunkZ * 16 + (z * 4), chunkX * 16 + (x * 4));
                    caveNoise = farn.greg_cave.util.MathHelper.clampedLerp(caveNoise, (lowestScaledDepth * -30) + 20, (y - sub + 2) / 2.0);
                    this.caveNoise[i] = caveNoise;
                    i++;
                }
            }
        }
    }
}
