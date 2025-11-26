package candy;

import cinnamon.Client;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.text.Style;
import cinnamon.text.Text;
import cinnamon.utils.Alignment;
import cinnamon.world.Hud;

public class CandyHud extends Hud {

    @Override
    public void render(MatrixStack matrices, float delta) {
        Text.of("Score: ").append(((CandyWorld) Client.getInstance().world).getScore()).withStyle(Style.EMPTY.outlined(true))
                .render(VertexConsumer.MAIN, matrices, Client.getInstance().window.scaledWidth / 2f, 4f, Alignment.TOP_CENTER);
    }
}
