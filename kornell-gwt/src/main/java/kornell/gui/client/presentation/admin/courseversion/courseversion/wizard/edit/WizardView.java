package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;

public class WizardView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, WizardView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private List<WizardSideItemLabel> sideItems;
	private HashMap<String, WizardSideItemLabel> sidePanelItemsMap;
	private static String HIGHLIGHT_CLASS = "highlightText";
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private KornellFormFieldWrapper name;
	private List<KornellFormFieldWrapper> fields;

	@UiField
	FlowPanel wizardPanel;
	@UiField	
	FlowPanel sideWrapper;
	@UiField	
	ScrollPanel sideItemsScroll;
	@UiField	
	FlowPanel sidePanel;
	@UiField
	WizardSlideView wizardSlideView;
	@UiField
	Button btnNewTopic;
	@UiField
	Button btnNewSlide;
	@UiField
	Button btnNewSlideQuiz;


	private Wizard wizard;
	private CourseVersion courseVersion;
	private Presenter presenter;
	
	private String changedString = "(*) ";

	private Label selectedLabel;

	public WizardView(final KornellSession session, EventBus bus) {
		this.session = session;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
		WizardUtils.createIcon(btnNewTopic, "fa-folder-open");
		WizardUtils.createIcon(btnNewSlide, "fa-newspaper-o");
		WizardUtils.createIcon(btnNewSlideQuiz, WizardUtils.getClasForWizardSlideItemViewIcon(WizardSlideItemType.QUIZ));
	}

	public void init(CourseVersion courseVersion, Wizard wizard, Presenter presenter) {
		this.courseVersion = courseVersion;
		this.wizard = wizard;
		this.presenter = presenter;
		updateSidePanel();
		wizardSlideView.setPresenter(presenter);
		wizardSlideView.updateSlidePanel();
		createSlide(true);
	}
	
	@UiHandler("btnNewTopic")
	void doNewTopic(ClickEvent e) {
		if(WizardUtils.wizardElementHasValueChanged(presenter.getSelectedWizardElement())){
			KornellNotification.show("Salve ou descarte as alterações antes de criar um novo tópico.", AlertType.WARNING);
			return;
		}
		WizardTopic wizardTopic = WizardUtils.newWizardTopic();
		wizardTopic.setOrder(wizard.getWizardTopics().size());
		wizard.getWizardTopics().add(wizardTopic);
		WizardElement prevWizardElement = WizardUtils.getPrevWizardElement(wizard, wizardTopic);
		if(prevWizardElement != null){
			wizardTopic.setBackgroundURL(prevWizardElement.getBackgroundURL());
			wizardTopic.getWizardSlides().get(0).setBackgroundURL(prevWizardElement.getBackgroundURL());
		}
		presenter.wizardElementClicked(wizardTopic);
	}
	
	@UiHandler("btnNewSlide")
	void doNewSlide(ClickEvent e) {
		createSlide(false);
	}
	
	@UiHandler("btnNewSlideQuiz")
	void doNewSlideQuiz(ClickEvent e) {
		createSlide(true);
	}

	private void createSlide(boolean isQuiz) {
		if(WizardUtils.wizardElementHasValueChanged(presenter.getSelectedWizardElement())){
			KornellNotification.show("Salve ou descarte as alterações antes de criar um novo slide.", AlertType.WARNING);
			return;
		}
		WizardSlide newWizardSlide = isQuiz 
				? WizardUtils.newWizardSlideQuiz() 
				: WizardUtils.newWizardSlide();
		WizardElement prevWizardElement;
		for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
			WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
			if(selectedWizardElement.getUUID().equals(wizardTopic.getUUID())){
				newWizardSlide.setOrder(wizardTopic.getWizardSlides().size());
				newWizardSlide.setParentOrder((wizardTopic.getOrder()+1)+"");
				wizardTopic.getWizardSlides().add(newWizardSlide);
				
				createNewSlide(newWizardSlide, wizardTopic);
				return;
			}
			int index = 0;
			for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
				if(selectedWizardElement.getUUID().equals(wizardSlide.getUUID())){
					newWizardSlide.setOrder(selectedWizardElement.getOrder()+1);
					newWizardSlide.setParentOrder((wizardTopic.getOrder()+1)+"");
					wizardTopic.getWizardSlides().add(index+1, newWizardSlide);
					for(int i = index+1; i < wizardTopic.getWizardSlides().size(); i++){
						wizardTopic.getWizardSlides().get(i).setOrder(i);
					}
					createNewSlide(newWizardSlide, wizardTopic);
					return;
				}	
				index++;
			}
		}
	}

	private void createNewSlide(WizardSlide newWizardSlide, final WizardTopic wizardTopic) {
		WizardElement prevWizardElement;
		prevWizardElement = WizardUtils.getPrevWizardElement(wizard, newWizardSlide);
		if(prevWizardElement != null){
			newWizardSlide.setBackgroundURL(prevWizardElement.getBackgroundURL());
		}
		newWizardSlide.setValueChanged(false);

		presenter.wizardElementClicked(newWizardSlide);
	}

	public void updateSidePanel() {
		sidePanel.clear();
		sideItems = new ArrayList<WizardSideItemLabel>();
		sidePanelItemsMap = new HashMap<String, WizardSideItemLabel>();

		WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
		
		

		Tree tree = new Tree();
		/*TreeItem topicTreeItxem = new TreeItem(new WizardSideItemLabel("Grand Parent", false, true));
		root.addItem(new WizardSideItemLabel("Parent 1", true, true));
		root.addItem(new WizardSideItemLabel("Parent 2", true, true));

		TreeItem parent3 = root.addItem(new WizardSideItemLabel("Parent 3", true, true));
		parent3.addItem(new WizardSideItemLabel("Parent 3.1", true, true));
		parent3.addItem(new WizardSideItemLabel("Parent 3.2", true, true));

		List<TreeItem> treeItems = new ArrayList<TreeItem>();
		treeItems.add(tree.getItem(0));
		int number = 1;
		while (!treeItems.isEmpty()) {
			TreeItem item = treeItems.remove(0);
			for (int i = 0; i < item.getChildCount(); i++) {
				treeItems.add(item.getChild(i));
			}
			item.setState(true);
		}
		WizardSideItemLabel labelTopic, labelSlide;
		TreeItem topicTreeItem;
		if(selectedWizardElement != null){
			for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
				labelTopic = createSidePanelItem(wizard, selectedWizardElement, null, wizardTopic);
				topicTreeItem = new TreeItem(labelTopic);
				for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
					labelSlide = createSidePanelItem(wizard, selectedWizardElement, wizardTopic, wizardSlide);
					topicTreeItem.addItem(new TreeItem(labelSlide));
				}
				tree.addItem(topicTreeItem);
			}
			for(int i = 0; i < tree.getItemCount(); i++){
				
			}
			sidePanelItemsMap.get(selectedWizardElement.getUUID()).addStyleName("selected");
			sidePanelItemsMap.get(selectedWizardElement.getUUID()).getElement().scrollIntoView();
		}
		
		*/
		

		if(selectedWizardElement != null){
			for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
				createSidePanelItem(wizard, selectedWizardElement, null, wizardTopic);
				for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
					createSidePanelItem(wizard, selectedWizardElement, wizardTopic, wizardSlide);				
				}
			}
			sidePanelItemsMap.get(selectedWizardElement.getUUID()).addStyleName("selected");
			sidePanelItemsMap.get(selectedWizardElement.getUUID()).getElement().scrollIntoView();
		}

		sidePanel.add(tree);

	}

	private WizardSideItemLabel createSidePanelItem(Wizard wizard, WizardElement selectedWizardElement, final WizardTopic parentWizardElement,
			WizardElement wizardElement) {
		final WizardSideItemLabel label = new WizardSideItemLabel("", false, false);
		label.addStyleName("sidePanelItem");
		label.addClickHandler(new ClickHandler() {
			boolean enableClick = true;
			@Override
			public void onClick(ClickEvent event) {
				if(!enableClick || 
						(selectedWizardElement != null && 
								selectedWizardElement.getUUID().equals(wizardElement.getUUID()))){
					return;
				}
				if(WizardUtils.wizardElementHasValueChanged(selectedWizardElement)){
					KornellNotification.show("Salve ou descarte as alterações antes de trocar de slide.", AlertType.WARNING);
					return;
				}
				wizardSlideView.displaySlidePanel(false);	
				bus.fireEvent(new ShowPacifierEvent(true));
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						for (Label lbl : sideItems) {
							lbl.removeStyleName("selected");
						}
						label.addStyleName("selected");
						setLabelContent(wizardElement, label);
						presenter.wizardElementClicked(wizardElement);
					}
				});
			}
		});
		if(parentWizardElement != null){
			label.addStyleName("marginLeft25");
		}
		label.getElement().setAttribute("data-wizard-element-uuid", wizardElement.getUUID());
		setLabelContent(wizardElement, label);
		sidePanel.add(label);
		sideItems.add(label);
		
		return label;
	}

	private void setLabelContent(WizardElement wizardElement, final WizardSideItemLabel label) {
		String valueChanged = WizardUtils.wizardElementHasValueChanged(wizardElement) ? changedString : "";
		String title = valueChanged + 
				(wizardElement.getParentOrder() == null ? 
						"Tópico " : 
						("Slide " + wizardElement.getParentOrder() + ".")) +
				(wizardElement.getOrder()+1); 
		String labelText = span(title, HIGHLIGHT_CLASS) + separator(true) + span(wizardElement.getTitle(), PLAIN_CLASS);
		sidePanelItemsMap.put(wizardElement.getUUID(), label);
		label.getElement().setInnerHTML(labelText);
	}

	private String separator(boolean lineBreak) {
		return separator(lineBreak, false);
	}

	private String separator(boolean lineBreak, boolean dash) {
		return lineBreak ? "<br>" : (dash ? "&nbsp;&nbsp;-&nbsp;&nbsp;" : "&nbsp;&nbsp;&nbsp;");
	}

	private String span(String str, String className) {
		return "<span class=\""+className+"\">"+str+"</span>";
	}

	public void toggleViewMode(boolean isViewModeOn) {
		this.sideWrapper.setVisible(!isViewModeOn);
	}

	public void displaySlidePanel(boolean display) {
		this.wizardSlideView.displaySlidePanel(display);		
	}

	public void updateSlidePanel() {
		this.wizardSlideView.updateSlidePanel();		
	}

	public void refreshSlidePanel() {
		this.wizardSlideView.refreshForm();		
	}
}