package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit.WizardView;

public interface AdminCourseVersionContentView extends IsWidget {
	public interface Presenter extends IsWidget {
		void init(CourseVersion courseVersion, ContentSpec contentSpec);
		void wizardElementClicked(WizardElement wizardElement);
		void valueChanged(boolean valueHasChanged);
		void valueChanged(WizardElement wizardElement, boolean valueHasChanged);
		WizardElement getSelectedWizardElement();
		Wizard getWizard();
		AdminCourseVersionContentView getView();
		void deleteSlide();
		void moveSlideDown();
		void moveSlideUp();
	}
	void setPresenter(Presenter presenter);
	void init(CourseVersion courseVersion, Wizard wizard);
	WizardView getWizardView();
}