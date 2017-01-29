package kornell.scorm.client.scorm12;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.ActomEntries;
import kornell.core.entity.EnrollmentEntries;
import kornell.core.entity.EnrollmentsEntries;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.event.NavigationAuthorizationEvent;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;
import kornell.gui.client.sequence.NavigationRequest;

public class SCORM12Runtime implements ActomEnteredEventHandler, ProgressEventHandler {
	private static final Logger logger = Logger.getLogger(SCORM12Runtime.class.getName());
	private static SCORM12Runtime instance;
	private EnrollmentsEntries entries;

	private SCORM12Adapter currentAPI = null;
	private KornellSession session;
	private PlaceController placeCtrl;

	private Map<String,CMITree> forestCache = new HashMap<>();
	private EventBus bus;
	
	private boolean disableNextButton;
	private boolean disablePrevButton;
	private Timer disableButtonsTimer;
	
	private SCORM12Runtime(EventBus bus, KornellSession session, PlaceController placeCtrl, EnrollmentsEntries entries){
		this.entries = entries;
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.bus = bus;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
		bus.addHandler(ProgressEvent.TYPE, this);
		initializeDisableButtonsTimer();
	}

	public static synchronized SCORM12Runtime launch(EventBus bus, KornellSession session, PlaceController placeCtrl,EnrollmentsEntries entries){		
		instance = new SCORM12Runtime(bus, session, placeCtrl, entries);
		return instance;
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		logger.info("Loading [enrollmentUUID:"+event.getEnrollmentUUID()+"][actomKey:"+event.getActomKey()+"]");
		if (currentAPI != null) currentAPI.onActomEntered();
		bindNewAdapter(event.getEnrollmentUUID(),event.getActomKey());
	}

	private void bindNewAdapter(String enrollmentUUID, String actomKey) {
		ActomEntries actomEntries = lookupActomEntries(enrollmentUUID, actomKey);
		SCORM12Adapter apiAdapter = SCORM12Adapter.create(this,session,placeCtrl,enrollmentUUID,actomKey,actomEntries);
		SCORM12Binder.bindToWindow(apiAdapter);
	}

	private ActomEntries lookupActomEntries(String enrollmentUUID, String actomKey) {
		Map<String, EnrollmentEntries> enrollmentEntriesMap = entries.getEnrollmentEntriesMap();
		EnrollmentEntries enrollmentEntries = enrollmentEntriesMap.get(enrollmentUUID);
		if(enrollmentEntries != null){
			Map<String, ActomEntries> actomEntriesMap = enrollmentEntries.getActomEntriesMap();
			ActomEntries actomEntries = actomEntriesMap.get(actomKey);
			return actomEntries;
		} else {
			logger.warning("Enrollment entries not found for ["+enrollmentUUID+"]["+actomKey+"]");
			logger.warning("Current enrollments: ");
			Set<String> enrollments = entries.getEnrollmentEntriesMap().keySet();
			for (String enroll : enrollments) {
				logger.warning("- "+enroll);
			}
			return null;
		}
	}

	public CMITree getDataModel(String targetUUID,String actomKey) {
		//trim query params when building cache key
		int index = actomKey.indexOf("?");
		if (index != -1) {
			actomKey= actomKey.substring(0 , index); 
		}
		String cacheKey = StringUtils.hash(targetUUID,actomKey);
		CMITree dataModel = forestCache.get(cacheKey);
		if (dataModel == null){
			ActomEntries ae = lookupActomEntries(targetUUID,actomKey);
			if(ae != null){
				dataModel = CMITree.create(ae.getEntries());				
			} else {
				logger.warning("DataModel not found for ["+targetUUID+"]["+actomKey+"]");
				dataModel = CMITree.create(new HashMap<String,String>());
			}
		}
		forestCache.put(cacheKey, dataModel);
		return dataModel;
	}

	public void onLMSSetValue(String key, String value) {
		if ("knl.next".equals(key)){
			bus.fireEvent(NavigationRequest.next());
		} else if ("knl.prev".equals(key)){
			bus.fireEvent(NavigationRequest.prev());
		} else if (key != null && key.endsWith(".nextEnabled")){
			boolean isOk = "true".equals(value);
			disableNextButton = !isOk;
			bus.fireEvent(NavigationAuthorizationEvent.next(isOk));
		} else if (key != null && key.endsWith(".prevEnabled")){
			boolean isOk = "true".equals(value);
			disablePrevButton = !isOk;
			bus.fireEvent(NavigationAuthorizationEvent.prev(isOk));
		}
	}
	
	private void initializeDisableButtonsTimer(){
		bus.fireEvent(NavigationAuthorizationEvent.next(false));
		bus.fireEvent(NavigationAuthorizationEvent.prev(false));
		disableButtonsTimer = new Timer() {
			public void run() {
				bus.fireEvent(NavigationAuthorizationEvent.next(!disableNextButton));
				bus.fireEvent(NavigationAuthorizationEvent.prev(!disablePrevButton));
			}
		};
		// Schedule the timer to run after 3s
		disableButtonsTimer.schedule(3000);
	}

	@Override
	public void onProgress(ProgressEvent event) {
		disableNextButton = false;
		disablePrevButton = false;
		initializeDisableButtonsTimer();
	}
}