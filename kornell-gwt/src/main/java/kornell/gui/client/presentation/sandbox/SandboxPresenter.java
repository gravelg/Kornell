package kornell.gui.client.presentation.sandbox;

import com.google.gwt.user.client.ui.Widget;

public class SandboxPresenter implements SandboxView.Presenter {
    SandboxView homeView;

    public SandboxPresenter(SandboxView view) {
        homeView = view;
        homeView.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return homeView.asWidget();
    }

    public void setPlace(SandboxPlace place) {
        // TODO Auto-generated method stub

    }
}