package kornell.gui.client.presentation.welcome;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class WelcomePlace extends Place {
    @Prefix("welcome")
    public static class Tokenizer implements PlaceTokenizer<WelcomePlace> {

        public WelcomePlace getPlace(String token) {
            return new WelcomePlace();
        }

        public String getToken(WelcomePlace place) {
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
