package kornell.gui.client.util.entity;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface GUIEntityFactory extends AutoBeanFactory {

    AutoBean<TermsLanguageItems> newTermsLanguageItems();

    AutoBean<TermsLanguageItem> newTermsLanguageItem();

}
