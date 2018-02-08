package kornell.gui.client.presentation.bar;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface CourseBarView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);

    void updateSelection(Place place);

}
