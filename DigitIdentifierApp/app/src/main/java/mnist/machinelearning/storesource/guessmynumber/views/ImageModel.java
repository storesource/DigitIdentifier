package mnist.machinelearning.storesource.guessmynumber.views;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantsingh on 12/02/18.
 */

public class ImageModel {
    
    //line starts at x,y 
    //lineElements make up the line
    public static class LineElement {
        public float x;
        public float y;

        
        private LineElement(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public static class Line {
        
        private List<LineElement> elems = new ArrayList<>();

        private Line() {
        }

        private void addElement(LineElement elem) {
            elems.add(elem);
        }

        public int getElementSize() {
            return elems.size();
        }

        public LineElement getElement(int index) {
            return elems.get(index);
        }
    }


    private Line mCurrentLine;

    private int mWidth; 
    private int mHeight;


    private List<Line> LinesinMemory = new ArrayList<>();
    
    
    public ImageModel(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
    
    public void startLine(float x, float y) {
        mCurrentLine = new Line();
        mCurrentLine.addElement(new LineElement(x, y));
        LinesinMemory.add(mCurrentLine);
    }

    public void endLine() {
        mCurrentLine = null;
    }

    public void addLineElement(float x, float y) {
        if (mCurrentLine != null) {
            mCurrentLine.addElement(new LineElement(x, y));
        }
    }

    public int getLineSize() {
        return LinesinMemory.size();
    }

    public Line getLine(int index) {
        return LinesinMemory.get(index);
    }

    public void clear() {
        LinesinMemory.clear();
    }
}
