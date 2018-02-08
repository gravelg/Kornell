package kornell.gui.client.presentation.home;

import com.google.gwt.user.client.ui.IsWidget;

public interface HomeView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);

}