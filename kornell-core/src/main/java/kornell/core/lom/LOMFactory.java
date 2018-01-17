package kornell.core.lom;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;


public interface LOMFactory extends AutoBeanFactory {
    public static String PREFIX = "application/vnd.kornell.v1.lom.";
    AutoBean<Topic> newTopic();

    AutoBean<ExternalPage> newExternalPage();

    AutoBean<Contents> newContents();

    AutoBean<Content> newContent();
}
