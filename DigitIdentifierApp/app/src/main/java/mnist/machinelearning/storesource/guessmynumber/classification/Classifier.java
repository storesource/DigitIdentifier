package mnist.machinelearning.storesource.guessmynumber.classification;

/**
 * Created by prashantsingh on 13/02/18.
 */

public class Classifier {

    private float output;

    private String label;

    Classifier() {
        this.output = -1.0F;
        this.label = null;
    }

    void update(float output, String label) {
        this.output = output;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public float getOutput() {
        return output;
    }
}
