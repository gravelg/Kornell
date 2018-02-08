package kornell.gui.client;

import com.google.gwt.i18n.client.Messages;

public interface KornellMessages extends Messages {

    @DefaultMessage("{0} hours ago.")
    @AlternateMessage({ "true", "{0} hours ago.", "false", "{0} hour ago." })
    String hoursAgo(long hours, @Select boolean plural);

    @DefaultMessage("{0} minutes ago.")
    @AlternateMessage({ "true", "{0} minutes ago.", "false", "{0} minute ago." })
    String minutesAgo(long minutes, @Select boolean plural);

    @DefaultMessage("{0} seconds ago.")
    @AlternateMessage({ "true", "{0} seconds ago.", "false", "{0} second ago." })
    String secondsAgo(long seconds, @Select boolean plural);
}
