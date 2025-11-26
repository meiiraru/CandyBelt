package candy;

import cinnamon.gui.ParentedScreen;
import cinnamon.gui.Screen;
import cinnamon.gui.widgets.types.Label;
import cinnamon.render.MatrixStack;
import cinnamon.text.Text;
import cinnamon.utils.TextUtils;

public class HowToPlayScreen extends ParentedScreen {

    public HowToPlayScreen(Screen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void init() {
        addWidget(new Label(20, 20, TextUtils.parseColorFormatting(Text.of("""
        &nHow to Play Candy Belt&r
            Each round you must catch &bcandies&r from the &bcandy belt&r using your hands
            (Use the trigger button on your controller to grab candies)
    
            After catching a candy, you must &elick&r it by bringing it close to your mouth
            (Bring the candy close to your headset to simulate licking)
    
            After licking, the candy may be &aGOOD&r or &cBAD&r
            &aGOOD&r candies give you points, so take it back to the candy belt
            &cBAD&r candies take away points, so throw them away from you
    
            After the round ends, you will pass to the next round with a higher difficulty
            but only if you passed the &dscore target&r for that round
            Try to get the highest score possible!
    
            To start the game, press the &cred button&r on your right while ingame
            that button starts the game, starts the next round,
            and restarts the game after a game over
        """))));

        super.init();
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta) {
        renderSolidBackground(0xFF202020);
    }
}
