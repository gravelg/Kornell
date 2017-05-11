package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardMock;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;

public class AdminCourseVersionContentPresenter implements AdminCourseVersionContentView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionContentPresenter.class.getName());
	private AdminCourseVersionContentView view;
	private KornellSession session;
	private PlaceController placeController;
	private EventBus bus;
	Place defaultPlace;
	private ViewFactory viewFactory;

	private Wizard wizard;
	private WizardElement selectedWizardElement;

	public AdminCourseVersionContentPresenter(KornellSession session, PlaceController placeController, EventBus bus,
			Place defaultPlace, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.bus = bus;
		this.defaultPlace = defaultPlace;
		this.viewFactory = viewFactory;
	}

	@Override
	public void init(CourseVersion courseVersion, ContentSpec contentSpec) {
		if (session.isInstitutionAdmin()) {
			view = viewFactory.getAdminCourseVersionContentView();
			view.setPresenter(this);
			
			boolean isWizardVersion = ContentSpec.WIZARD.equals(contentSpec);
			if(isWizardVersion){			
				wizard = WizardMock.mockWizard();
				selectedWizardElement = wizard.getWizardTopics().get(0).getWizardSlides().get(0);
				view.init(courseVersion, wizard);
			} else {			
				view.init(courseVersion, null);
			}
			
		} else {
			logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void wizardElementClicked(WizardElement wizardElement) {
		view.getWizardView().displaySlidePanel(false);
		this.selectedWizardElement = wizardElement;	
		reorderItems();
		view.getWizardView().updateSidePanel();
		view.getWizardView().updateSlidePanel();
		view.getWizardView().displaySlidePanel(true);	
		bus.fireEvent(new ShowPacifierEvent(false));
	}

	@Override
	public AdminCourseVersionContentView getView() {
		return view;
	}

	@Override
	public void valueChanged(boolean valueHasChanged) {
		if(selectedWizardElement != null){
			valueChanged(selectedWizardElement, valueHasChanged);
		}
	}

	@Override
	public void valueChanged(WizardElement wizardElement, boolean valueHasChanged) {
		wizardElement.setValueChanged(valueHasChanged);
		view.getWizardView().updateSidePanel();
	}
	
	@Override
	public WizardElement getSelectedWizardElement(){
		return selectedWizardElement;
	}
	
	@Override
	public Wizard getWizard(){
		return wizard;
	}

	@Override
	public void deleteSlide() {
		
		WizardElement selectedElement = getSelectedWizardElement();
		WizardTopic wizardTopic = null;


		WizardElement nextElement = WizardUtils.getNextWizardElement(wizard, selectedElement);
		WizardElement prevElement = WizardUtils.getPrevWizardElement(wizard, selectedElement);
		selectedWizardElement = null;
		if(nextElement != null){
			selectedWizardElement = nextElement;
		} else if(prevElement != null){
			selectedWizardElement = prevElement;
		} else {
			selectedWizardElement = null;
		}
		if(selectedElement instanceof WizardTopic){
			wizardTopic = (WizardTopic) selectedElement;
			wizard.getWizardTopics().remove(wizardTopic);
		} else {
			for(WizardTopic topic : wizard.getWizardTopics()){
				int i = 0;
				for(WizardSlide slide : topic.getWizardSlides()){
					if(selectedElement.getUUID().equals(slide.getUUID())){
						topic.getWizardSlides().remove(slide);
						if(topic.getWizardSlides().size() == 0 || i == topic.getWizardSlides().size()){
							selectedWizardElement = prevElement;
						}
					}
					i++;
				}
			}
		}
		//@TODO CALLBACK
		reorderItems();
		view.getWizardView().updateSidePanel();
		view.getWizardView().updateSlidePanel();
		view.getWizardView().refreshSlidePanel();
		//@TODO CALLBACK
		
	}

	@Override
	public void moveSlideDown() {
		WizardTopic topic, nextTopic;
		WizardSlide slide;
		if(selectedWizardElement instanceof WizardTopic){
			topic = (WizardTopic) selectedWizardElement;
			wizard.getWizardTopics().remove(topic);
			wizard.getWizardTopics().add(topic.getOrder()+1, topic);
		} else {
			topic = (WizardTopic) WizardUtils.getParentWizardElement(wizard, selectedWizardElement);
			slide = (WizardSlide) selectedWizardElement;
			if(slide.getOrder() == topic.getWizardSlides().size() - 1){
				nextTopic = (WizardTopic) WizardUtils.getNextWizardElement(wizard, topic.getWizardSlides().get(topic.getWizardSlides().size()-1));
				topic.getWizardSlides().remove(slide);
				nextTopic.getWizardSlides().add(0, slide);
			} else {
				topic.getWizardSlides().remove(slide);
				topic.getWizardSlides().add(slide.getOrder()+1, slide);
			}
		}
		reorderItems();
		view.getWizardView().updateSidePanel();
		view.getWizardView().refreshSlidePanel();
	}

	@Override
	public void moveSlideUp() {
		WizardTopic topic, prevTopic;
		WizardSlide slide;
		if(selectedWizardElement instanceof WizardTopic){
			topic = (WizardTopic) selectedWizardElement;
			wizard.getWizardTopics().remove(topic);
			wizard.getWizardTopics().add(topic.getOrder()-1, topic);
		} else {
			topic = (WizardTopic) WizardUtils.getParentWizardElement(wizard, selectedWizardElement);
			slide = (WizardSlide) selectedWizardElement;
			if(slide.getOrder() == 0){
				WizardElement element = WizardUtils.getPrevWizardElement(wizard, topic);
				if(element instanceof WizardTopic){
					prevTopic = (WizardTopic) element;
				} else {
					prevTopic = (WizardTopic) WizardUtils.getParentWizardElement(wizard, element);
				}
				topic.getWizardSlides().remove(slide);
				prevTopic.getWizardSlides().add(prevTopic.getWizardSlides().size(), slide);
			} else {
				topic.getWizardSlides().remove(slide);
				topic.getWizardSlides().add(slide.getOrder()-1, slide);
			}
		}
		reorderItems();
		view.getWizardView().updateSidePanel();
		view.getWizardView().refreshSlidePanel();
	}

	
	private void reorderItems() {
		int topicIndex = 0, slideIndex = 0;
		for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
			wizardTopic.setOrder(topicIndex);
			slideIndex = 0;
			for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
				wizardSlide.setOrder(slideIndex);
				wizardSlide.setParentOrder((topicIndex + 1) + "");
				slideIndex++;
			}
			topicIndex++;
		}
	}
}