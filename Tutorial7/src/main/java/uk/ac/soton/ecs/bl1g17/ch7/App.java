package uk.ac.soton.ecs.bl1g17.ch7;


import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.FFastGaussianConvolve;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;
import org.openimaj.video.xuggle.XuggleVideo;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
            Video video = new VideoCapture(600, 600);
//             video = new XuggleVideo(new URL("http://static.openimaj.org/media/tutorial/keyboardcat.flv"));

//            VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);


//            for (MBFImage mbfImage : video) {
//                DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
//            }


            VideoDisplay<MBFImage> displayEdgeDetection = VideoDisplay.createVideoDisplay(video);
            displayEdgeDetection.addVideoListener(
                    new VideoDisplayListener<MBFImage>() {
                        public void beforeUpdate(MBFImage frame) {
                            frame.processInplace(new CannyEdgeDetector());
                        }

                        public void afterUpdate(VideoDisplay<MBFImage> display) {
                        }
                    });



            // 7.1.1. Exercise 1: Applying different types of image processing to the video
            // the frame rate is faster than using the CannyEdgeDetector
            VideoDisplay<MBFImage> displayGaussian = VideoDisplay.createVideoDisplay(video);
            displayGaussian.addVideoListener(
                    new VideoDisplayListener<MBFImage>() {
                        @Override
                        public void beforeUpdate(MBFImage frame) {
                            frame.processInplace(new FFastGaussianConvolve(10f, 5));
                        }

                        @Override
                        public void afterUpdate(VideoDisplay<MBFImage> display) {
                        }
                    });

        } catch (VideoCaptureException ex) {
            ex.printStackTrace();
        }
    }
}
