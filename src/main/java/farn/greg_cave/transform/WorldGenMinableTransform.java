package farn.greg_cave.transform;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenMinable;

import java.util.Random;

@CTransformer(WorldGenMinable.class)
public class WorldGenMinableTransform {

    @CInject(method="generate", target = @CTarget("HEAD"), cancellable = true)
    public void gregCavesReduceOre(World world, Random random, int x, int y, int z, InjectionCallback callback) {
        if(world.worldProvider.worldType == 0 && y < 33 && random.nextFloat() < 0.6F) {
            callback.setReturnValue(true);
        }
    }
}
