package kornell.gui.client.presentation.message.generic;

import com.google.gwt.user.client.ui.Label;

import kornell.core.to.ChatThreadMessageTO;

public class MessageItem {

    private Label label;
    private ChatThreadMessageTO chatThreadMessageTO;

    public MessageItem(Label label, ChatThreadMessageTO chatThreadMessageTO) {
        this.label = label;
        this.chatThreadMessageTO = chatThreadMessageTO;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public ChatThreadMessageTO getChatThreadMessageTO() {
        return chatThreadMessageTO;
    }

    public void setChatThreadMessageTO(ChatThreadMessageTO chatThreadMessageTO) {
        this.chatThreadMessageTO = chatThreadMessageTO;
    }

}
