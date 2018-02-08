package kornell.gui.client.presentation.profile;

import com.google.gwt.user.client.ui.IsWidget;

public interface ProfileView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);
}
