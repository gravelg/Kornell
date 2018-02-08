package kornell.gui.client.presentation.admin.institution.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.util.StringUtils;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;

public class GenericInstitutionReportItemView extends Composite {
    interface MyUiBinder extends UiBinder<Widget, GenericInstitutionReportItemView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private FormHelper formHelper = GWT.create(FormHelper.class);
    private String ADMIN_IMAGES_PATH = StringUtils.mkurl(ClientConstants.IMAGES_PATH, "admin/");
    private KornellSession session;
    private String type;
    private String name;
    private String description;
    public static final String BILLING = "billing";

    @UiField
    Image certificationIcon;
    @UiField
    Label lblName;
    @UiField
    Label lblDescription;
    @UiField
    FlowPanel optionPanel;
    @UiField
    Anchor lblGenerate;
    @UiField
    Anchor lblDownload;
    private ListBox periodListBox;

    public GenericInstitutionReportItemView(EventBus eventBus, KornellSession session, String type) {
        this.session = session;
        this.type = type;
        periodListBox = new ListBox();
        initWidget(uiBinder.createAndBindUi(this));
        display();
    }

    private void display() {
        if (BILLING.equals(this.type)) {
            this.name = "Relatório de utilização";
            this.description = "Escolha o período desejado na lista abaixo:";

            certificationIcon.setUrl(StringUtils.mkurl(ADMIN_IMAGES_PATH, type + ".png"));
            lblName.setText(name);
            lblDescription.setText(description);
            lblGenerate.setText("Gerar");
            lblGenerate.addStyleName("cursorPointer");

            addItemsToPeriodList();
            optionPanel.add(periodListBox);

            lblDownload.setText("-");
            lblDownload.removeStyleName("cursorPointer");
            lblDownload.addStyleName("anchorToLabel");
            lblDownload.setEnabled(false);

            lblGenerate.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    KornellNotification.show("Aguarde um instante...", AlertType.WARNING, 2000);
                    session.report().locationAssign("/report/institutionBilling",
                            "?institutionUUID=" + session.getInstitution().getUUID() + "&periodStart="
                                    + periodListBox.getValue() + "&periodEnd="
                                    + getNextPeriod(periodListBox.getValue()));
                }
            });
        }
    }

    private String getNextPeriod(String start) {
        String year = start.split("-")[0];
        int nextMonth = Integer.parseInt(start.split("-")[1]) + 1;
        if (nextMonth > 12)
            return (Integer.parseInt(year) + 1) + "-01";
        else
            return year + "-" + (nextMonth < 10 ? "0" + nextMonth : nextMonth);
    }

    private void addItemsToPeriodList() {
        String now = formHelper.dateToString(new Date());
        String next = getNextPeriod(now.split("-")[0] + "-" + now.split("-")[1]);
        List<String> dates = new ArrayList<String>();
        String date = formHelper.dateToString(session.getInstitution().getActivatedAt());
        date = date.split("-")[0] + "-" + date.split("-")[1];
        while (!date.equals(next)) {
            dates.add(date);
            date = getNextPeriod(date);
        }
        Collections.reverse(dates);
        for (String dateToAdd : dates) {
            periodListBox.addItem(dateToAdd, dateToAdd);
        }
    }

}
