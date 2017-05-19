package kornell.gui.client.util;

import com.google.gwt.core.client.GWT;

import kornell.gui.client.KornellConstants;

public class EnumTranslator {

	private static KornellConstants constants = GWT.create(KornellConstants.class);

	public static String translateEnum(Enum<?> e) {
		return constants.getString(e.getClass().getSimpleName() + '_' + e.name());
	}
}
