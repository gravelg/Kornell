package kornell.gui.client.presentation.sandbox;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SandboxPlace extends Place {
    @Prefix("sandbox")
    public static class Tokenizer implements PlaceTokenizer<SandboxPlace> {

        public SandboxPlace getPlace(String token) {
            return new SandboxPlace();
        }

        public String getToken(SandboxPlace place) {
            return "";
        }
    }
}
