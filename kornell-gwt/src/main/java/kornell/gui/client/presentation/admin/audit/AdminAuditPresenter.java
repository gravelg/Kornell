package kornell.gui.client.presentation.admin.audit;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.EntityChangedEventsTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.forms.FormHelper;

public class AdminAuditPresenter implements AdminAuditView.Presenter {
	Logger logger = Logger.getLogger(AdminAuditPresenter.class.getName());
	private AdminAuditView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private EntityChangedEventsTO entityChangedEventsTO;
	private String pageSize = "20";
	private String pageNumber = "1";
	private String searchTerm = "";
	private boolean asc = true;
	private String orderBy = "";
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
	public String getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String getPageNumber() {
		return pageNumber;
	}

	@Override
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;	
	}

	@Override
	public void updateData() {
		getEntitiesChanged();
	}

	@Override
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public boolean getAsc() {
		return asc;
	}
	
}