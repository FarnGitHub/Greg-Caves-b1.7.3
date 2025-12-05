package farn.greg_cave.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(Carver.class)
public abstract class CarverStub {

    @Shadow
    protected Random random = new Random();

    @WrapMethod(method="carve(Lnet/minecraft/world/chunk/ChunkSource;Lnet/minecraft/world/World;II[B)V")
    public void greg_wrapGeneration(ChunkSource source, World world, int chunkX, int chunkZ, byte[] blocks, Operation<Void> original) {
        original.call(source, world, chunkX, chunkZ, blocks);
    }
}
