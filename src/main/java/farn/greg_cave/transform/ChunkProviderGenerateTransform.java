package farn.greg_cave.transform;

import farn.greg_cave.world.MapGenGregCaves;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.src.*;

@CTransformer(ChunkProviderGenerate.class)
public class ChunkProviderGenerateTransform implements IReplaceVanillaCave{

    /*private MapGenGregCaves gregCaves = new MapGenGregCaves();

    @CRedirect(method = "provideChunk", target = @CTarget(value="INVOKE", target = "Lnet/minecraft/src/MapGenBase;func_867_a(Lnet/minecraft/src/IChunkProvider;Lnet/minecraft/src/World;II[B)V"))
    public void swapVanillaCaveWithGregCave(MapGenBase cavesOg, IChunkProvider provider, World world, int chunkX, int chunkY, byte[] block) {
        gregCaves.func_867_a(provider, world, chunkX, chunkY, block);
    }*/

    @CShadow
    private MapGenBase field_902_u;

    @CInject(method = "provideChunk", target = @CTarget("HEAD"))
    public void swapVanillaCaveWithGregCave(int x, int z, InjectionCallback callback) {
        if(!MapGenGregCaves.mgPack) {
            replaceVanillaCave();
        }
    }

    @Override
    public void replaceVanillaCave() {
        if(!(field_902_u instanceof MapGenGregCaves)) {
            field_902_u = new MapGenGregCaves();
        }
    }
}
