package kornell.gui.client.presentation.sandbox.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.gui.client.presentation.sandbox.SandboxView;

public class GenericSandboxView extends Composite implements SandboxView {
    interface MyUiBinder extends UiBinder<Widget, GenericSandboxView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel panel;

    public GenericSandboxView(KornellSession session) {
        initWidget(uiBinder.createAndBindUi(this));
        panel.add(new Label("Loading some stuff..."));
        session.enrollment("8002f404-8488-4fb3-b69e-9828f6fe396b").isApproved(new Callback<String>() {
            @Override
            public void ok(String approved) {
                /*
                 * if(approved){ panel.add(new Label("Approved!!!!")); }else {
                 * panel.add(new Label("Not this time :(")); }
                 */

            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {

    }

}
