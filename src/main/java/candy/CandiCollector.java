package candy;

import cinnamon.registry.EntityRegistry;
import cinnamon.text.Style;
import cinnamon.text.Text;
import cinnamon.utils.Colors;
import cinnamon.utils.Resource;
import cinnamon.world.collisions.CollisionResult;
import cinnamon.world.entity.Entity;
import cinnamon.world.entity.PhysEntity;
import org.joml.Vector3f;

import java.util.UUID;

public class CandiCollector extends PhysEntity {

    protected int collectedGood = 0;
    protected int collectedBad = 0;

    public CandiCollector() {
        super(UUID.randomUUID(), new Resource("candy", "box/model.obj"));
    }

    @Override
    protected Text getHeadText() {
        return Text.empty()
                .append(Text.of("+").withStyle(Style.EMPTY.color(Colors.LIME)).append(collectedGood))
                .append("\n")
                .append(Text.of("-").withStyle(Style.EMPTY.color(Colors.RED)).append(collectedBad));
    }

    @Override
    protected void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    protected void collide(Entity entity, CollisionResult result, Vector3f toMove) {
        if (entity instanceof Candy candy) {
            if (candy.isGood) collectedGood++;
            else              collectedBad++;

            candy.remove();
            return;
        }

        super.collide(entity, result, toMove);
    }

    @Override
    public EntityRegistry getType() {
        return EntityRegistry.UNKNOWN;
    }

    public float getCurrentRate() {
        int total = collectedGood + collectedBad;
        return total == 0 ? 1f : (float) collectedGood / total;
    }

    public void reset() {
        collectedGood = 0;
        collectedBad = 0;
    }
}
