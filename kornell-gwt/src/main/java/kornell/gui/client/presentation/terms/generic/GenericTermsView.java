package kornell.gui.client.presentation.terms.generic;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.terms.TermsView;

public class GenericTermsView extends Composite implements TermsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTermsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	@UiField
	Paragraph titleUser;
	@UiField
	Paragraph txtTerms;
	@UiField
	Button btnAgree;
	@UiField
	Button btnDontAgree;
	@UiField
	Image institutionLogo;

	private ClientFactory clientFactory;
	private KornellSession session;
	private PlaceController placeCtrl;
	private EventBus bus;



	public GenericTermsView(ClientFactory clientFactory) {
		this.bus = clientFactory.getEventBus();
		this.session = clientFactory.getKornellSession();
		this.placeCtrl = clientFactory.getPlaceController();
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		
		bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if(event.getNewPlace() instanceof TermsPlace){							
					initData();
				}
			}});
		btnAgree.setText(constants.agreeTerms().toUpperCase());
		btnDontAgree.setText(constants.refuseTerms().toUpperCase());
	}

	private void initData() {
		if(session.getCurrentUser().getPerson().getTermsAcceptedOn() == null)
			paint();
		else 
			goStudy();
	}

	private void paint() {
		clientFactory.getViewFactory().getMenuBarView().initPlaceBar(IconType.LEGAL, constants.termsTitle(), constants.termsDescription());
		titleUser.setText(session.getCurrentUser().getPerson().getFullName());
		if (session.getInstitution() != null) {
			txtTerms.getElement().setInnerHTML(session.getInstitution().getTerms());
			String skin = session.getInstitution().getSkin();
			boolean isLightSkin = skin == null || !skin.contains("_light");
			String barLogoFileName = "/logo300x80" + (isLightSkin ? "_light" : "") + ".png?1";
			institutionLogo.setUrl(session.getInstitutionAssetsURL() + barLogoFileName);
		}
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		session.user().acceptTerms(
				new Callback<UserInfoTO>() {
					@Override
					public void ok(UserInfoTO userInfo) {
						session.setCurrentUser(userInfo);
						goStudy();
					}
				});
	}

	@UiHandler("btnDontAgree")
	void handleClickInProgress(ClickEvent e) {
		bus.fireEvent(new LogoutEvent());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	private void goStudy() {
		if(session.getInstitution().isDemandsPersonContactDetails()){
			placeCtrl.goTo(new ProfilePlace(session.getCurrentUser().getPerson().getUUID(), true));
		} else {
			placeCtrl.goTo(clientFactory.getDefaultPlace());
		}
	}

}