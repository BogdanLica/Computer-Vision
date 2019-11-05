package uk.ac.soton.ecs.bl1g17.hybridimages;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.typography.hershey.HersheyFont;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        MBFImage lowImage = null;
        MBFImage highImage = null;
        float lowSigma = 5f;
        float highSigma = 5f;

        try {
            lowImage = ImageUtilities.readMBF(new File("data/panda.jpg"));
            highImage = ImageUtilities.readMBF(new File("data/flute.jpg"));

            MBFImage res = MyHybridImages.makeHybrid(lowImage, lowSigma, highImage, highSigma);

            createVisualisation(res, 5, 0.5f);

//            DisplayUtilities.display(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void createVisualisation(MBFImage img, int n, float amount){
        ResizeProcessor scaler = new ResizeProcessor(amount);
//        List<MBFImage> imagesResized = new LinkedList<>();
//        imagesResized.add(img);
        MBFImage[] imagesResized = new MBFImage[n+1];
        imagesResized[0] = img;
        int j = 1;



        MBFImage prevImage = img;
        for(int i = 1; i <= n; i++){
//            JFrame frame = DisplayUtilities.makeFrame("Image");
//            frame.setUndecorated(true);
//            DisplayUtilities.display(prevImage, frame);

            DisplayUtilities.displayLinked("Images", 1, prevImage);

            MBFImage tmpClone = prevImage.clone();
//            System.out.println(tmpClone.getWidth() + " <-> "  + tmpClone.getHeight());
            tmpClone.processInplace(scaler);
//            imagesResized.add(tmpClone);
            imagesResized[j] = tmpClone;

            prevImage = tmpClone;
            j++;
        }

//        DisplayUtilities.display("Images", n, imagesResized);

//        DisplayUtilities.display("Images", imagesResized);
    }
}
