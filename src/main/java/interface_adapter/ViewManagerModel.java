package interface_adapter;

/**
 * Tracks which view is currently on screen. Its state is the active view's name.
 */
public class ViewManagerModel extends ViewModel<String> {

    public ViewManagerModel() {
        super("view manager");
        setState("");
    }
}
