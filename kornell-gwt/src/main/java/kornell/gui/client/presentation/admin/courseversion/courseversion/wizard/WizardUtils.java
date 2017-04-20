package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.core.client.GWT;

import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit.WizardSlideItemView;

public class WizardUtils {
	
	public static final WizardFactory WIZARD_FACTORY = GWT.create(WizardFactory.class);    
	
	public static Comparator<WizardSlideItemView> COMPARE_WIZARD_SLIDE_ITEM_VIEWS_BY_DISPLAY_ORDER = new Comparator<WizardSlideItemView>() {
        public int compare(WizardSlideItemView one, WizardSlideItemView other) {
            return one.getDisplayOrder().compareTo(other.getDisplayOrder());
        }
    };
	
	public static Comparator<WizardElement> COMPARE_WIZARD_ELEMENT_BY_ORDER = new Comparator<WizardElement>() {
        public int compare(WizardElement one, WizardElement other) {
            return one.getOrder().compareTo(other.getOrder());
        }
    };

	public static WizardElement findWizardElementByUUID(Wizard wizard, String wizardElementUUID){
		for(WizardTopic topic : wizard.getWizardTopics()){
			if(wizardElementUUID.equals(topic.getUUID())) return topic;
			for(WizardSlide slide : topic.getWizardSlides()){
				if(wizardElementUUID.equals(slide.getUUID())) return slide;
				for(WizardSlideItem item : slide.getWizardSlideItems()){
					if(wizardElementUUID.equals(item.getUUID())) return item;
				}
			}
		}
		return null;
	}
	
	public static boolean wizardElementHasValueChanged(WizardElement wizardElement){
		if(wizardElement instanceof WizardTopic)
			return wizardTopicHasValueChanged((WizardTopic) wizardElement);
		else if(wizardElement instanceof WizardSlide)
			return wizardSlideHasValueChanged((WizardSlide) wizardElement);
		else if(wizardElement instanceof WizardSlideItem)
			return wizardSlideItemHasValueChanged((WizardSlideItem) wizardElement);
		else
			return false;
	}
	
	public static boolean wizardTopicHasValueChanged(WizardTopic wizardTopic){
		boolean anySlideHasValueChanged = false;
		/*for(WizardSlide slide : wizardTopic.getWizardSlides()){
			anySlideHasValueChanged = anySlideHasValueChanged || wizardSlideHasValueChanged(slide);
		}*/
		return wizardTopic.isValueChanged() || anySlideHasValueChanged;
	}
	
	public static boolean wizardSlideHasValueChanged(WizardSlide wizardSlide){
		boolean anyItemHasValueChanged = false;
		for(WizardSlideItem item : wizardSlide.getWizardSlideItems()){
			anyItemHasValueChanged = anyItemHasValueChanged || wizardSlideItemHasValueChanged(item);
		}
		return wizardSlide.isValueChanged() || anyItemHasValueChanged;
	}
	
	public static boolean wizardSlideItemHasValueChanged(WizardSlideItem wizardSlideItem){
		return wizardSlideItem.isValueChanged();
	}
	
	public static WizardTopic newWizardTopic(){
		WizardTopic wizardTopic = WIZARD_FACTORY.newWizardTopic().as();
		wizardTopic.setUUID("new" + Math.random());
		wizardTopic.setTitle("Novo Tópico");
		wizardTopic.setOrder(0);
		wizardTopic.setBackgroundURL("");
		List<WizardSlide> wizardSlides = new ArrayList<>();
		WizardSlide wizardSlide = newWizardSlide();
		wizardSlides.add(wizardSlide);
		wizardTopic.setWizardSlides(wizardSlides);
		return wizardTopic;
	}
	
	public static WizardSlide newWizardSlide(){
		WizardSlide wizardSlide = WIZARD_FACTORY.newWizardSlide().as();
		wizardSlide.setUUID("new" + Math.random());
		wizardSlide.setTitle("Novo Slide");
		wizardSlide.setWizardSlideType(WizardSlideType.CONTENT);
		wizardSlide.setOrder(0);
		List<WizardSlideItem> wizardSlideItems = new ArrayList<>();
		wizardSlide.setWizardSlideItems(wizardSlideItems);
		return wizardSlide;
	}
	
	public static WizardSlide newWizardSlideQuiz(){
		WizardSlide wizardSlideQuiz = newWizardSlide();
		wizardSlideQuiz.setWizardSlideType(WizardSlideType.QUIZ);
		return wizardSlideQuiz;
	}
	
	public static WizardSlideItem newWizardSlideItem(){
		WizardSlideItem wizardSlideItem = WIZARD_FACTORY.newWizardSlideItem().as();
		wizardSlideItem.setUUID("new" + Math.random());
		wizardSlideItem.setTitle("");
		wizardSlideItem.setText("");
		wizardSlideItem.setOrder(0);
		wizardSlideItem.setValueChanged(true);
		return wizardSlideItem;
	}

	public static native String stripIdFromVideoURL(String url) /*-{
		return $wnd.stripIdFromVideoURL(url);
	}-*/;
	
	public static List<WizardElement> wizardToList(Wizard wizard){
		List<WizardElement> wizardElementList = new ArrayList<>();
		for(WizardTopic wizardTopic : wizard.getWizardTopics()){
			wizardElementList.add(wizardTopic);
			for(WizardSlide wizardSlide : wizardTopic.getWizardSlides()){
				wizardElementList.add(wizardSlide);
			}
		}
		return wizardElementList;
	}

	public static WizardElement getParentWizardElement(Wizard wizard, WizardElement wizardElement) {
		for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
			for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
				if(wizardSlide.getUUID().equals(wizardElement.getUUID())){
					return wizardTopic;
				}
			}
		}
		return null;
	}

	public static WizardElement getNextWizardElement(Wizard wizard, WizardElement currentViewedWizardElement) {
		if(currentViewedWizardElement == null) return null;
		
		List<WizardElement> wizardElementList = wizardToList(wizard);
		WizardElement wizardElement;
		for(int i = 0; i < wizardElementList.size(); i++){
			wizardElement = wizardElementList.get(i);
			if(currentViewedWizardElement.getUUID().equals(wizardElement.getUUID())){
				if(wizardElementList.size() > (i + 1)){
					return wizardElementList.get(++i);
				}
			}
		}
		return null;
	}

	public static WizardElement getPrevWizardElement(Wizard wizard, WizardElement currentViewedWizardElement) {
		if(currentViewedWizardElement == null) return null;
		
		List<WizardElement> wizardElementList = wizardToList(wizard);
		WizardElement wizardElement;
		for(int i = 0; i < wizardElementList.size(); i++){
			wizardElement = wizardElementList.get(i);
			if(currentViewedWizardElement.getUUID().equals(wizardElement.getUUID())){
				if(i > 0){
					return wizardElementList.get(--i);
				}
			}
		}
		return null;
	}
	
	public static String buildParentOrderFromParent(WizardElement wizardElement){
		String parentOrder = wizardElement.getParentOrder();
		Integer order = wizardElement.getOrder();
		if(parentOrder == null){
			return ""+(order+1);
		}else if(order ==  null){
			return parentOrder;
		}else{
			return parentOrder+"."+(order+1);
		}
	}
	
	public static String getItemNameByType(WizardSlideItemType wizardSlideItemType){
		switch (wizardSlideItemType) {
		case IMAGE:
			return "Imagem";
		case QUIZ:
			return "Quiz";
		case TEXT:
			return "Texto";
		case VIDEO_LINK:
			return "Video";
		default:
			return "";
		}
	}
	
	public static String getClasForWizardSlideItemViewIcon(WizardSlideItemType type){
		switch(type){
		case IMAGE:
			return "fa-picture-o";
		case QUIZ:
			return "fa-check-square-o";
		case TEXT:
			return "fa-text-height";
		case VIDEO_LINK:
			return "fa-youtube-play";
		default:
			return "";
		}
	}
	
	public static void createIcon(Button btn, String iconClass){
		Icon icon = new Icon();
		icon.addStyleName("fa " + iconClass);
		btn.clear();
		btn.add(icon);
	}
	
}
