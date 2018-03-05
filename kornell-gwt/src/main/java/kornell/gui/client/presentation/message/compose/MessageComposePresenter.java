package kornell.gui.client.presentation.message.compose;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;

public class MessageComposePresenter implements MessageComposeView.Presenter {
    private FormHelper formHelper = GWT.create(FormHelper.class);
    private KornellConstants constants = GWT.create(KornellConstants.class);

    private MessageComposeView view;
    private PlaceController placeCtrl;
    private ChatThreadsClient threadsClient;
    private ViewFactory viewFactory;
    private KornellSession session;

    public MessageComposePresenter(PlaceController placeCtrl, KornellSession session, ViewFactory viewFactory,
            EntityFactory entityFactory) {
        this.placeCtrl = placeCtrl;
        this.threadsClient = session.chatThreads();
        this.viewFactory = viewFactory;
        this.session = session;
    }

    @Override
    public void init(ArrayList<CourseClassTO> helpCourseClasses) {
        if (view == null) {
            view = viewFactory.getMessageComposeView();
            view.setPresenter(this);
        }

        // check if it's inside the classroom to preselect the recipient
        view.show(helpCourseClasses, getCourseClassUUIDFromPlace());
    }

    private String getCourseClassUUIDFromPlace() {
        if (placeCtrl.getWhere() instanceof ClassroomPlace) {
            return session.getCurrentCourseClass().getCourseClass().getUUID();
        }
        return null;
    }

    @Override
    public void okButtonClicked() {
        if (validateMessage()) {
            String messageText = view.getMessageText().getFieldPersistText();
            Callback<String> chatThreadCallback = new Callback<String>() {
                @Override
                public void ok(String str) {
                    KornellNotification.show(constants.messageSentSuccess());
                    MrPostman.hide();
                }
            };
            String threadSelectValue = view.getRecipient().getFieldPersistText();
            if ("platformSupport".equals(threadSelectValue)) {
                threadsClient.postMessageToSupportPlatformThread(messageText, chatThreadCallback);
            } else if ("institutionSupport".equals(threadSelectValue)) {
                threadsClient.postMessageToSupportInstitutionThread(messageText, chatThreadCallback);
            } else {
                threadsClient.postMessageToSupportCourseClassThread(messageText, threadSelectValue, chatThreadCallback);
            }
        }
    }

    @Override
    public void cancelButtonClicked() {
        MrPostman.hide();
    }

    private boolean validateMessage() {
        view.clearErrors();
        if (!formHelper.isLengthValid(view.getMessageText().getFieldPersistText(), 1, 1000)) {
            view.getMessageText().setError(constants.noMessageBodyError());
        }
        return !view.checkErrors();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}
