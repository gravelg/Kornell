package kornell.gui.client.presentation.terms;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TermsPlace extends Place {
    @Prefix("terms")
    public static class Tokenizer implements PlaceTokenizer<TermsPlace> {

        public TermsPlace getPlace(String token) {
            return new TermsPlace();
        }

        public String getToken(TermsPlace place) {
            return "";
        }
    }
}
