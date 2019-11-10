package uk.ac.soton.ecs.bl1g17;

import org.apache.commons.lang3.ArrayUtils;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.image.segmentation.SegmentationUtilities;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;
import org.openimaj.util.pair.IntFloatPair;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
            JFrame frame = DisplayUtilities.createNamedWindow("Clustering");

            // read the Image
            String landscape = "http://www.iso100.com.au/wp-content/uploads/2017/07/iso100photography_LANDSCAPE_JASPER_1.jpg";
            MBFImage myImage = ImageUtilities.readMBF(new URL(landscape));
            DisplayUtilities.display(myImage, frame);

            // Exercise 3.1.2. Exercise 1: The PixelProcessor

            // clustering
            MBFImage resultClustering = kClustering(myImage);
            DisplayUtilities.display(resultClustering, frame);


             // segmentation
            MBFImage resultConnectedComponents = connectComponents(resultClustering);
            DisplayUtilities.display(resultConnectedComponents, frame);




            // Exercise 3.1.2. Exercise 2: A real segmentation algorithm
            // How it compares:
            // 1. more appropriate as at the beginning it does smoothing of the image
            // to remove the noise and possibly components that cannot be connected
            // 2. slower before more pre-processing is involved(smoothing, building graph)
            FelzenszwalbHuttenlocherSegmenter segmenter = new  FelzenszwalbHuttenlocherSegmenter();
            MBFImage res = SegmentationUtilities.renderSegments(myImage, segmenter.segment(myImage));
            DisplayUtilities.display(res, frame);


            System.out.println("Done");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MBFImage connectComponents(MBFImage img) {
        MBFImage clone = img.clone();

        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
        List<ConnectedComponent> components = labeler.findComponents(img.flatten());

        int i = 0;
        for(ConnectedComponent comp : components){
            if(comp.calculateArea() < 450) continue;

            clone.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
        }

        return clone;
    }


    private static MBFImage kClustering(MBFImage img){
        // transform image
        MBFImage clone = img.clone();
        MBFImage colorSpaceImg = ColourSpace.convert(clone, ColourSpace.CIE_Lab);


        //clustering
        FloatKMeans cluster = FloatKMeans.createExact(6);

        float[][] imageData = colorSpaceImg.getPixelVectorNative(new float[colorSpaceImg.getHeight() * colorSpaceImg.getWidth()][3]);
        final FloatCentroidsResult res = cluster.cluster(imageData);

        final float[][] centroids = res.centroids;
        for (float[] fs : centroids) {
            System.out.println(Arrays.toString(fs));
        }

        // classification
        final HardAssigner<float[], float[], IntFloatPair> assigner = res.defaultHardAssigner();


        // Exercise 3.1.2. Exercise 1: The PixelProcessor
        // Advantage:
        // 1. being a separate class means that the code can be reused
        // Disadvantage:
        // 1. extra time spent allocating from Float[] to float[] and back to Float[]

        colorSpaceImg.processInplace((PixelProcessor<Float[]>) floats -> {
            int val =  assigner.assign(ArrayUtils.toPrimitive(floats));
            return ArrayUtils.toObject(centroids[val]);
        });

//        for(int y = 0; y < colorSpaceImg.getHeight(); y++){
//            for(int x = 0; x < colorSpaceImg.getWidth(); x++){
//                float[] pixels = colorSpaceImg.getPixelNative(x,y);
//                int centroid = assigner.assign(pixels);
//                colorSpaceImg.setPixelNative(x, y, centroids[centroid]);
//            }
//        }


        return ColourSpace.convert(colorSpaceImg, ColourSpace.RGB);
    }
}
