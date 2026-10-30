package view;

import interface_adapter.ViewManagerModel;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Switches the visible screen whenever the ViewManagerModel's state changes.
 */
public class ViewManager implements PropertyChangeListener {
    private final JPanel views;
    private final CardLayout cardLayout;

    public ViewManager(JPanel views, CardLayout cardLayout, ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;
        viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            cardLayout.show(views, (String) evt.getNewValue());
        }
    }
}
