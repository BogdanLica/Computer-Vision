package uk.ac.soton.ecs.bl1g17;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;

import javax.swing.*;
import java.io.File;
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
            JFrame window = DisplayUtilities.createNamedWindow("My only window");
            MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));

            System.out.println(image.colourSpace);
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
