package kornell.gui.client.presentation.terms;

import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;

public class TermsPresenter implements TermsView.Presenter {
    private TermsView view;

    public TermsPresenter(ClientFactory clientFactory) {
        view = clientFactory.getViewFactory().getTermsView();
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        Widget termsView = getView().asWidget();
        return termsView;
    }

    private TermsView getView() {
        return view;
    }

}
