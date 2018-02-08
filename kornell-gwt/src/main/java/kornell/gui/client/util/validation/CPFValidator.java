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

public class CPFValidator implements Validator {
    KornellConstants constants = GWT.create(KornellConstants.class);
    KornellSession session;
    String personUUID;

    public CPFValidator(String personUUID, KornellSession session) {
        this.session = session;
        this.personUUID = personUUID;
    }

    public static final CPFValidator unregisteredCPFValidator(String personUUID, KornellSession session) {
        CPFValidator unregisteredCPFVal = new CPFValidator(personUUID, session);
        return unregisteredCPFVal;
    }

    @Override
    public void getErrors(Widget widget, final Callback<List<String>> cb) {
        final List<String> errors = new ArrayList<String>();
        if (widget instanceof HasText) {
            HasText txtWidget = (HasText) widget;
            String cpf = txtWidget.getText();
            if (StringUtils.isSome(cpf)) {
                if (!isValidCPF(cpf)) {
                    errors.add(constants.invalidCPF());
                    cb.ok(errors);
                } else {
                    session.person(personUUID).isCPFRegistered(cpf, new Callback<Boolean>() {
                        @Override
                        public void ok(Boolean to) {
                            if (to) {
                                errors.add(constants.existingCPF());
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

    private static boolean isValidCPF(String cpf) {
        return FormHelper.isCPFValid(cpf);
    }

}
