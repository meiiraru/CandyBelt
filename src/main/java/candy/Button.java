package candy;

import cinnamon.registry.EntityRegistry;
import cinnamon.utils.Resource;
import cinnamon.world.entity.Entity;

import java.util.UUID;

public class Button extends Entity {

    protected final Runnable toRun;

    public Button(Runnable toRun) {
        super(UUID.randomUUID(), new Resource("candy", "button/model.obj"));
        this.toRun = toRun;
    }

    @Override
    public EntityRegistry getType() {
        return EntityRegistry.UNKNOWN;
    }

    public void press() {
        getAnimation("1").play();
        toRun.run();
    }
}
