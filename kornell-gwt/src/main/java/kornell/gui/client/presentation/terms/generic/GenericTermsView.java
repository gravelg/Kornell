package kornell.gui.client.presentation.terms.generic;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.Institution;
import kornell.core.shared.data.Person;
import kornell.core.shared.data.Registration;
import kornell.core.shared.to.RegistrationsTO;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.generic.GenericMenuLeftView;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GenericTermsView extends Composite implements TermsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTermsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Paragraph titleUser;
	@UiField
	Paragraph txtInstitutionName;
	@UiField
	Paragraph txtTerms;
	@UiField
	Button btnAgree;
	@UiField
	Button btnDontAgree;
	
	Registration registration;
	Institution institution;
	UserInfoTO user;

	private static String COURSES_ALL = "all";
	private static String COURSES_IN_PROGRESS = "inProgress";
	private static String COURSES_TO_START = "toStart";
	private static String COURSES_TO_ACQUIRE = "toAcquire";
	private static String COURSES_FINISHED = "finished";

	private KornellClient client;

	private PlaceController placeCtrl;
	private Place defaultPlace;
	private String displayCourses;

	private KornellConstants constants = GWT.create(KornellConstants.class);

	private GenericMenuLeftView menuLeftView;

	public GenericTermsView(KornellClient client, PlaceController placeCtrl, Place defaultPlace) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		this.defaultPlace = defaultPlace;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		// TODO i18n
		btnAgree.setText("Concordo".toUpperCase());
		btnDontAgree.setText("Não Concordo".toUpperCase());
	}

	private void initData() {
		client.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO userTO) {
				user = userTO;
				paint();
			}
		});
		
		//TODO: Improve client API (eg. client.registrations().getUnsigned();
		client.registrations().getUnsigned(new Callback<RegistrationsTO>() {
			@Override
			protected void ok(RegistrationsTO to) {
				Set<Entry<Registration, Institution>> entrySet = to
						.getRegistrationsWithInstitutions().entrySet();
				ArrayList<Entry<Registration, Institution>> regs = new ArrayList<Entry<Registration, Institution>>(
						entrySet);
				//TODO: Handle multiple unsigned terms
				if(regs.size() > 0) {
				Entry<Registration, Institution> e = regs.get(0);
				registration = e.getKey();
				institution = e.getValue();
				paint();
				}else {
					GWT.log("OPS! Should not be here if nothing to sign");
					goStudy();
				}
			}
		});
	}

	private void paint() {
		Person p = user.getPerson();
		titleUser.setText(p.getFullName());
		if(institution != null){
			txtTerms.getElement().setInnerHTML(institution.getTerms());
			txtInstitutionName.setText(institution.getName());
		}
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		client.institution(institution.getUUID()).acceptTerms(new Callback<Void>(){
			@Override
			protected void ok() {				
				goStudy();
			}
		});		
	}

	@UiHandler("btnDontAgree")
	void handleClickInProgress(ClickEvent e) {
		placeCtrl.goTo(new VitrinePlace());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}
	
	private void goStudy() {
		placeCtrl.goTo(defaultPlace);				
	}

}