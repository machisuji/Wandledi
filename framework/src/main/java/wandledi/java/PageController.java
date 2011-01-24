package wandledi.java;

import wandledi.java.html.Page;

/**
 *
 * @author Markus Kahl
 */
public abstract class PageController extends Controller implements SpellController {

    @Override
    public boolean isSpellController() {

        return true;
    }

    /**Returns this controller's page object responsible for transforming
     * each page.
     *
     * @return
     */
    public abstract Page getPage();
}
