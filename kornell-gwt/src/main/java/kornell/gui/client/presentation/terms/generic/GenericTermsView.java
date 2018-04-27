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
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.entity.TermsLanguageItem;
import kornell.gui.client.util.entity.TermsLanguageItems;

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
                if (event.getNewPlace() instanceof TermsPlace) {
                    initData();
                }
            }
        });
        btnAgree.setText(constants.agreeTerms().toUpperCase());
        btnDontAgree.setText(constants.refuseTerms().toUpperCase());
    }

    private void initData() {
        if (session.getCurrentUser().getPerson().getTermsAcceptedOn() == null) {
            paint();
        } else {
            goStudy();
        }
    }

    private void paint() {
        clientFactory.getViewFactory().getMenuBarView().initPlaceBar(IconType.LEGAL, constants.termsTitle(),
                constants.termsDescription());
        titleUser.setText(session.getCurrentUser().getPerson().getFullName());
        if (session.getInstitution() != null) {
            String locale = ClientProperties.getLocaleCookie();
            if (locale == null) {
                locale = "pt_BR";
            }
            String terms = getTermsForLanguage(locale);
            if(StringUtils.isNone(terms)){
                goStudy();
            }
            txtTerms.getElement().setInnerHTML(terms);
            String skin = session.getInstitution().getSkin();
            boolean isLightSkin = skin == null || !skin.contains("_light");
            String barLogoFileName = "/logo300x80" + (isLightSkin ? "_light" : "") + ".png?1";
            institutionLogo.setUrl(session.getInstitutionAssetsURL() + barLogoFileName);
        }
    }

    private String getTermsForLanguage(String language) {
        try {
            AutoBeanFactory factory = GenericClientFactoryImpl.GUI_ENTITY_FACTORY;
            AutoBean<TermsLanguageItems> bean = AutoBeanCodex.decode(factory, TermsLanguageItems.class,
                    session.getInstitution().getTerms());
            TermsLanguageItems list = bean.as();
            for (TermsLanguageItem termsLanguageItem : list.getTermsLanguageItems()) {
                if (termsLanguageItem.getLanguage().equals(language)) {
                    return termsLanguageItem.getTerms();
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    @UiHandler("btnAgree")
    void handleClickAll(ClickEvent e) {
        session.user().acceptTerms(new Callback<UserInfoTO>() {
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
        if (session.getInstitution().isDemandsPersonContactDetails()) {
            placeCtrl.goTo(new ProfilePlace(session.getCurrentUser().getPerson().getUUID(), true));
        } else {
            placeCtrl.goTo(clientFactory.getDefaultPlace());
        }
    }

}