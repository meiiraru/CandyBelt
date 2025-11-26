package candy;

import cinnamon.Client;
import cinnamon.gui.Toast;
import cinnamon.registry.TerrainRegistry;
import cinnamon.sound.SoundCategory;
import cinnamon.utils.Colors;
import cinnamon.utils.IOUtils;
import cinnamon.utils.Maths;
import cinnamon.utils.Resource;
import cinnamon.vr.XrManager;
import cinnamon.world.entity.Entity;
import cinnamon.world.entity.misc.Firework;
import cinnamon.world.entity.misc.FireworkStar;
import cinnamon.world.entity.misc.TriggerArea;
import cinnamon.world.entity.xr.XrHand;
import cinnamon.world.terrain.Terrain;
import cinnamon.world.world.WorldClient;
import org.joml.Vector3f;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class CandyWorld extends WorldClient {

    private static final Path scoreFile = IOUtils.ROOT_FOLDER.resolve("candy");

    private static final List<Wave> waves = List.of(
            new Wave(10, 0.5f, 100, 75, 0.3f, 0.7f),  //round 1 (tutorial)
            new Wave(20, 0.5f, 100, 50, 0.1f, 0.7f),  //round 2
            new Wave(40, 0.6f, 250, 40, 0.1f, 0.6f),  //round 3
            new Wave(60, 0.7f, 500, 30, 0.1f, 0.5f),  //round 4
            new Wave(100, 0.8f, 1000, 20, 0f, 0.33f), //round 5 (semi impossible)
            new Wave(150, 0.9f, 10000, 20, 0f, 0.1f), //round 6 (impossible-ish)
            new Wave(-1, 1f, 0, 20, 0f, 0.1f)         //infinite mode
    );

    private StateMachine gameState = StateMachine.START;
    private int currentWave = 0;
    private int score = 0;
    private int hiscore = 0;

    private final XrHand[] hands = new XrHand[2];
    private TriggerArea lickArea;
    private Dropper dropper;
    private CandiCollector collector;

    @Override
    protected void tempLoad() {
        Toast.clear(Toast.ToastType.WORLD);
        Toast.addToast("Press the button on your left to start the game").length(100);

        try {
            byte[] data = IOUtils.readFile(scoreFile);
            if (data != null) {
                String s = new String(data);
                hiscore = Integer.parseInt(s.trim());
            }
        } catch (Exception e) {
            Client.LOGGER.error(e);
        }

        if (XrManager.isInXR()) {
            for (int i = 0; i < hands.length; i++) {
                hands[i] = new XrHand(UUID.randomUUID(), i);
                this.addEntity(hands[i]);
            }
        }

        this.hud = new CandyHud();
        this.hud.init();

        addTerrain(new Terrain(new Resource("candy", "factory/model.obj"), TerrainRegistry.CUSTOM));

        float beltSpeed = 0.05f;
        Terrain belt = new ConveyorBelt(beltSpeed);
        belt.setPos(2, 0, 9);
        belt.setRotation((byte) 2);
        addTerrain(belt);

        Terrain belt2 = new ConveyorBelt(beltSpeed);
        belt2.setPos(2, 0, 4);
        belt2.setRotation((byte) 3);
        addTerrain(belt2);

        dropper = new Dropper();
        dropper.setPos(2, 3, 9);
        addTerrain(dropper);

        lickArea = new TriggerArea(UUID.randomUUID(), 0.1f, 0.1f, 0.1f);
        lickArea.setEnterTrigger(e -> {
            if (e instanceof Candy c)
                c.lick();
        });
        addEntity(lickArea);

        collector = new CandiCollector();
        collector.setPos(-3f, 0, 4.5f);
        addEntity(collector);

        Button button = new Button(this::startWave);
        button.setPos(0.5f, 0f, 3.5f);
        addEntity(button);

        TriggerArea buttonArea = new TriggerArea(UUID.randomUUID(), 0.25f, 1/16f, 0.25f);
        buttonArea.setPos(0.5f, 1f, 3.5f);
        buttonArea.setEnterTrigger(e -> {
            if (e instanceof XrHand)
                button.press();
        });
        addEntity(buttonArea);

        gameState = StateMachine.START;
        currentWave = 0;
        score = 0;
    }

    protected void startWave() {
        if (gameState != StateMachine.PLAYING) {
            if (gameState == StateMachine.GAME_OVER) {
                currentWave = 0;
                score = 0;
            }

            loadWave(currentWave);
            if (getWave(currentWave).candies() < 0)
                Toast.addToast("Endless!!!!").length(100);
            else
                Toast.addToast("Wave " + (currentWave + 1)).length(100);
            gameState = StateMachine.PLAYING;
        }
    }

    protected void loadWave(int waveIndex) {
        Wave wave = getWave(waveIndex);
        dropper.setDelay(wave.delay());
        dropper.setGoodChance(wave.goodChance());
        dropper.setGoodChanceLicked(wave.goodChanceLicked());
        dropper.setCandies(wave.candies());
        collector.reset();
    }

    protected void summonFirework() {
        Firework f = new Firework(UUID.randomUUID(), Maths.range(10, 30), Maths.spread(new Vector3f(0, 1f, 0), 15, 15).mul(2f),
                new FireworkStar(new Integer[]{Colors.randomRainbow().argb}, new Integer[]{Colors.WHITE.argb}, true, true)
        );
        f.setSilent(getTime() % (Client.TPS / 2) != 0);
        float angle = (float) (Math.random() * Math.PI * 2);
        float radius = Maths.range(20, 64);
        f.setPos((float) (Math.cos(angle) * radius), 5f, (float) (Math.sin(angle) * radius));
        addEntity(f);
    }

    protected Wave getWave(int wave) {
        return wave >= waves.size() ? waves.getLast() : waves.get(wave);
    }

    @Override
    public void tick() {
        super.tick();
        lickArea.setPos(player.getEyePos());
        if (gameState == StateMachine.WAVE_END && getTime() % (Client.TPS / 10) == 0)
            summonFirework();

        if (gameState == StateMachine.PLAYING && dropper.getCandiCount() == 0 && !hasCandy()) {
            Wave w = getWave(currentWave);
            float ratio = collector.getCurrentRate();
            if (ratio < w.winFactor()) {
                gameState = StateMachine.GAME_OVER;
                Toast.addToast("YOU ARE FIRED!").length(100);
                Toast.addToast("Game Over - score %s\nYou reached wave %d\nSuccess rate %.2f/%.2f".formatted(score, currentWave + 1, ratio, w.winFactor())).length(200);
                if (score > hiscore) {
                    Toast.addToast("New High Score! %d pts".formatted(score)).length(200);
                    hiscore = score;
                }
                IOUtils.writeFile(scoreFile, "%d".formatted(hiscore).getBytes());
                playSound(new Resource("candy", "game_over.ogg"), SoundCategory.MASTER, player.getPos());
            } else {
                gameState = StateMachine.WAVE_END;
                currentWave++;
                int score = (int) Math.ceil(w.score() * ratio);
                this.score += score;
                Toast.addToast("Wave %d Complete!\nPrepare for the next wave\n+%d pts\nSuccess rate %.2f".formatted(currentWave, score, ratio)).length(100);
            }
        }
    }

    protected boolean hasCandy() {
        for (Entity entity : entities.values())
            if (entity instanceof Candy c && (c.isGrabbed() || c.getPos().y > 0.5f))
                return true;
        return false;
    }

    @Override
    protected void updateCamera(Entity camEntity, int cameraMode, float delta) {
        super.updateCamera(camEntity, this.cameraMode, delta);
    }

    @Override
    public void respawn(boolean init) {
        player = new CandyPlayer();
        player.setPos(0f, 0.9f, 3.7f);
        player.setRot(0, 180);
        player.getAbilities().godMode(true).canFly(true).canBuild(false);
        player.updateMovementFlags(false, false, true);
        this.addEntity(player);
    }

    @Override
    public void xrTriggerPress(int button, float value, int hand, float lastValue) {
        if (hand < hands.length && hands[hand] != null) {
            if (value > 0.5f && lastValue <= 0.5f) {
                hands[hand].grab();
            } else if (value <= 0.5f && lastValue > 0.5f) {
                hands[hand].release();
            }
            return;
        }

        super.xrTriggerPress(button, value, hand, lastValue);
    }

    public int getScore() {
        return score;
    }
}
