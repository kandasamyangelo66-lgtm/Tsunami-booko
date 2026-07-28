package tsunamimod.content;

import mindustry.content.Items;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.Category;
import tsunamimod.blocks.TsunamiBlock;

public class TsunamiBlocks{

    public static Block tsunami;

    public static void load(){
        tsunami = new TsunamiBlock("tsunami"){{
            requirements(Category.effect, ItemStack.with(
                Items.copper, 150,
                Items.titanium, 80,
                Items.silicon, 60,
                Items.metaglass, 40
            ));

            spreadInterval = 6f;
            tilesPerWave = 10;
            maxRadius = 260f;
            destroyBuildings = true;
            clearOverlays = true;
            ambientEffectChance = 0.3f;
        }};
    }

    private TsunamiBlocks(){}
}
