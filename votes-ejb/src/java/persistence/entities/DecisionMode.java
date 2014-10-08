package persistence.entities;

public enum DecisionMode {

    SIMPLE_MAJORITY("poll.items.mode.simple"),
    REL_MAJORITY("poll.items.mode.rel"),
    ABS_MAJORITY("poll.items.mode.abs");

    private String label;

    private DecisionMode(String label) {
        this.label = label;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

}
