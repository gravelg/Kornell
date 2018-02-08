package kornell.gui.client.presentation.bar.generic;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.presentation.bar.PlaceBarView;

public class GenericPlaceBarView extends Composite implements PlaceBarView {
    interface MyUiBinder extends UiBinder<Widget, GenericPlaceBarView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    Icon icon;
    @UiField
    Label title;
    @UiField
    Label subtitle;
    @UiField
    FlowPanel btnPanel;

    public GenericPlaceBarView() {

        // if(placeCtrl)
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void clear() {
        this.icon.setVisible(false);
        this.title.setVisible(false);
        this.subtitle.setVisible(false);
        btnPanel.clear();
    }

    public void init(IconType iconType, String titleStr, String subtitleStr) {
        this.icon.setIcon(iconType);
        this.title.setText(titleStr);
        this.subtitle.setText(subtitleStr);
        this.icon.setVisible(true);
        this.title.setVisible(true);
        this.subtitle.setVisible(true);
        btnPanel.clear();
    }

    public void setWidgets(List<IsWidget> widgets, boolean alwaysShowWidgets) {
        btnPanel.clear();
        for (IsWidget widget : widgets) {
            btnPanel.add(widget);
        }
        if (alwaysShowWidgets) {
            btnPanel.setStyleName("btnPanel");
        } else {
            btnPanel.setStyleName("btnPanel hideOnSmallerScreens");
        }
    }

}