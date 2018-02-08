package kornell.gui.client.presentation.bar;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;

public interface MenuBarView extends IsWidget {
    public interface Presenter extends IsWidget {
    }

    void setPresenter(Presenter presenter);

    void display();

    boolean isVisible();

    void initPlaceBar(IconType iconType, String titleStr, String subtitleStr);

    void clearPlaceBar();

    void setPlaceBarWidgets(List<IsWidget> widgets);

    void setPlaceBarWidgets(List<IsWidget> widgets, boolean alwaysShowWidgets);
}
