package kornell.gui.client.presentation.admin.audit;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.event.EntityChanged;
import kornell.core.to.EntityChangedEventsTO;
import kornell.gui.client.util.view.table.PaginationPresenter;

public interface AdminAuditView extends IsWidget {
	public interface Presenter extends PaginationPresenter<EntityChanged> {
	}
	void setPresenter(AdminAuditView.Presenter presenter);
	void setEntitiesChangedEvents(EntityChangedEventsTO entityChangedEventsTO);
	EntityChangedEventsTO getEntityChangedEventsTO();
}