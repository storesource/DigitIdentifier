package mnist.machinelearning.storesource.guessmynumber;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mnist.machinelearning.storesource.guessmynumber.classification.Classifier;
import mnist.machinelearning.storesource.guessmynumber.classification.ClassifierEngine;
import mnist.machinelearning.storesource.guessmynumber.views.ImageModel;
import mnist.machinelearning.storesource.guessmynumber.views.NumberView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{

    private NumberView NumView;
    private Button clearButton;
    private Button guessButton;
    private ImageModel imageModel;
    private float mLastX;
    private float mLastY;
    private PointF tempPoint = new PointF();
    private static final int PIXEL_WIDTH = 28;
    private TextView resultText;
    private ClassifierEngine TensorflowEngine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NumView = (NumberView) findViewById(R.id.NumberView);
        imageModel = new ImageModel(28, 28);

        NumView.setModel(imageModel);
        NumView.setOnTouchListener(this);

        clearButton = (Button) findViewById(R.id.ClearButton);
        clearButton.setOnClickListener(this);

        resultText = (TextView) findViewById(R.id.ResultTextView);

        guessButton = (Button) findViewById(R.id.DetectButton);
        guessButton.setOnClickListener(this);

        prepareModel();

    }

    private void prepareModel(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TensorflowEngine = new ClassifierEngine(getAssets(), "Keras",
                            "opt_DigitIdentifierKerasModel.pb", "labels.txt", PIXEL_WIDTH,
                            "conv2d_1_input", "dense_2/Softmax", false);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        NumView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause(){
        NumView.onPause();
        super.onPause();
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ClearButton) {
            Log.i("onCLickEvent","Clear Button clicked");
            imageModel.clear();
            NumView.reset();
            NumView.invalidate();
            //resultText.setText("");
        } else if (view.getId() == R.id.DetectButton) {

            float pixels[] = NumView.getPixelData();

            //init an empty string to fill with the classification output
            String text = "";
            final Classifier classificationResult = TensorflowEngine.recognize(pixels);

            if (classificationResult.getLabel() == null) {
                text += TensorflowEngine.name() + ": Unknown\n";
            } else {
                text += String.format("%s says the number is %s, confidence: %f percent\n", TensorflowEngine.name(), classificationResult.getLabel(),
                        (classificationResult.getOutput()*100));
            }
            resultText.setText(text);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        Log.i("onTouchEvent","OnTouch event triggered");


        if (action == MotionEvent.ACTION_DOWN) {
            //starts to draw
            processTouchDown(event);
            return true;
            //follow the user
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
            //stop drawing when finger is lifted
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {
        //calculate the x, y coordinates where the user has touched
        mLastX = event.getX();
        mLastY = event.getY();

        NumView.calcPos(mLastX, mLastY, tempPoint);

        float lastConvX = tempPoint.x;
        float lastConvY = tempPoint.y;

        imageModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        NumView.calcPos(x, y, tempPoint);
        float newConvX = tempPoint.x;
        float newConvY = tempPoint.y;
        imageModel.addLineElement(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        NumView.invalidate();
    }

    private void processTouchUp() {
        imageModel.endLine();
    }
    
    private void performClearing(){
        imageModel.clear();
        NumView.reset();
        NumView.invalidate();
        resultText.setText(R.string.Result_text);
    }

}
