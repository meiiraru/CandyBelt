package candy;

import cinnamon.Cinnamon;
import cinnamon.Client;

public class Main {
    public static void main(String... args) {
        Client.mainScreen = CandyMainMenu::new;
        Cinnamon.TITLE = "Candy Belt VR";
        Cinnamon.ENABLE_XR = true;
        new Cinnamon(args).run();
    }
}
