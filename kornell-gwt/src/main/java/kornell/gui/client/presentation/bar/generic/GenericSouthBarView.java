package kornell.gui.client.presentation.bar.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.KornellSession;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.InstitutionType;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.event.HideSouthBarEventHandler;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.util.easing.Ease;
import kornell.gui.client.util.easing.Transitions;
import kornell.gui.client.util.easing.Updater;
import kornell.gui.client.util.view.Positioning;

public class GenericSouthBarView extends Composite implements SouthBarView, HideSouthBarEventHandler {

	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static final int NO_SOUTH_BAR = 0;
	private static final int ACTIVITY_BAR = 1;
	private static final int ADMIN_BAR = 2;
	private int currentSouthBar = 0;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	IsWidget barView = null;
	
	private AdminBarView adminBarView;

	@UiField
	FlowPanel southBar;

	private ClientFactory clientFactory;

	private ScrollPanel scrollPanel;
	private KornellSession session;

	public GenericSouthBarView(final ClientFactory clientFactory, ScrollPanel scrollPanel) {
		this.clientFactory = clientFactory;
		clientFactory.getEventBus().addHandler(HideSouthBarEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));
		this.scrollPanel = scrollPanel;
		this.session = clientFactory.getKornellSession();

		pickSouthBar(clientFactory.getPlaceController().getWhere());

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				pickSouthBar(newPlace);
			}
		});
	}

	private void pickSouthBar(final Place newPlace) {
		if(newPlace instanceof AdminPlace && clientFactory.getKornellSession().isInstitutionAdmin()){
			if(currentSouthBar != ADMIN_BAR){
				currentSouthBar = ADMIN_BAR;
				barView = getAdminBarView(newPlace);
				showSouthBar(barView);
			}
		} else if (newPlace instanceof ClassroomPlace && session.getCurrentCourseClass() != null && 
				session.getCurrentCourseClass().getEnrollment() != null &&
				EnrollmentState.enrolled.equals(session.getCurrentCourseClass().getEnrollment().getState()) &&
				!(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType()))) {
			if(currentSouthBar != ACTIVITY_BAR){
				currentSouthBar = ACTIVITY_BAR;
				barView = getActivityBarView();
				showSouthBar(barView);
			}
		} else {
			if(currentSouthBar != NO_SOUTH_BAR){
				currentSouthBar = NO_SOUTH_BAR;
				hideSouthBar(barView);
			}
		}
	}
	
	private void hideSouthBar(final IsWidget barView){
		scrollPanel.removeStyleName("offsetSouthBar");
		if(barView == null){
			setVisible(false);
			return;
		}
		barView.asWidget().getElement().getStyle().setProperty("bottom", "0px");
		Ease.out(Transitions.QUAD, new Updater() {
			@Override
			public void update(double progress) {
				int position = -((int) (Positioning.SOUTH_BAR * progress));
				barView.asWidget().getElement().getStyle().setProperty("bottom", position + "px");
				if(position == -Positioning.SOUTH_BAR){
					setVisible(false);
				}
			}
		}).run(Positioning.BAR_ANIMATION_LENGTH);
	}
	
	private void showSouthBar(final IsWidget barView){
		southBar.clear();
		barView.asWidget().getElement().getStyle().setProperty("bottom", (Positioning.SOUTH_BAR * -1) + "px");
		southBar.add(barView);
		this.setVisible(true);
		Ease.out(Transitions.QUAD, new Updater() {
			@Override
			public void update(double progress) {
				int position = ((int) (Positioning.SOUTH_BAR * progress)) - Positioning.SOUTH_BAR;
				barView.asWidget().getElement().getStyle().setProperty("bottom", position + "px");
			}
		}).run(Positioning.BAR_ANIMATION_LENGTH);
		scrollPanel.addStyleName("offsetSouthBar");
	}

	private ActivityBarView getActivityBarView() {
		return new GenericActivityBarView(clientFactory);
	}

	private AdminBarView getAdminBarView(Place newPlace) {
		if (adminBarView == null)
			adminBarView = new GenericAdminBarView(clientFactory);
		return adminBarView;
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
  public void onHideSouthBar(HideSouthBarEvent event) {
		pickSouthBar(clientFactory.getPlaceController().getWhere());
  }

}
