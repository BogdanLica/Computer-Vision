package uk.ac.soton.ecs.bl1g17;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.model.EigenImages;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.io.DataOutput;
import java.util.*;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
            VFSGroupDataset<FImage> dataset =
                    new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);


            // 13.1.2. Exercise 2: Explore the effect of training set size
            // 1. The accuracy of the classifier decreases
            // 2. The number of total features is the same ??????
            int nTraining = 5;
            int nTesting = 5;
            GroupedRandomSplitter<String, FImage> splits =
                    new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
            GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
            GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();



            List<FImage> basisImages = DatasetAdaptors.asList(training);
            int nEigenvectors = 100;
            EigenImages eigen = new EigenImages(nEigenvectors);
            eigen.train(basisImages);


            List<FImage> eigenFaces = new ArrayList<FImage>();
            for (int i = 0; i < 12; i++) {
                eigenFaces.add(eigen.visualisePC(i));
            }
            DisplayUtilities.display("EigenFaces", eigenFaces);



            // build all the features
            Map<String, DoubleFV[]> features = buildFeatures(training, nTraining, eigen);
            System.out.println(features.keySet().size());

            // extract features from images
            guessImages(testing, eigen, features);


            // 13.1.3. Exercise 3: Apply a threshold
            // Good value => 0.8 * max value of the min values of the Euclidean distance
            // https://pdfs.semanticscholar.org/1e1c/fcd1da24ec87a4b59b248e826c14a82c66f8.pdf
            // add Harvard citation
            double threshold = findThreshold(testing, eigen, features);
            guessImagesWithUnknown(testing, eigen, features, threshold);

            // 13.1.1. Exercise 1: Reconstructing faces
//            FImage randomTestTarget = testing.getRandomInstance();
//            DoubleFV randomTestFeature = eigen.extractFeature(randomTestTarget);
//            FImage res = eigen.reconstruct(randomTestFeature).normalise();
//            DisplayUtilities.display("Reconstructed Face", res);
            // END


        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }



    private static Map<String, DoubleFV[]> buildFeatures(GroupedDataset<String, ListDataset<FImage>, FImage> training,
                                                         int nTraining, EigenImages eigen){
        Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();

        for (final String person : training.getGroups()) {
            final DoubleFV[] fvs = new DoubleFV[nTraining];

            for (int i = 0; i < nTraining; i++) {
                final FImage face = training.get(person).get(i);
                fvs[i] = eigen.extractFeature(face);
            }
            features.put(person, fvs);
        }


        return features;
    }


    private static double findThreshold(GroupedDataset<String, ListDataset<FImage>, FImage> testing,
                                        EigenImages eigen, Map<String, DoubleFV[]> features){
        double maxVal = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);
                double minDistance = Double.MAX_VALUE;

                for (final String person : features.keySet()) {
                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);
                        if (distance < minDistance ) {
                            minDistance = distance;
                        }
                    }
                }

                if(minDistance != Double.MAX_VALUE){
                    maxVal = Math.max(maxVal, minDistance);
                }

            }
        }

        return maxVal* 0.8;
    }


    private static void guessImagesWithUnknown(GroupedDataset<String, ListDataset<FImage>, FImage> testing, EigenImages eigen,
                                    Map<String, DoubleFV[]> features, double threshold){
        double correct = 0, incorrect = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);
                String bestPerson = null;
                double minDistance = Double.MAX_VALUE;

                for (final String person : features.keySet()) {
                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);
                        if (distance < minDistance) {
                            minDistance = distance;
                            bestPerson = person;
                        }
                    }
                }

                if(minDistance > threshold){
                    bestPerson = "unknown";
                }
                System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);

                if (truePerson.equals(bestPerson))
                    correct++;
                else
                    incorrect++;
            }
        }

        System.out.println("Accuracy: " + (correct / (correct + incorrect)));
    }



    private static void guessImages(GroupedDataset<String, ListDataset<FImage>, FImage> testing, EigenImages eigen,
                                    Map<String, DoubleFV[]> features){
        double correct = 0, incorrect = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);

                String bestPerson = null;
                double minDistance = Double.MAX_VALUE;
                for (final String person : features.keySet()) {
                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);

                        if (distance < minDistance) {
                            minDistance = distance;
                            bestPerson = person;
                        }
                    }
                }

                System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);

                if (truePerson.equals(bestPerson))
                    correct++;
                else
                    incorrect++;
            }
        }

        System.out.println("Accuracy: " + (correct / (correct + incorrect)));
    }
}
