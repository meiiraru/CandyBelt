package candy;

import cinnamon.gui.Screen;
import cinnamon.gui.screens.MainMenu;
import cinnamon.gui.widgets.ContainerGrid;
import cinnamon.gui.widgets.types.Button;
import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.text.Text;
import cinnamon.utils.Resource;

public class CandyMainMenu extends Screen {

    @Override
    public void init() {
        //buttons
        ContainerGrid grid = new ContainerGrid(0, 0, 4);

        //play
        Button play = new MainMenu.MainButton(Text.of("Play"), button -> new CandyWorld().init());
        grid.addWidget(play);

        //how to play
        Button htp = new MainMenu.MainButton(Text.of("How to play"), button -> client.setScreen(new HowToPlayScreen(this)));
        grid.addWidget(htp);

        //credits
        Button credits = new MainMenu.MainButton(Text.of("Credits"), button -> client.setScreen(new CreditsScreen(this)));
        grid.addWidget(credits);

        //exit
        Button exitButton = new MainMenu.MainButton(Text.translated("gui.exit"), button -> client.window.exit());
        exitButton.setTooltip(Text.translated("gui.main_menu.exit.tooltip"));
        grid.addWidget(exitButton);

        //add grid to screen
        int y = (int) (height * 0.15f);
        grid.setPos((width - grid.getWidth()) / 2, y + (height - grid.getHeight() - y) / 2);
        grid.setStyle(MainMenu.GUI_STYLE);
        this.addWidget(grid);
    }

    @Override
    protected void renderChildren(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float s = 0.25f;
        VertexConsumer.MAIN.consume(GeometryHelper.quad(matrices, (width - 1230 * s) / 2f, 4, 1230 * s, 339 * s),
                new Resource("candy", "logo.png"));

        super.renderChildren(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta) {
        renderSolidBackground(0xFF202020);
    }
}
