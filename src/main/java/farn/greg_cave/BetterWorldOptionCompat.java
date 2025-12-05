package farn.greg_cave;

import com.itselix99.betterworldoptions.config.Config;

public class BetterWorldOptionCompat {

    public static int getBWO_WorldHeight() {
        return Config.BWOConfig.world.worldHeightLimit.getIntValue();
    }
}
