package kandasamyangelo66;

import arc.util.Time;
import mindustry.gen.Building;
import mindustry.Vars;
import mindustry.world.Block;
import mindustry.world.Tile;

public class TsunamiBlock extends Block {

    public TsunamiBlock(String name) {
        super(name);
        update = true;
        solid = true;
        destructible = true;
        size = 1;
        buildType = TsunamiBuild::new;
    }

    @Override
    public void setBars() {
        super.setBars();
    }

    public class TsunamiBuild extends Building {

        private float timer = 0f;

        @Override
        public void updateTile() {
            timer += Time.delta;

            // Spread every ~2 seconds
            if (timer >= 2f) {
                timer = 0f;
                spread();
            }
        }

        void spread() {
            int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            };

            for (int[] dir : directions) {
                Tile tile = Vars.world.tile(tileX() + dir[0], tileY() + dir[1]);

                if (tile == null) continue;

                if (tile.build != null) {
                    // Destroy buildings (own and enemy)
                    tile.build.kill();
                    // After destroying, place tsunami block on that tile
                    tile.setBlock(TsunamiBlock.this);
                } else {
                    // If there's no building and it's not already a tsunami block, spread into it
                    if (tile.block() != TsunamiBlock.this) {
                        tile.setBlock(TsunamiBlock.this);
                    }
                }
            }
        }
    }
}
