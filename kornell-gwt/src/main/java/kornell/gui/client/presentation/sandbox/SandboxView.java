package kornell.gui.client.presentation.sandbox;

import com.google.gwt.user.client.ui.IsWidget;

public interface SandboxView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);

}