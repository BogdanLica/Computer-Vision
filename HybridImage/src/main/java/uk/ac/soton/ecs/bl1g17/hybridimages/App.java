package uk.ac.soton.ecs.bl1g17.hybridimages;

import org.openimaj.OpenIMAJ;
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
        float lowSigma = 25.0f;
        float highSigma = 5.0f;

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
        int totalWidth = img.getWidth();



        MBFImage prevImage = img;
        for(int i = 1; i <= n; i++){

            MBFImage tmpClone = prevImage.clone();
            tmpClone.processInplace(scaler);

            imagesResized[j] = tmpClone;
            totalWidth += tmpClone.getWidth();

            prevImage = tmpClone;
            j++;
        }

        MBFImage filteredImages = new MBFImage(totalWidth, img.getHeight());
        int x = 0;

        for(MBFImage curr : imagesResized){
            filteredImages.drawImage(curr, x, img.getHeight() - curr.getHeight());
            x += curr.getWidth();
        }

//        try {
//            ImageUtilities.write(filteredImages, new File("output.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        DisplayUtilities.display(filteredImages);
    }
}
