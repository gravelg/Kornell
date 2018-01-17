package kornell.core.scorm12.lom;

public interface LearningResource {
    enum Type {
        SCO,
        Asset
    }

    public Type getType();
    public void setType(Type t);

    public Asset getAsset();
    public void setAsset(Asset asset);

    public SCO getSCO();
    public void setSCO(SCO sco);
}
