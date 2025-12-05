package net.minecraft.src;

import farn.greg_cave.world.MapGenGregCaves;
import farn.greg_cave.world.WorldGenOverrideGreg;

public class mod_GregCaveMangoPack extends BaseMod{
    @Override
    public String Version() {
        return "1.0";
    }

    @MLProp(name = "LangGregCaves")
    public static String langGreg = "Greg Caves";

    public static final String KEY_LANG_GREG = "farn.gregcaves";

    @Override
    public void ModsLoaded() {
        if(ModLoader.isModLoaded("mod_WorldGeneratorOverrides")) {
            MapGenGregCaves.mgPack = true;
            ModLoader.AddLocalization(KEY_LANG_GREG, langGreg);
            WorldGenOverrideGreg.register();
        }
    }
}
