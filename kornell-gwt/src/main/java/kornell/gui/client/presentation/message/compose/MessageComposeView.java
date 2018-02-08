package kornell.gui.client.presentation.message.compose;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.CourseClassTO;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public interface MessageComposeView extends IsWidget {
    public interface Presenter extends IsWidget {
        void okButtonClicked();

        void init(ArrayList<CourseClassTO> helpCourseClasses);

        void cancelButtonClicked();
    }

    void setPresenter(Presenter presenter);

    KornellFormFieldWrapper getMessageText();

    KornellFormFieldWrapper getRecipient();

    void show(ArrayList<CourseClassTO> helpCourseClasses, String courseClassUUID);

    boolean checkErrors();

    void clearErrors();

}
