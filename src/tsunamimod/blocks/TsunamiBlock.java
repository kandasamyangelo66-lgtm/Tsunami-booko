package tsunamimod.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public class TsunamiBlock extends Block{

    public float spreadInterval = 6f;
    public int tilesPerWave = 10;
    public float maxRadius = 260f;
    public Floor floodFloor;
    public boolean destroyBuildings = true;
    public boolean clearOverlays = true;
    public float ambientEffectChance = 0.3f;
    public Effect splashEffect = defaultSplashEffect();
    public Effect waterEffect = defaultWaterEffect();

    public TsunamiBlock(String name){
        super(name);

        update = true;
        solid = true;
        destructible = true;
        placeableLiquid = false;
        size = 3;
        health = 700;
        buildVisibility = mindustry.world.meta.BuildVisibility.shown;

        floodFloor = mindustry.content.Blocks.deepwater;
    }

    public class TsunamiBlockBuild extends Building{

        private Seq<Tile> activeQueue;
        private Seq<Tile> nextQueue;
        private IntSet visited;
        private float waveTimer;
        private boolean started;
        private boolean finished;
        private int originTx, originTy;

        private void initFlood(){
            if(started) return;
            started = true;

            activeQueue = new Seq<>();
            nextQueue = new Seq<>();
            visited = new IntSet();

            originTx = tile.x;
            originTy = tile.y;

            int half = (block.size - 1) / 2;
            for(int dx = -half; dx <= block.size - 1 - half; dx++){
                for(int dy = -half; dy <= block.size - 1 - half; dy++){
                    Tile t = world.tile(originTx + dx, originTy + dy);
                    if(t != null && visited.add(t.pos())){
                        activeQueue.add(t);
                    }
                }
            }
        }

        @Override
        public void updateTile(){
            if(net.client()) return;

            initFlood();
            if(finished) return;

            waveTimer += Time.delta;
            if(waveTimer >= spreadInterval){
                waveTimer = 0f;
                processWave();
            }
        }

        private void processWave(){
            if(activeQueue.isEmpty()){
                if(nextQueue.isEmpty()){
                    finished = true;
                    return;
                }
                Seq<Tile> swap = activeQueue;
                activeQueue = nextQueue;
                nextQueue = swap;
                nextQueue.clear();
            }

            int count = Math.min(tilesPerWave, activeQueue.size);
            for(int i = 0; i < count; i++){
                Tile t = activeQueue.pop();
                floodTile(t);
                discoverNeighbors(t);
            }
        }

        private void floodTile(Tile t){
            if(t == null) return;

            if(destroyBuildings && t.build != null && t.build != this){
                t.build.kill();
            }

            if(clearOverlays && t.overlay() != mindustry.content.Blocks.air){
                t.setOverlayNet(mindustry.content.Blocks.air);
            }

            if(t.floor() != floodFloor){
                t.setFloorNet(floodFloor);
            }

            Call.effect(splashEffect, t.worldx(), t.worldy(), 0f, Color.white);
            if(Mathf.chance(ambientEffectChance)){
                Call.effect(waterEffect, t.worldx(), t.worldy(), 0f, Color.white);
            }
        }

        private void discoverNeighbors(Tile t){
            queueNeighbor(t.x + 1, t.y);
            queueNeighbor(t.x - 1, t.y);
            queueNeighbor(t.x, t.y + 1);
            queueNeighbor(t.x, t.y - 1);
        }

        private void queueNeighbor(int nx, int ny){
            Tile n = world.tile(nx, ny);
            if(n == null) return;

            int pos = n.pos();
            if(visited.contains(pos)) return;

            if(maxRadius > 0f){
                float dst = Mathf.dst(originTx, originTy, n.x, n.y);
                if(dst > maxRadius) return;
            }

            visited.add(pos);
            nextQueue.add(n);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(finished);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            finished = read.bool();
        }
    }

    private static Effect defaultSplashEffect(){
        return new Effect(22f, e -> {
            Draw.color(Color.white, Color.blue, e.fin());
            Lines.stroke(1.6f * (1f - e.fin()) + 0.3f);
            Lines.circle(e.x, e.y, 2f + e.fin() * 5f);
            Draw.color();
        });
    }

    private static Effect defaultWaterEffect(){
        return new Effect(40f, e -> {
            Draw.color(Color.blue, Color.white, e.fin() * 0.5f);
            for(int i = 0; i < 3; i++){
                float angle = i * 120f + e.rotation;
                float rad = e.fin() * 8f;
                Fill.circle(
                    e.x + Mathf.cosDeg(angle) * rad,
                    e.y + Mathf.sinDeg(angle) * rad,
                    2.2f * (1f - e.fin())
                );
            }
            Draw.color();
        });
    }
                      }
