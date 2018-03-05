package kornell.gui.client.presentation.welcome;

import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;

public class WelcomePresenter implements WelcomeView.Presenter {
    private WelcomeView view;

    public WelcomePresenter(ClientFactory clientFactory) {
        view = clientFactory.getViewFactory().getWelcomeView();
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        Widget welcomeView = getView().asWidget();
        return welcomeView;
    }

    private WelcomeView getView() {
        return view;
    }

}
