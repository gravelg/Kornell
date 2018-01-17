package kornell.core.lom;


public interface Topic extends Node {
    public static String TYPE = LOMFactory.PREFIX + "topic+json";

    String getName();
    void setName(String name);

}
