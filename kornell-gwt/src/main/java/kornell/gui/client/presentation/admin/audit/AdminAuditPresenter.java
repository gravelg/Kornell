package kornell.gui.client.presentation.admin.audit;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.event.EntityChanged;
import kornell.core.to.EntityChangedEventsTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.table.PaginationPresenterImpl;

public class AdminAuditPresenter extends PaginationPresenterImpl<EntityChanged> implements AdminAuditView.Presenter {
	Logger logger = Logger.getLogger(AdminAuditPresenter.class.getName());
	private AdminAuditView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private EntityChangedEventsTO entityChangedEventsTO;
	private EventBus bus;


	public AdminAuditPresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory, EventBus bus) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		this.bus = bus;
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isPlatformAdmin()) {
			initializeProperties("eventFiredAt");
			view = getView();
			view.setPresenter(this);
			entityChangedEventsTO = view.getEntityChangedEventsTO();
			if(entityChangedEventsTO == null){
				view.setEntitiesChangedEvents(null); 
			} else {
				view.setEntitiesChangedEvents(entityChangedEventsTO);
			}     
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	private void getEntitiesChanged() {
		if(StringUtils.isNone(searchTerm)){
			view.setEntitiesChangedEvents(null);
		} else {
			bus.fireEvent(new ShowPacifierEvent(true));
			session.events().getEntityChangedEvents(searchTerm, pageSize, pageNumber,  new Callback<EntityChangedEventsTO>() {
	  			@Override
	  			public void ok(EntityChangedEventsTO to) {
	  				entityChangedEventsTO = to;
	  				view.setEntitiesChangedEvents(entityChangedEventsTO);
	  				bus.fireEvent(new ShowPacifierEvent(false));
					updateProperties();
	  			}
	  		});
		}
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminAuditView getView() {
		return viewFactory.getAdminAuditView();
	}

	@Override
	public void updateData() {
		getEntitiesChanged();
	}

	@Override
	public String getClientPropertyName(String property){
		return session.getAdminHomePropertyPrefix() +
				"audit" + ClientProperties.SEPARATOR +
				property;
	}

	@Override
	public int getTotalRowCount() {
		return StringUtils.isNone(getSearchTerm()) ? entityChangedEventsTO.getCount() : entityChangedEventsTO.getSearchCount();
	}

	@Override
	public List<EntityChanged> getRowData() {
		return entityChangedEventsTO.getEntitiesChanged();
	}
	
}