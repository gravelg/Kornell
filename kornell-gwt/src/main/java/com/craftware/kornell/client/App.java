package com.craftware.kornell.client;

import com.craftware.kornell.client.activity.AppPlaceHistoryMapper;
import com.craftware.kornell.client.presenter.vitrine.VitrinePlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

public class App {
	private final EventBus eventBus;
	private final PlaceController placeController;
	private final ActivityManager activityManager;	
	private final AppPlaceHistoryMapper historyMapper;
	private final PlaceHistoryHandler historyHandler;

	public App(EventBus eventBus,
			PlaceController placeController,
		    ActivityManager activityManager, 			
			AppPlaceHistoryMapper historyMapper,
			PlaceHistoryHandler historyHandler) {
		this.eventBus = eventBus;
	    this.placeController = placeController;
	    this.activityManager = activityManager;
		this.historyMapper = historyMapper;
		this.historyHandler = historyHandler;
	}
	


	public void run(HasWidgets.ForIsWidget parentView) {
		SimpleLayoutPanel shell = new SimpleLayoutPanel();
		parentView.add(shell);
	    activityManager.setDisplay(shell);
		initBrowserHistory(historyMapper, historyHandler, new VitrinePlace());

	}

	private void initBrowserHistory(
			final AppPlaceHistoryMapper historyMapper,
			PlaceHistoryHandler historyHandler,
			Place defaultPlace) {

		historyHandler.register(placeController, eventBus, defaultPlace);
		
		historyHandler.handleCurrentHistory();
	}

}