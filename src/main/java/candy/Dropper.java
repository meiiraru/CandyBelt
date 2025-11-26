package candy;

import cinnamon.model.material.Material;
import cinnamon.registry.TerrainRegistry;
import cinnamon.render.Camera;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.sound.SoundCategory;
import cinnamon.text.Text;
import cinnamon.utils.Alignment;
import cinnamon.utils.Resource;
import cinnamon.utils.Rotation;
import cinnamon.world.terrain.Terrain;

public class Dropper extends Terrain {

    protected int delay;
    protected int ticks = 0;

    protected float goodChance;
    protected float goodChanceLicked;

    protected int candies;

    public Dropper() {
        super(new Resource("candy", "dropper/model.obj"), TerrainRegistry.CUSTOM);
    }

    @Override
    public void tick() {
        super.tick();

        ticks--;
        if (candies != 0 && ticks <= 0) {
            dropItem();
            ticks = delay;
        }
    }

    @Override
    protected void renderModel(Camera camera, Material material, MatrixStack matrices, float delta) {
        super.renderModel(camera, material, matrices, delta);
        matrices.pushMatrix();
        matrices.translate(0f, 0.25f, -0.65f);
        matrices.scale(1/48f);
        matrices.rotate(Rotation.Z.rotationDeg(180f));
        Text.of(candies < 0 ? "\u221E" : candies).render(VertexConsumer.WORLD_MAIN, matrices, 0, 0, Alignment.CENTER);
        matrices.popMatrix();
    }

    protected void dropItem() {
        Candy item = new Candy(goodChance, goodChanceLicked);
        item.setPos(pos.x + 0.5f, pos.y - item.getAABB().getHeight() - 0.1f, pos.z + 0.5f);
        getWorld().addEntity(item);
        getWorld().playSound(new Resource("candy", "hopper_pop.ogg"), SoundCategory.ENTITY, pos);
        candies--;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setGoodChance(float goodChance) {
        this.goodChance = goodChance;
    }

    public void setGoodChanceLicked(float goodChanceLicked) {
        this.goodChanceLicked = goodChanceLicked;
    }

    public void setCandies(int candies) {
        this.candies = candies;
    }

    public int getCandiCount() {
        return candies;
    }
}
