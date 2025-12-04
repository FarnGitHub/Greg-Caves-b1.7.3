package farn.greg_cave.transform;

import farn.greg_cave.world.MapGenGregCaves;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.src.*;

@CTransformer(ChunkProviderGenerate.class)
public class ChunkProviderGenerateTransform {

    @CShadow
    private MapGenBase field_902_u;

    @CInject(method = "<init>", target = @CTarget("TAIL"))
    public void swapVanillaCaveWithGregCave(World fd1, long j2, InjectionCallback callback) {
        field_902_u = new MapGenGregCaves();
    }
}
