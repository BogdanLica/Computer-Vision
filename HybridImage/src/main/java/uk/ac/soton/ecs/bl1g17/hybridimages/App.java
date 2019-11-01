package uk.ac.soton.ecs.bl1g17.hybridimages;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        MBFImage dog = null;
        MBFImage cat = null;
        try {
            dog = ImageUtilities.readMBF(new File("data/dog.bmp"));
            cat = ImageUtilities.readMBF(new File("data/cat.bmp"));
//            DisplayUtilities.display(dog);
//            DisplayUtilities.display(cat);

            MBFImage res = MyHybridImages.makeHybrid(dog, 10f, cat, 10f);
            DisplayUtilities.display(cat);
            DisplayUtilities.display(dog);
            DisplayUtilities.display(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
