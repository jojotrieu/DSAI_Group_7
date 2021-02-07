public class Skill {

    private String action, entity, property;

    public Skill(String action, String entity, String property) {
        this.action = action;
        this.entity = entity;
        this.property = property;
    }

    public String getAction() {
        return action;
    }

    public String getEntity() {
        return entity;
    }

    public String getProperty() {
        return property;
    }
}