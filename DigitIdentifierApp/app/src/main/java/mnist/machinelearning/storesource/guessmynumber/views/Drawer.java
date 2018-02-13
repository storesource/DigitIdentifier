package mnist.machinelearning.storesource.guessmynumber.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by prashantsingh on 12/02/18.
 */

//Does the actual drawing using paint on canvas
public class Drawer {
    
    public static void renderModel(Canvas canvas, ImageModel model, Paint paint,
                                   int firstLineIndex) {
        
        paint.setAntiAlias(true);

        
        int lineSize = model.getLineSize();
        
        for (int i = firstLineIndex; i < lineSize; ++i) {
            
            ImageModel.Line line = model.getLine(i);
            //get ith line from memory
            
            paint.setColor(Color.BLACK);
            
            int elemSize = line.getElementSize();
            
            if (elemSize < 1) {
                continue;
            }
            // store that first line element in line i
            ImageModel.LineElement elem = line.getElement(0);

            float lastX = elem.x;//element coordinates
            float lastY = elem.y;

            //for each coordinate in the line
            for (int j = 0; j < elemSize; ++j) {
                //get the next coordinate
                elem = line.getElement(j);
                float x = elem.x;
                float y = elem.y;
                //line drawn beteen coordinates and coordinates updated
                canvas.drawLine(lastX, lastY, x, y, paint);

                lastX = x;
                lastY = y;
            }
        }
    }
}
