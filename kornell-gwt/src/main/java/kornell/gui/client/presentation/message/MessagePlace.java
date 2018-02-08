package kornell.gui.client.presentation.message;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MessagePlace extends Place {

    public MessagePlace() {
    }

    @Prefix("message")
    public static class Tokenizer implements PlaceTokenizer<MessagePlace> {
        public MessagePlace getPlace(String tokIn) {
            return new MessagePlace();
        }

        public String getToken(MessagePlace place) {
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
