package kornell.core.scorm12.lom;

public interface Agregated {
    enum Type {
        LearningResource,
        Aggregation
    }

    //PreReqs
    public Type getType();
    public void setType(Type type);

    public LearningResource getLearningResource();
    public void setLearningResource(LearningResource resource);

    public void getAggregation();
    public void setAggregation(Aggregation aggregation);
}
