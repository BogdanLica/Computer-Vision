package uk.ac.soton.ecs.bl1g17.ch2;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
    	//Create an image
        try {
            // 2.1.1. Exercise 1: DisplayUtilities
            JFrame window = DisplayUtilities.createNamedWindow("My only window");
            MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
            DisplayUtilities.display(image, window);

            // only shows the values for the RED Channel of the colourSpace
            //DisplayUtilities.display(image.getBand(0), "Red Channel");

            // preserve the image for other processing
            // set the values for green and blue to 0
            MBFImage clone1 = image.clone();
            for (int row=0; row<image.getHeight(); row++) {
                for(int column=0; column<image.getWidth(); column++) {
                    clone1.getBand(1).pixels[row][column] = 0;
                    clone1.getBand(2).pixels[row][column] = 0;
                }
            }
            DisplayUtilities.display(clone1, window);



            // shorter version
            MBFImage clone2 = image.clone();
            clone2.getBand(1).fill(0f);
            clone2.getBand(2).fill(0f);
            DisplayUtilities.display(clone2, window);

            MBFImage cloneEdgeDetection = image.clone();
            cloneEdgeDetection.processInplace(new CannyEdgeDetector());
            DisplayUtilities.display(cloneEdgeDetection, window);


            // draw on top of the image
            MBFImage edgeDetectionWithMessage = cloneEdgeDetection.clone();


            Ellipse smallEllipse = new Ellipse(700f, 450f, 20f, 10f, 0f);
            Ellipse mediumEllipse = new Ellipse(650f, 425f, 25f, 12f, 0f);
            Ellipse largeEllipse = new Ellipse(600f, 380f, 30f, 15f, 0f);
            Ellipse extraLargeEllipse = new Ellipse(500f, 300f, 100f, 70f, 0f);


            edgeDetectionWithMessage.drawShapeFilled(smallEllipse , RGBColour.WHITE);
            edgeDetectionWithMessage.drawShapeFilled(mediumEllipse, RGBColour.WHITE);
            edgeDetectionWithMessage.drawShapeFilled(largeEllipse, RGBColour.WHITE);
            edgeDetectionWithMessage.drawShapeFilled(extraLargeEllipse, RGBColour.WHITE);

            edgeDetectionWithMessage.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
            edgeDetectionWithMessage.drawText("Nice", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);

            DisplayUtilities.display(edgeDetectionWithMessage, window);


            // 2.1.2. Exercise 2: Drawing
            MBFImage edgeDetectionWithMessageBordered = cloneEdgeDetection.clone();


            edgeDetectionWithMessageBordered.drawShapeFilled(smallEllipse, RGBColour.WHITE);
            edgeDetectionWithMessageBordered.drawShape(smallEllipse, 5 ,RGBColour.RED);

            edgeDetectionWithMessageBordered.drawShapeFilled(mediumEllipse, RGBColour.WHITE);
            edgeDetectionWithMessageBordered.drawShape(mediumEllipse, 5, RGBColour.RED);

            edgeDetectionWithMessageBordered.drawShapeFilled(largeEllipse,RGBColour.WHITE);
            edgeDetectionWithMessageBordered.drawShape(largeEllipse, 5,RGBColour.RED);

            edgeDetectionWithMessageBordered.drawShapeFilled(extraLargeEllipse,RGBColour.WHITE);
            edgeDetectionWithMessageBordered.drawShape(extraLargeEllipse, 5, RGBColour.RED);

            edgeDetectionWithMessageBordered.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
            edgeDetectionWithMessageBordered.drawText("Nice", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);

            DisplayUtilities.display(edgeDetectionWithMessageBordered, window);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
