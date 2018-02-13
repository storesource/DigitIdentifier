package mnist.machinelearning.storesource.guessmynumber.classification;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * Created by prashantsingh on 13/02/18.
 */

public class ClassifierEngine {
    private static final float THRESHOLD = 0.1f;

    private TensorFlowInferenceInterface tfInterface;

    private String name;
    private String inputNodeName;
    private String outputNodeName;
    private int inputSize;
    private boolean feedKeepProb;

    private List<String> labels;
    private float[] output;
    private String[] outputNodeNames;

    //labels read from file to memory
    private static List<String> readLabels(AssetManager am, String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)));

        String line;
        List<String> labels = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            labels.add(line);
        }

        br.close();
        return labels;
    }
    
    public ClassifierEngine (AssetManager assetManager, String name,
                                              String modelPath, String labelFile, int inputSize, String inputNodeName, String outputNodeName,
                                              boolean feedKeepProb) throws IOException {

        this.name = name;

        this.inputNodeName = inputNodeName;
        this.outputNodeName = outputNodeName;

        this.labels = readLabels(assetManager, labelFile);

        //actual tensorflow interface for android
        this.tfInterface = new TensorFlowInferenceInterface(assetManager, modelPath);
        int numClasses = 10;

        this.inputSize = inputSize;

        this.outputNodeNames = new String[] { outputNodeName };

        this.outputNodeName = outputNodeName;
        this.output = new float[numClasses];

        this.feedKeepProb = feedKeepProb;
    }

    public String name() {
        return name;
    }

    public Classifier recognize(final float[] pixels) {

        tfInterface.feed(inputNodeName, pixels, 1, inputSize, inputSize, 1);


        if (feedKeepProb) {
            tfInterface.feed("keep_prob", new float[] { 1 });
        }

        tfInterface.run(outputNodeNames);


        tfInterface.fetch(outputNodeName, output);

        Classifier ans = new Classifier();
        for (int i = 0; i < output.length; ++i) {
            System.out.println(output[i]);
            System.out.println(labels.get(i));
            if (output[i] > THRESHOLD && output[i] > ans.getOutput()) {
                ans.update(output[i], labels.get(i));
            }
        }

        return ans;
    }
}
