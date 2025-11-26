package candy;

import cinnamon.Client;
import cinnamon.registry.EntityRegistry;
import cinnamon.sound.SoundCategory;
import cinnamon.utils.Colors;
import cinnamon.utils.Maths;
import cinnamon.utils.Resource;
import cinnamon.vr.XrHandTransform;
import cinnamon.vr.XrRenderer;
import cinnamon.world.entity.xr.XrGrabbable;
import cinnamon.world.world.WorldClient;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class Candy extends XrGrabbable {

    private static final List<Resource> models = List.of(
            new Resource("candy", "lollipop/model.obj"),
            new Resource("candy", "candy/model.obj"),
            new Resource("candy", "chocolate/model.obj")
    );

    protected boolean isGood;
    protected boolean licked;
    protected float goodChanceLicked;

    protected int lifetime = -1;

    public Candy(float goodChance, float goodChanceLicked) {
        super(UUID.randomUUID(), models.get((int) (Math.random() * models.size())));
        this.goodChanceLicked = goodChanceLicked;
        isGood = Math.random() < goodChance;
    }

    @Override
    public void tick() {
        super.tick();

        if (lifetime > 0) {
            lifetime--;
            if (lifetime <= 0)
                remove();
        }
    }

    @Override
    public void release() {
        if (getHand() != null) {
            int i = getHand().getHand();
            XrHandTransform transform = XrRenderer.getHandTransform(i);
            Vector3f vel = new Vector3f(transform.vel());
            Vector2f rot = ((WorldClient) getWorld()).player.getRot();
            vel.rotateY((float) Math.toRadians(-rot.y));
            vel.rotateX((float) Math.toRadians(rot.x));
            vel.mul(0.15f);
            getMotion().add(vel);
            lifetime = 600; //30s
        }

        super.release();
        setRot(90, getRot().y);
    }

    @Override
    protected void updateAABB() {
        if (getHand() != null) {
            int i = getHand().getHand();
            XrHandTransform transform = XrRenderer.getHandTransform(i);

            aabb.set(model.getAABB());
            aabb.applyMatrix(new Matrix4f().rotate(Client.getInstance().camera.getRot()).rotate(transform.rot()));
            aabb.translate(getPos());
        } else {
            super.updateAABB();
        }
    }

    @Override
    public EntityRegistry getType() {
        return EntityRegistry.UNKNOWN;
    }

    @Override
    public boolean shouldRenderOutline() {
        return licked || super.shouldRenderOutline();
    }

    @Override
    public int getOutlineColor() {
        return licked ? (isGood ? Colors.LIME.argb : Colors.RED.argb) : super.getOutlineColor();
    }

    public void lick() {
        isGood |= !licked && Math.random() < goodChanceLicked;
        licked = true;
        getWorld().playSound(new Resource("candy", "lick.ogg"), SoundCategory.ENTITY, pos).pitch(Maths.range(0.8f, 1.2f));
    }
}
