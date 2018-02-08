package kornell.gui.client.presentation.admin.audit;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.AdminPlace;

public class AdminAuditPlace extends AdminPlace {
    public static final AdminAuditPlace instance = new AdminAuditPlace();

    public AdminAuditPlace() {
    }

    @Prefix("a.audit")
    public static class Tokenizer implements PlaceTokenizer<AdminAuditPlace> {
        public AdminAuditPlace getPlace(String token) {
            return new AdminAuditPlace();
        }

        public String getToken(AdminAuditPlace place) {
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
