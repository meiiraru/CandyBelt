package candy;

import cinnamon.render.Camera;
import cinnamon.render.MatrixStack;
import cinnamon.world.entity.living.LocalPlayer;

public class CandyPlayer extends LocalPlayer {

    @Override
    protected void renderModel(Camera camera, MatrixStack matrices, float delta) {
        //super.renderModel(camera, matrices, delta);
    }

    @Override
    public void updateMovementFlags(boolean sneaking, boolean sprinting, boolean flying) {
        super.updateMovementFlags(false, false, true);
    }

    @Override
    public void impulse(float left, float up, float forwards) {
        super.impulse(0, 0, 0);
    }
}
