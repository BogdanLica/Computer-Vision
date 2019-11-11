package uk.ac.soton.ecs.bl1g17;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
            final URL[] imageUrls = new URL[]{
                    new URL( "http://openimaj.org/tutorial/figs/hist1.jpg" ),
                    new URL( "http://openimaj.org/tutorial/figs/hist2.jpg" ),
                    new URL( "http://openimaj.org/tutorial/figs/hist3.jpg" )
            };
            List<MBFImage> images = new ArrayList<>(3);


            List<MultidimensionalHistogram> histograms = new ArrayList<>();
            HistogramModel model = new HistogramModel(4, 4, 4);

            for(URL image: imageUrls){
                MBFImage tmpImage = ImageUtilities.readMBF(image);
                model.estimateModel(tmpImage);

                histograms.add(model.histogram.clone());
                images.add(tmpImage);
            }


//            for(int i = 0; i < histograms.size(); i++){
//                for(int j =i + 1; j < histograms.size(); j++){
//                    MultidimensionalHistogram hist1 = histograms.get(i);
//                    MultidimensionalHistogram hist2 = histograms.get(j);
//
//                    double distance = hist1.compare(hist2, DoubleFVComparison.EUCLIDEAN);
//                    System.out.println(distance);
//                }
//            }

            // 4.1.1. Exercise 1: Finding and displaying similar images
            // The hist1.jpg and hist2.jpg are the most similar
            // this is to be expected as the colors in both are closer to each other than compared to hist3.jpg
            Queue<int[]> similar = groupingHistograms(histograms, DoubleFVComparison.EUCLIDEAN);
            List<MBFImage> mostSimilar = extractImages(images, similar);
            DisplayUtilities.display("Most similar", mostSimilar);


            // 4.1.2. Exercise 2: Exploring comparison measures
            // The hist2.jpg and hist3.jpg are the most similar
            // ??
            Queue<int[]> similar2 = groupingHistograms(histograms, DoubleFVComparison.INTERSECTION);
            List<MBFImage> mostSimilar2 = extractImages(images, similar2);
            DisplayUtilities.display("Most similar", mostSimilar2);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





    private static Queue<int[]> groupingHistograms(List<MultidimensionalHistogram> histograms, DoubleFVComparison comparisonType){
        //create queue
        Queue<int[]> similar = new PriorityQueue<>((group1, group2) -> {
            double distance1 = histograms.get(group1[0]).compare(histograms.get(group1[1]), comparisonType);
            double distance2 = histograms.get(group2[0]).compare(histograms.get(group2[1]), comparisonType);

            return Double.compare(distance1, distance2);
        });

        // add elements
        for(int i = 0; i < histograms.size(); i++){
            for(int j =i + 1; j < histograms.size(); j++){
                similar.offer(new int[]{ i, j });
            }
        }

        return similar;
    }


    private static List<MBFImage> extractImages(List<MBFImage> images, Queue<int[]> similar ){
        int[] candidates = similar.peek();
        return Arrays.stream(candidates)
                .mapToObj(images::get)
                .collect(Collectors.toList());
    }
}
