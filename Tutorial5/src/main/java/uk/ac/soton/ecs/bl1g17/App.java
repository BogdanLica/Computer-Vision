package uk.ac.soton.ecs.bl1g17;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.*;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.transforms.HomographyModel;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.math.model.fit.RobustModelFitting;

import java.io.IOException;
import java.net.URL;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
            // Define Images
            MBFImage query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));
            MBFImage target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));

            // Create keypoints
            DoGSIFTEngine engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
            LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());


            // Basic Matcher
            LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
            DisplayUtilities.display(basicMatches);



            // Random Sample Consensus
            RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5));
            matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                    new FastBasicKeypointMatcher<Keypoint>(4), modelFitter);

            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
                    RGBColour.RED);

            DisplayUtilities.display(consistentMatches);

            MBFImage targetCopy = target.clone();
            targetCopy.drawShape(
                    query.getBounds().transform(modelFitter.getModel().getTransform().inverse()), 3,RGBColour.BLUE);
            DisplayUtilities.display(targetCopy);



            // 5.1.1. Exercise 1: Different matchers


            // BasicTwoWayMatcher without model
            matcher = new BasicTwoWayMatcher<>();
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage basicTwoWayMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
            DisplayUtilities.display(basicTwoWayMatches);


            // FastEuclideanKeypointMatcher without model
            matcher = new FastEuclideanKeypointMatcher<>(9000);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage fastEuclideanMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
            DisplayUtilities.display(fastEuclideanMatches);


            // VotingKeypointMatcher without model
            matcher = new VotingKeypointMatcher<>(4);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);

            MBFImage votingMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
            DisplayUtilities.display(votingMatches);



            // 5.1.2. Exercise 2: Different models


            //  Homography Model with RANSAC
            RobustHomographyEstimator modelFitter2 = new RobustHomographyEstimator(5, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5),
                    HomographyRefinement.SYMMETRIC_TRANSFER);
            matcher = createMatcher(modelFitter2, queryKeypoints, targetKeypoints, 4);
            drawMatches(query, target, "Homography Model with RANSAC", modelFitter2.getModel(), matcher );


            //  Homography Model with LMedS
            modelFitter2 = new RobustHomographyEstimator(0.5, HomographyRefinement.SYMMETRIC_TRANSFER);
            matcher = createMatcher(modelFitter2, queryKeypoints, targetKeypoints, 4);
            drawMatches(query, target, "Homography Model with LMedS", modelFitter2.getModel(), matcher );


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static LocalFeatureMatcher<Keypoint> createMatcher(RobustModelFitting model,
                                                                LocalFeatureList<Keypoint> qKeypoints,
                                                                LocalFeatureList<Keypoint> tKeypoints,
                                                               int threshold){

        LocalFeatureMatcher<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(threshold), model);

        matcher.setModelFeatures(qKeypoints);
        matcher.findMatches(tKeypoints);

        return matcher;
    }


    private static void drawMatches(MBFImage query, MBFImage target, String title,
                                    HomographyModel modelFitter, LocalFeatureMatcher<Keypoint> matcher){
        MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
                RGBColour.RED);

        DisplayUtilities.display(consistentMatches, title);

        MBFImage targetCopy = target.clone();
        targetCopy.drawShape(
                query.getBounds().transform(modelFitter.getTransform().inverse()), 3,RGBColour.BLUE);
        DisplayUtilities.display(targetCopy, title);
    }
}
