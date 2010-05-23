package wandledi.java;

/**Contains request information for Wandledi.
 *
 * @author Markus Kahl
 */
public class WandlediRequest {

    private boolean viewless;
    private String view;

    /**
     * @return the viewless
     */
    public boolean isViewless() {
        return viewless;
    }

    /**
     * @param viewless the viewless to set
     */
    public void setViewless(boolean viewless) {
        this.viewless = viewless;
    }

    /**The file (jsp) which is the view to be rendered for this request.
     * 
     * @return the view
     */
    public String getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(String view) {
        this.view = view;
    }
}
