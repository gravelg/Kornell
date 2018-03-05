package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView.Presenter;

public class GenericCourseClassReportsView extends Composite {
    interface MyUiBinder extends UiBinder<Widget, GenericCourseClassReportsView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

    private EventBus bus;
    private KornellSession session;
    boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

    @UiField
    FlowPanel reportsPanel;

    public GenericCourseClassReportsView(final KornellSession session, EventBus bus, Presenter presenter,
            CourseClassTO courseClassTO) {
        this.session = session;
        this.bus = bus;
        initWidget(uiBinder.createAndBindUi(this));
        initData();
    }

    public void initData() {
        reportsPanel.setVisible(false);
        reportsPanel.add(getReportPanel());
        reportsPanel.setVisible(true);

    }

    private FlowPanel getReportPanel() {
        FlowPanel reportPanel = new FlowPanel();
        reportPanel.addStyleName("reportPanel");
        reportPanel.add(getReportInfo());
        reportPanel.add(getReportTableHeader());
        reportPanel.add(getReportTableContent());

        return reportPanel;
    }

    private FlowPanel getReportInfo() {
        FlowPanel reportInfo = new FlowPanel();
        reportInfo.addStyleName("titlePanel");

        Label infoTitle = new Label("Relatórios");
        infoTitle.addStyleName("title");
        reportInfo.add(infoTitle);

        Label infoText = new Label("Geração dos relatórios administrativos.");
        infoText.addStyleName("subTitle");
        reportInfo.add(infoText);

        return reportInfo;
    }

    private FlowPanel getReportTableContent() {
        FlowPanel reportContentPanel = new FlowPanel();
        reportContentPanel.addStyleName("reportContentPanel");
        reportContentPanel.add(new GenericCourseClassReportItemView(bus, session, session.getCurrentCourseClass(),
                GenericCourseClassReportItemView.CERTIFICATE));
        reportContentPanel.add(new GenericCourseClassReportItemView(bus, session, session.getCurrentCourseClass(),
                GenericCourseClassReportItemView.COURSE_CLASS_INFO));
        if (session.isInstitutionAdmin()) {
            reportContentPanel.add(new GenericCourseClassReportItemView(bus, session, session.getCurrentCourseClass(),
                    GenericCourseClassReportItemView.COURSE_CLASS_AUDIT));
        }

        return reportContentPanel;
    }

    private FlowPanel getReportTableHeader() {
        FlowPanel reportHeaderPanel = new FlowPanel();
        reportHeaderPanel.addStyleName("reportHeaderPanel");

        reportHeaderPanel.add(getHeaderButton("Relatório", "btnReport", "btnReportHeader"));
        reportHeaderPanel.add(getHeaderButton("Geração", "btnGenerate", "btnReportHeader"));
        reportHeaderPanel.add(getHeaderButton("Download", "btnDownload", "btnReportHeader"));

        return reportHeaderPanel;
    }

    private Button getHeaderButton(String label, String styleName, String styleNameGlobal) {
        Button btn = new Button(label);
        btn.removeStyleName("btn");
        btn.addStyleName(styleNameGlobal);
        btn.addStyleName(styleName);
        return btn;
    }

}