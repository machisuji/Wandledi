package wandledi.java;

import wandledi.java.html.Pages;

/**
 *
 * @author Markus Kahl
 */
public abstract class PageController extends Controller {

    @Override
    public boolean isSpellController() {

        return true;
    }

    public abstract Pages getPages();
}
