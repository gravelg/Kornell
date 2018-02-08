package kornell.gui.client.uidget;

import com.google.gwt.user.client.ui.Composite;

import kornell.core.lom.Actom;
import kornell.core.lom.ExternalPage;
import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;

public abstract class Uidget extends Composite {

    public static Uidget forActom(Actom actom) {
        if (actom == null)
            return null;
        if (actom instanceof ExternalPage)
            return new ExternalPageView((ExternalPage) actom);
        throw new IllegalArgumentException("Do not know how to view [" + actom + "]");
    }

    private ViewReadyEventHandler viewReadyEventHandler;

    public void onViewReady(ViewReadyEventHandler viewReadyEventHandler) {
        this.viewReadyEventHandler = viewReadyEventHandler;
    }

    protected void fireViewReady() {
        if (viewReadyEventHandler != null)
            viewReadyEventHandler.onViewReady(new ViewReadyEvent());
    }
}
