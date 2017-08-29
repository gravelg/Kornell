package kornell.core.entity;

public interface EmailTemplate extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "certificateDetails+json";

	EmailTemplateType getTemplateType();
	void setTemplateType(EmailTemplateType templateType);

	String getLocale();
	void setLocale(String locale);

	String getTitle();
	void setTitle(String title);

	String getTemplate();
	void setTemplate(String template);
}
