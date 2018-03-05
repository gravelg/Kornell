package kornell.gui.client.presentation.admin.institution.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.entity.InstitutionAdminRole;
import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.entity.Roles;
import kornell.core.to.RoleTO;
import kornell.core.to.RolesTO;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.forms.formfield.PeopleMultipleSelect;
import kornell.gui.client.util.view.KornellNotification;

public class GenericInstitutionAdminsView extends Composite {
    interface MyUiBinder extends UiBinder<Widget, GenericInstitutionAdminsView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

    private KornellSession session;
    boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

    PeopleMultipleSelect peopleMultipleSelect;

    @UiField
    Form form;
    @UiField
    FlowPanel adminsFields;
    @UiField
    Button btnOK;
    @UiField
    Button btnCancel;

    private Institution institution;
    private EventBus bus;

    public GenericInstitutionAdminsView(final KornellSession session, EventBus bus,
            kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter presenter,
            Institution institution) {
        this.session = session;
        this.bus = bus;
        this.institution = institution;
        initWidget(uiBinder.createAndBindUi(this));

        // i18n
        btnOK.setText("Salvar Alterações");
        btnCancel.setText("Cancelar Alterações");

        initData();
    }

    public void initData() {
        adminsFields.clear();
        FlowPanel fieldPanelWrapper = new FlowPanel();
        fieldPanelWrapper.addStyleName("fieldPanelWrapper");

        FlowPanel labelPanel = new FlowPanel();
        labelPanel.addStyleName("labelPanel");
        Label lblLabel = new Label("Administradores da Instituição");
        lblLabel.addStyleName("lblLabel");
        labelPanel.add(lblLabel);
        fieldPanelWrapper.add(labelPanel);

        bus.fireEvent(new ShowPacifierEvent(true));
        session.institution(institution.getUUID()).getAdmins(RoleCategory.BIND_WITH_PERSON, new Callback<RolesTO>() {
            @Override
            public void ok(RolesTO to) {
                for (RoleTO roleTO : to.getRoleTOs()) {
                    String item = roleTO.getUsername();
                    if (roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())) {
                        item += " (" + roleTO.getPerson().getFullName() + ")";
                    }
                    peopleMultipleSelect.addItem(item, roleTO.getPerson().getUUID());
                }
                bus.fireEvent(new ShowPacifierEvent(false));
            }
        });
        peopleMultipleSelect = new PeopleMultipleSelect(session);
        fieldPanelWrapper.add(peopleMultipleSelect.asWidget());
        adminsFields.add(fieldPanelWrapper);
    }

    @UiHandler("btnOK")
    void doOK(ClickEvent e) {
        if (session.isInstitutionAdmin()) {
            Roles roles = entityFactory.newRoles().as();
            List<Role> rolesList = new ArrayList<Role>();
            ListBox multipleSelect = peopleMultipleSelect.getMultipleSelect();
            for (int i = 0; i < multipleSelect.getItemCount(); i++) {
                String personUUID = multipleSelect.getValue(i);
                Role role = entityFactory.newRole().as();
                InstitutionAdminRole institutionAdminRole = entityFactory.newInstitutionAdminRole().as();
                role.setPersonUUID(personUUID);
                role.setRoleType(RoleType.institutionAdmin);
                institutionAdminRole.setInstitutionUUID(institution.getUUID());
                role.setInstitutionAdminRole(institutionAdminRole);
                rolesList.add(role);
            }
            roles.setRoles(rolesList);
            session.institution(institution.getUUID()).updateAdmins(roles, new Callback<Roles>() {
                @Override
                public void ok(Roles to) {
                    KornellNotification.show("Os administradores da instituição foram atualizados com sucesso.");
                }
            });
        }

    }

    @UiHandler("btnCancel")
    void doCancel(ClickEvent e) {
        initData();
    }

}