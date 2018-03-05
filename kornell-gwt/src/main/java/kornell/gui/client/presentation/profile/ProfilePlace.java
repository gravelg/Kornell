package kornell.gui.client.presentation.profile;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProfilePlace extends Place {
    String personUUID;
    boolean edit;

    public ProfilePlace(String personUUID, boolean edit) {
        this.personUUID = personUUID;
        this.edit = edit;
    }

    public String getPersonUUID() {
        return personUUID;
    }

    public void setPersonUUID(String personUUID) {
        this.personUUID = personUUID;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    @Prefix("profile")
    public static class Tokenizer implements PlaceTokenizer<ProfilePlace> {
        public ProfilePlace getPlace(String tokIn) {
            String[] toks = tokIn.split(";");
            String personUUID = toks.length > 0 ? toks[0] : "";
            boolean edit = toks.length > 1 && "true".equals(toks[1]) ? true : false;
            return new ProfilePlace(personUUID, edit);
        }

        public String getToken(ProfilePlace place) {
            return place.getPersonUUID() + ";" + place.isEdit();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
