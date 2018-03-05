package kornell.gui.client.util.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.util.forms.FormHelper;

public class EmailValidator implements Validator {
    KornellConstants constants = GWT.create(KornellConstants.class);
    KornellSession session;
    String personUUID;

    public EmailValidator(String personUUID, KornellSession session) {
        this.session = session;
        this.personUUID = personUUID;
    }

    public static final EmailValidator unregisteredEmailValidator(String personUUID, KornellSession session) {
        EmailValidator validator = new EmailValidator(personUUID, session);
        return validator;
    }

    @Override
    public void getErrors(Widget widget, final Callback<List<String>> cb) {
        final List<String> errors = new ArrayList<String>();
        if (widget instanceof HasText) {
            HasText txtWidget = (HasText) widget;
            String email = txtWidget.getText();
            if (StringUtils.isSome(email)) {
                if (!isValidEmail(email)) {
                    errors.add(constants.invalidEmail());
                    cb.ok(errors);
                } else {
                    session.person(personUUID).isEmailRegistered(email, new Callback<Boolean>() {
                        @Override
                        public void ok(Boolean exists) {
                            if (exists) {
                                errors.add(constants.existingEmail());
                            }
                            cb.ok(errors);
                        }
                    });
                }
            } else {
                cb.ok(errors);
            }
        }
    }

    private static boolean isValidEmail(String email) {
        boolean emailValid = FormHelper.isEmailValid(email);
        return emailValid;
    }

}
