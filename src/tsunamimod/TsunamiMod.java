package tsunamimod;

import mindustry.mod.Mod;
import tsunamimod.content.TsunamiBlocks;

public class TsunamiMod extends Mod{

    public TsunamiMod(){
        arc.util.Log.info("Tsunami Flood mod initializing...");
    }

    @Override
    public void loadContent(){
        TsunamiBlocks.load();
    }
}
