package kornell.gui.client.presentation.home.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellClient;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.home.HomeView;

public class GenericHomeView extends Composite implements HomeView {
    interface MyUiBinder extends UiBinder<Widget, GenericHomeView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public GenericHomeView(ClientFactory factory, EventBus eventBus, KornellClient client) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {

    }

}
