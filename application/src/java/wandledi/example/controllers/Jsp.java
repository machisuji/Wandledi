package wandledi.example.controllers;

import java.util.Collection;
import wandledi.example.models.BlogEntry;
import wandledi.example.pages.HomePage;

import static wandledi.java.Switchboard.linkTo;
import static wandledi.java.Switchboard.linkToUri;

/**Does exactly the same as Home, but with JSP.
 *
 * @author markus
 */
public class Jsp extends Home {

    public Jsp() {

        this.page = new ObsoletePage();
    }

    @Override
    public boolean isSpellController() {

        return false;
    }

    private class ObsoletePage extends HomePage {

        @Override
        public void beforeAction(String msg, String homeLink) {

            if (msg != null) {
                model.put("msg", msg);
            }
            model.put("homeLink", homeLink);
            model.put("css", linkToUri("/css/main.css"));
        }

        @Override
        public void index(Collection<BlogEntry> entries) {

            model.put("entries", entries);
            if (isLogin()) {
                model.put("href", linkTo("jsp", "post"));
                model.put("label", "Post Entry");
            } else {
                model.put("href", linkTo("jsp", "login"));
                model.put("label", "Login");
            }
        }
    }
}
