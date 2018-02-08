package kornell.gui.client.presentation.admin.institution;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.Institution;

public interface AdminInstitutionView extends IsWidget {
    public interface Presenter extends IsWidget {
        void updateInstitution(Institution institution);
    }

    void setPresenter(Presenter presenter);
}