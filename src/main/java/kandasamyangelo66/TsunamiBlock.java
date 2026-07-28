package kandasamyangelo66;

import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.World;

public class TsunamiBlock extends Block {

    public TsunamiBlock(String name) {
        super(name);
        update = true;
        solid = true;
        destructible = true;
        size = 1;
    }

    @Override
    public void setBars() {
        super.setBars();
    }

    public class TsunamiBuild extends Building {

        private float timer = 0;

        @Override
        public void updateTile() {
            timer += arc.util.Time.delta;

            // Spread every 2 seconds
            if (timer >= 120) {
                timer = 0;
                spread();
            }
        }

        void spread() {
            int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            };

            for (int[] dir : directions) {
                Tile tile = world.tile(tileX() + dir[0], tileY() + dir[1]);

                if (tile != null && tile.build != null) {
                    // Destroy buildings
                    tile.build.kill();
                }
            }
        }
    }
}
