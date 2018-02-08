package kornell.gui.client.presentation.terms;

import com.google.gwt.user.client.ui.IsWidget;

public interface TermsView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);
}
