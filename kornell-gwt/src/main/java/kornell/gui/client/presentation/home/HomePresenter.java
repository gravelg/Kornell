package kornell.gui.client.presentation.home;

import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;

public class HomePresenter implements HomeView.Presenter {
    HomeView homeView;

    public HomePresenter(ClientFactory clientFactory) {
        homeView = clientFactory.getViewFactory().getHomeView();
        homeView.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return homeView.asWidget();
    }
}