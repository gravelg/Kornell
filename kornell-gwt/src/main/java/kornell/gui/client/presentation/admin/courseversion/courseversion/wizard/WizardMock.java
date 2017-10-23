package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemImage;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemVideoLink;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;

public class WizardMock {

	public static final WizardFactory WIZARD_FACTORY = GWT.create(WizardFactory.class);
	
	public static Wizard mockWizard(){	
		return mockWizard(false);
	}
	
	public static Wizard mockWizard(boolean mockData){				
		Wizard wizard = WIZARD_FACTORY.newWizard().as();

		List<WizardTopic> wizardTopics = new ArrayList<>();
		if(mockData){
			wizardTopics.add(mockWizardTopic(0));
			wizardTopics.add(mockWizardTopic(1));
			wizardTopics.add(mockWizardTopic(2));
		} else {
			wizardTopics.add(mockWizardTopic(0, false));
		}
		
		wizard.setWizardTopics(wizardTopics);
		
		return wizard;
	}

	private static WizardTopic mockWizardTopic(int order) {
		return mockWizardTopic(order, false);
	}

	private static WizardTopic mockWizardTopic(int order, boolean mockData) {
		WizardTopic wizardTopic = WIZARD_FACTORY.newWizardTopic().as();

		wizardTopic.setUUID(order + "");
		wizardTopic.setOrder(order);		
		wizardTopic.setTitle("I am topic " + (order+1));
		wizardTopic.setBackgroundURL("https://picsum.photos/g/5615/2907?image=974");

		List<WizardSlide> wizardSlides = new ArrayList<>();
		if(mockData){
			wizardSlides.add(mockWizardSlide(wizardTopic, 0));
			wizardSlides.add(mockWizardSlide(wizardTopic, 1));
			wizardSlides.add(mockWizardSlide(wizardTopic, 2));
			/*wizardSlides.add(mockWizardSlide(wizardTopic, 3));
			wizardSlides.add(mockWizardSlide(wizardTopic, 4));*/
		} else {
			wizardSlides.add(mockWizardSlide(wizardTopic, 0, false));
		}
		
		wizardTopic.setWizardSlides(wizardSlides);
		
		return wizardTopic;
	}
	
	private static WizardSlide mockWizardSlide(WizardTopic wizardTopic, int order) {
		return mockWizardSlide(wizardTopic, order, false);
	}

	private static WizardSlide mockWizardSlide(WizardTopic wizardTopic, int order, boolean mockData) {
		WizardSlide wizardSlide = WIZARD_FACTORY.newWizardSlide().as();
		
		wizardSlide.setUUID(wizardTopic.getUUID() + "." + order);		
		wizardSlide.setOrder(order);		
		wizardSlide.setParentOrder(WizardUtils.buildParentOrderFromParent(wizardTopic));
		wizardSlide.setTitle("I am slide " + wizardSlide.getParentOrder() + "." + (order+1));
		wizardSlide.setBackgroundURL("https://picsum.photos/g/5615/2907?image=974");
		wizardSlide.setWizardSlideType(WizardSlideType.CONTENT);
		
		List<WizardSlideItem> wizardSlideItems = new ArrayList<>();
		if(mockData){
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 0));
			wizardSlideItems.add(mockWizardSlideItemVideoLink(wizardSlide, 1));
			wizardSlideItems.add(mockWizardSlideItemImage(wizardSlide, 2));
			/*wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 3));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 4));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 5));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 6));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 7));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 8));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 9));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 10));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 11));
			wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 12));*/
		} else {
			wizardSlide.setWizardSlideType(WizardSlideType.QUIZ);
			wizardSlideItems.add(mockWizardSlideItemQuiz(wizardSlide, 0));
		}
		
		wizardSlide.setWizardSlideItems(wizardSlideItems);
		
		return wizardSlide;
	}

	private static WizardSlideItem mockWizardSlideItemGeneric(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = WIZARD_FACTORY.newWizardSlideItem().as();		
		wizardSlideItem.setUUID(wizardSlide.getUUID() + "." + order);		
		wizardSlideItem.setParentOrder(WizardUtils.buildParentOrderFromParent(wizardSlide));
		wizardSlideItem.setOrder(order);
		wizardSlideItem.setTitle("I am item " + wizardSlideItem.getParentOrder() + "." + (order+1));
		return wizardSlideItem;
	}

	private static WizardSlideItem mockWizardSlideItemText(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = mockWizardSlideItemGeneric(wizardSlide, order);
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.TEXT);
		wizardSlideItem.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In venenatis enim mauris, euismod tincidunt odio efficitur sit amet. Nam gravida finibus convallis. Proin vitae feugiat nisi, et lobortis nulla. In commodo congue ligula, quis pharetra turpis condimentum eu. Donec dignissim justo vel venenatis venenatis. Phasellus eu felis erat. In vulputate dignissim nisl at lobortis. Aenean congue tempor urna at scelerisque. Suspendisse porttitor fringilla elit, id suscipit velit viverra et. Duis pellentesque ipsum vitae urna scelerisque iaculis. Praesent volutpat est posuere erat pharetra, vel gravida libero eleifend. Nulla fringilla vitae urna at dapibus. Nunc suscipit cursus ligula eu pellentesque. Nunc eu eleifend mi. Phasellus consequat auctor eros, in tristique lectus ornare ac. Duis et vulputate tortor.\n Mauris vel ultricies leo. Mauris aliquet volutpat lectus, venenatis sollicitudin sem accumsan nec. Nullam a elit porta, euismod turpis nec, tincidunt nulla. In vel pharetra lorem, eu cursus urna. Duis nulla eros, blandit in dolor et, ultricies fermentum nibh. Aliquam bibendum tincidunt eros in sodales. Aliquam a convallis nibh. Nullam venenatis non ligula a viverra. Mauris eget sollicitudin nibh. Integer vitae ullamcorper sem. \n Vivamus ultricies odio non vehicula varius. Curabitur massa purus, elementum cursus scelerisque in, malesuada vel sem. Sed euismod convallis sem a congue. Mauris ut lacus sed urna laoreet sollicitudin eleifend scelerisque erat. Pellentesque semper nunc eu quam auctor tristique. Suspendisse fringilla justo orci, quis dictum orci sollicitudin eu. Integer nisl justo, hendrerit quis neque sit amet, suscipit dignissim diam. Nullam eget quam congue, pulvinar nibh vel, semper ipsum. Pellentesque id elit eget ipsum viverra lobortis. Etiam nisi purus, finibus consequat arcu ut, sollicitudin ultricies massa. Quisque ac scelerisque mauris. Aliquam porta convallis eros, et sodales ante gravida a. Maecenas tempus mauris velit, iaculis viverra sem maximus id.");
		return wizardSlideItem;
	}

	private static WizardSlideItem mockWizardSlideItemVideoLink(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = mockWizardSlideItemGeneric(wizardSlide, order);
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.VIDEO_LINK);

		WizardSlideItemVideoLink wizardSlideItemVideoLink = WIZARD_FACTORY.newWizardSlideItemVideoLink().as();
		wizardSlideItemVideoLink.setVideoLinkType("YOUTUBE");
		wizardSlideItemVideoLink.setURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		wizardSlideItem.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideItemVideoLink)).getPayload().toString());

		return wizardSlideItem;
	}

	private static WizardSlideItem mockWizardSlideItemImage(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = mockWizardSlideItemGeneric(wizardSlide, order);
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.IMAGE);

		WizardSlideItemImage wizardSlideItemImage = WIZARD_FACTORY.newWizardSlideItemImage().as();
		wizardSlideItemImage.setURL("https://i.vimeocdn.com/portrait/58832_300x300");
		wizardSlideItem.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideItemImage)).getPayload().toString());

		return wizardSlideItem;
	}

	private static WizardSlideItem mockWizardSlideItemQuiz(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = mockWizardSlideItemGeneric(wizardSlide, order);
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.QUIZ);

		WizardSlideItemImage wizardSlideItemImage = WIZARD_FACTORY.newWizardSlideItemImage().as();
		wizardSlideItemImage.setURL("https://i.vimeocdn.com/portrait/58832_300x300");
		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideItemImage)).getPayload().toString());
		wizardSlideItem.setText("");
		wizardSlideItem.setTitle("");

		return wizardSlideItem;
	}

}
