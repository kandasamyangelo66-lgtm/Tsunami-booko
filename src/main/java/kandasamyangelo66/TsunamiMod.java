package kandasamyangelo66;

import mindustry.mod.Mod;
import mindustry.world.Block;

public class TsunamiMod extends Mod {

    public static Block tsunami;

    @Override
    public void loadContent() {
        tsunami = new TsunamiBlock("tsunami");
    }
}
