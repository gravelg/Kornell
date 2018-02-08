package kornell.gui.client.mvp;

import com.google.gwt.place.shared.Place;

public interface AsyncActivityMapper {

    void getActivity(Place place, ActivityCallbackHandler activityCallbackHandler);

}