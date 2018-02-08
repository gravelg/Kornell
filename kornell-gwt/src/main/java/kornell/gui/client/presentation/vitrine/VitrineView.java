package kornell.gui.client.presentation.vitrine;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.IsWidget;

public interface VitrineView extends IsWidget {
    public interface Presenter extends IsWidget {
        void onLoginButtonClicked();

        void onRegisterButtonClicked();

        void onSignUpButtonClicked();

        void onCancelSignUpButtonClicked();

        void onForgotPasswordButtonClicked();

        void onRequestPasswordChangeButtonClicked();

        void onCancelPasswordChangeRequestButtonClicked();

        void onChangePasswordButtonClicked();

        void onCancelChangePasswordButtonClicked();
    }

    void setPresenter(Presenter presenter);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);

    String getSuEmail();

    String getSuName();

    String getSuPassword();

    String getSuPasswordConfirm();

    String getFpEmail();

    String getNewPassword();

    String getNewPasswordConfirm();

    void hideMessage();

    void showMessage();

    void setMessage(String msg);

    void setMessage(String msg, AlertType alertType);

    void setMessage(List<String> msgs);

    void setLogoURL(String assetsURL);

    void displayView(VitrineViewType type);

    void showRegistrationOption(boolean show);

    void setRegistrationEmail(String email);

    boolean isForcedPasswordUpdate();

    void setForcedPasswordUpdate(boolean forcedPasswordUpdate);
}