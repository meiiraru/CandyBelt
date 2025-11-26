package candy;

import cinnamon.gui.ParentedScreen;
import cinnamon.gui.Screen;
import cinnamon.gui.widgets.types.Label;
import cinnamon.render.MatrixStack;
import cinnamon.text.Text;
import cinnamon.utils.TextUtils;

public class CreditsScreen extends ParentedScreen {

    public CreditsScreen(Screen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void init() {
        addWidget(new Label(30, 30, TextUtils.parseColorFormatting(Text.of("""
        &nMeiiraru Akitsuki&r
                Game Developer, Engine Developer
        
        &nCindi Saicosque&r
                Storyboard, Art Director
        
        &nSounds&r
                pixabay.com
        """))));

        super.init();
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta) {
        renderSolidBackground(0xFF202020);
    }
}
