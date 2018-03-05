package kornell.scorm.client.scorm12;

public class CMIDataModel extends CMICoreModel {
    /*
     * boolean dirty; private SCORM12Adapter api; private EventBus bus;
     * 
     * public CMIDataModel(SCORM12Adapter api, EventBus bus, Map<String, String>
     * entries) { this.api = api; this.entries = entries; this.bus = bus; }
     * 
     * public String getValue(String elementKey) { if (elementKey == null) {
     * logger.severe("!!! Null Element !!!"); throw new NullPointerException(
     * "Data model element can not be null."); } CMIDataElement element =
     * elements.get(elementKey); if (element != null) return
     * getElementValue(element); // TODO: Set Appropriate Error throw new
     * IllegalArgumentException("Unkown CMI Data Model Element [" + elementKey +
     * "]"); }
     * 
     * private String getElementValue(CMIDataElement element) { String key =
     * element.getKey(); String value = EMPTY; if (entries.containsKey(key))
     * value = entries.get(key); boolean isEmpty = isEmpty(value); String
     * defaultValue = element.getDefaultValue(); boolean hasDefault =
     * !isEmpty(defaultValue); if (isEmpty && hasDefault) return defaultValue;
     * return value; }
     * 
     * public String setValue(String elementKey, String value) { CMIDataElement
     * element = elements.get(elementKey); if (element == null) {
     * Set<Entry<String, CMIDataElement>> entries = elements.entrySet(); for
     * (Entry<String, CMIDataElement> entry : entries) { String key =
     * entry.getKey(); RegExp regExp = RegExp.compile(key); MatchResult matcher
     * = regExp.exec(elementKey); boolean matchFound = (matcher != null);
     * //logger.info("Matching (" + elementKey + ") against (" + key+ ") = " +
     * matchFound); if (matchFound) { element = entry.getValue(); } } } String
     * result = FALSE; if (element != null) { setElementValue(element,
     * elementKey, value); result = TRUE; } return result; }
     * 
     * private void setElementValue(final CMIDataElement element, final String
     * elementKey, final String value) { entries.put(elementKey, value);
     * 
     * api.onDirtyData(new Callback<ActomEntries>() {
     * 
     * @Override public void ok(ActomEntries to) { if (onSet != null) {
     * logger.info("Executing onSet action for [" + element.getKey() + "]");
     * onSet.execute(bus); } } }); }
     * 
     * private boolean isEmpty(String value) { return value == null ||
     * EMPTY.equals(value); }
     * 
     * public Map<String, String> getValues() { return entries; }
     */
    // TODO: Remove dead code
}
