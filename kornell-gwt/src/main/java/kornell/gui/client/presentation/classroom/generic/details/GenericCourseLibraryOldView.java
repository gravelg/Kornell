package kornell.gui.client.presentation.classroom.generic.details;

import static kornell.core.util.StringUtils.mkurl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.to.LibraryFileTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.util.ClientConstants;


public class GenericCourseLibraryOldView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, GenericCourseLibraryOldView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private static String COURSE_LIBRARY_IMAGES_PATH = mkurl(ClientConstants.IMAGES_PATH, "courseLibrary");

	@UiField
	FlowPanel libraryPanel;
	@UiField
	FlowPanel titlePanel;
	@UiField
	FlowPanel contentPanel; 
	

	com.github.gwtbootstrap.client.ui.Button btnFile;

	FlowPanel filesPanel; 
	FlowPanel filesHeader;
	FlowPanel filesWrapper; 
	
	LibraryFilesTO libraryFilesTO;
	Button btnIcon;
	Button btnName; 
	Button btnSize;
	Button btnPublishingDate;
	Button btnLastClicked;
	private static Integer ORDER_ASCENDING = 0;
	private static Integer ORDER_DESCENDING = 1;
	private Integer order = ORDER_ASCENDING;	
	Map<String, FlowPanel> fileWidgetMap;
	
	public GenericCourseLibraryOldView(EventBus eventBus, KornellSession session, PlaceController placeCtrl, LibraryFilesTO libraryFilesTO) {
		this.libraryFilesTO = libraryFilesTO;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		/*client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display();
			}
		});*/	
		display();
	}

	private void display() {
		displayTitle();
		fileWidgetMap = new HashMap<String, FlowPanel>();
		contentPanel.add(getFilesTable(btnLastClicked));	
		handleEvent(btnLastClicked);			
	}


	private void displayTitle() {		
		FlowPanel certificationInfo = new FlowPanel();
		certificationInfo.addStyleName("detailsInfo");

		Label infoTitle = new Label(constants.libraryTitle());
		infoTitle.addStyleName("detailsInfoTitle");
		certificationInfo.add(infoTitle);

		Label infoText = new Label(constants.libraryInfo());
		infoText.addStyleName("detailsInfoText");
		certificationInfo.add(infoText);

		titlePanel.add(certificationInfo);
	}
	
	private FlowPanel getFilesTable(Button btn) {
		FlowPanel filesPanel = new FlowPanel();
		filesPanel.addStyleName("filesPanel");
		filesPanel.add(getHeader());
		filesPanel.add(getFiles(btn));
		return filesPanel;
	}

	private FlowPanel getFiles(Button btn) {
		List<LibraryFileTO> list = libraryFilesTO.getLibraryFiles();

		if (list.size() > 0) {
			if(btnIcon.equals(btn)){
			    Collections.sort(list, new FileTypeComparator());
		    	if(btnIcon.equals(btnLastClicked) && ORDER_ASCENDING.equals(order)){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			} else if(btnSize.equals(btn)) {
			    Collections.sort(list, new FileSizeComparator());
		    	if(btnSize.equals(btnLastClicked) && ORDER_DESCENDING.equals(order)){
		    		Collections.reverse(list);
		    		order = ORDER_ASCENDING;
		    	} else {
		    		order = ORDER_DESCENDING;
		    	}
			} else if(btnPublishingDate.equals(btn)) {
			    Collections.sort(list, new FilePublishingDateComparator());
		    	if(btnPublishingDate.equals(btnLastClicked) && ORDER_ASCENDING.equals(order)){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			} else {
			    Collections.sort(list, new FileNameComparator());
		    	if(btnName.equals(btnLastClicked) && ORDER_ASCENDING.equals(order)){
		    		Collections.reverse(list);
		    		order = ORDER_DESCENDING;
		    	} else {
		    		order = ORDER_ASCENDING;
		    	}
			}
			btnLastClicked = btn != null ? btn : btnLastClicked;
		}
		
		if(fileWidgetMap.size() <= 0)
			for (LibraryFileTO fileTO : list)
				fileWidgetMap.put(fileTO.getFileName(), getFilePanel(fileTO));


		filesWrapper = new FlowPanel();
		filesWrapper.addStyleName("filesWrapper");
		for(LibraryFileTO fileTO : list){
			filesWrapper.add(fileWidgetMap.get(fileTO.getFileName()));
		}
		return filesWrapper;
	}

	private FlowPanel getHeader() {
		if(filesHeader != null)
			return filesHeader;
		
		filesHeader = new FlowPanel();
		filesHeader.addStyleName("filesHeader");
		
		btnIcon = btnIcon != null ? btnIcon : new Button(constants.libraryEntryIcon());
		displayHeaderButton(btnIcon, "btnIcon", false);
		filesHeader.add(btnIcon);

		btnName = btnName != null ? btnName : new Button(constants.libraryEntryName());
		displayHeaderButton(btnName, "btnName", true);
		filesHeader.add(btnName);

		btnSize = btnSize != null ? btnSize : new Button(constants.libraryEntrySize());
		displayHeaderButton(btnSize, "btnSize", false);
		filesHeader.add(btnSize);

		btnPublishingDate = btnPublishingDate != null ? btnPublishingDate : new Button(constants.libraryEntryDate());
		displayHeaderButton(btnPublishingDate, "btnPublishingDate", false);
		filesHeader.add(btnPublishingDate);
		
		btnLastClicked = btnLastClicked != null ? btnLastClicked : btnName;
		
		return filesHeader;
	}

	private void displayHeaderButton(Button btn, String styleName, boolean selected) {
		btn.removeStyleName("btn");
		btn.addStyleName("btnLibraryHeader"); 
		btn.addStyleName(styleName);
		btn.addStyleName(selected ? "btnAction" : "btnNotSelected");
		btn.addClickHandler(new LibraryHeaderClickHandler());
	}

	private void handleEvent(Button btn) {		
		if(btnLastClicked != null){
			btnLastClicked.removeStyleName("btnAction");
			btnLastClicked.addStyleName("btnNotSelected");
		}
		contentPanel.clear();
		contentPanel.add(getFilesTable(btn));
		btn.addStyleName("btnAction");
		btn.removeStyleName("btnNotSelected");
		btnLastClicked = btn;
	}

	private FlowPanel getFilePanel(final LibraryFileTO fileTO) {
		FlowPanel fileWrapper = new FlowPanel();
		fileWrapper.addStyleName("fileWrapper");
		
		Image fileIcon = new Image(getIconImageByFileType(fileTO.getFileType()));
		fileIcon.addStyleName("fileIconOld");
		fileIcon.addStyleName("cursorPointer");
		fileWrapper.add(fileIcon);
		
		fileIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(fileTO.getURL(),"_blank","");		
			}
		});
		
		FlowPanel pnlFileName = new FlowPanel();
		pnlFileName.addStyleName("pnlFileName");
		
		Label fileName = new Label(fileTO.getFileName());
		fileName.addStyleName("fileName");
		fileName.addStyleName("cursorPointer");
		pnlFileName.add(fileName);
		
		fileName.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(fileTO.getURL(),"_blank","");		
			}
		});

		Label fileDescription = new Label(fileTO.getFileDescription());
		fileDescription.addStyleName("fileDescription");
		pnlFileName.add(fileDescription);
		
		fileWrapper.add(pnlFileName);
		
		Label fileSize = new Label(fileTO.getFileSize());
		fileSize.addStyleName("fileSize");
		fileWrapper.add(fileSize);

		Label publishingDate = new Label(fileTO.getPublishingDate());
		publishingDate.addStyleName("publishingDate");
		fileWrapper.add(publishingDate);
		
		
		return fileWrapper;
	}

	private String getIconImageByFileType(String fileType) {
		String imgFileTypeName;
		if("pdf".equals(fileType)){
			imgFileTypeName = "pdf";
		} else if("xlsx".equals(fileType) || "xls".equals(fileType)){
			imgFileTypeName = "xls";
		} else if("docx".equals(fileType) || "doc".equals(fileType)){
			imgFileTypeName = "doc";
		} else {
			imgFileTypeName = "unknownFileType";
		}
		return StringUtils.mkurl(COURSE_LIBRARY_IMAGES_PATH, imgFileTypeName + ".png");
	}

	private final class LibraryHeaderClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((Button) event.getSource());
		}
	}

	private final class FileNameComparator implements Comparator<LibraryFileTO> {
		@Override
		public int compare(final LibraryFileTO object1, final LibraryFileTO object2) {
			if(object1.getFileName().compareTo(object2.getFileName()) == 0)
			    return object1.getFileDescription().compareTo(object2.getFileDescription());
			return object1.getFileName().compareTo(object2.getFileName());
		}
	}

	private final class FilePublishingDateComparator implements Comparator<LibraryFileTO> {
		@Override
		public int compare(final LibraryFileTO object1, final LibraryFileTO object2) {
			if(object1.getPublishingDate().compareTo(object2.getPublishingDate()) == 0)
				return object1.getFileName().compareTo(object2.getFileName());
		    return object1.getPublishingDate().compareTo(object2.getPublishingDate());
		}
	}

	private final class FileSizeComparator implements Comparator<LibraryFileTO> {
		@Override
		public int compare(final LibraryFileTO object1, final LibraryFileTO object2) {
			try {
				int ret = 0;
				String[] parts1 = object1.getFileSize().split(" ");
				String[] parts2 = object2.getFileSize().split(" ");
				Integer value1 = Integer.parseInt(parts1[0]);
				String unit1 = parts1[1].toUpperCase();
				Integer value2 = Integer.parseInt(parts2[0]);
				String unit2 = parts2[1].toUpperCase();
				ret = unit1.compareTo(unit2);
				if(ret == 0)
					ret = value1.compareTo(value2);
				if(ret == 0)
					return object1.getFileName().compareTo(object2.getFileName());
				return ret;
			} catch (Exception e) {
		      return object2.getFileSize().compareTo(object1.getFileSize());
			}
		}
	}

	private final class FileTypeComparator implements Comparator<LibraryFileTO> {
		@Override
		public int compare(final LibraryFileTO object1, final LibraryFileTO object2) {
			if(object1.getFileType().compareTo(object2.getFileType()) == 0)
				return object1.getFileName().compareTo(object2.getFileName());
		    return object1.getFileType().compareTo(object2.getFileType());
		}
	}

}