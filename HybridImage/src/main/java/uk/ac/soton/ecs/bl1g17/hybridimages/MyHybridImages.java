package uk.ac.soton.ecs.bl1g17.hybridimages;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FConvolution;
import org.openimaj.image.processing.convolution.Gaussian2D;


public class MyHybridImages {
    /**
     * Compute a hybrid image combining low-pass and high-pass filtered images
     *
     * @param lowImage
     *            the image to which apply the low pass filter
     * @param lowSigma
     *            the standard deviation of the low-pass filter
     * @param highImage
     *            the image to which apply the high pass filter
     * @param highSigma
     *            the standard deviation of the low-pass component of computing the
     *            high-pass filtered image
     * @return the computed hybrid image
     */
    public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, MBFImage highImage, float highSigma) {
        //implement your hybrid images functionality here.
        //Your submitted code must contain this method, but you can add
        //additional static methods or implement the functionality through
        //instance methods on the `MyHybridImages` class of which you can create
        //an instance of here if you so wish.
        //Note that the input images are expected to have the same size, and the output
        //image will also have the same height & width as the inputs.

        MBFImage low = lowPassFilter(lowImage, lowSigma);
        MBFImage high = highPassFilter(highImage, highSigma);

//        System.out.println(low.getWidth() + " < - > " + low.getHeight());
//        System.out.println(high.getWidth() + " < - > " + high.getHeight());


//        DisplayUtilities.display(low);
//        DisplayUtilities.display(high);


//        int differenceWidth = low.getWidth() - high.getWidth();
//        int differenceHeight = low.getHeight() - high.getHeight();
//
//        if(differenceHeight != 0 ){
//            System.out.println("here");
//            if(differenceHeight < 0)
//                low.padding(0, Math.abs(differenceHeight)/2, RGBColour.BLACK );
//            else high.padding(0, Math.abs(differenceHeight)/2, RGBColour.BLACK );
//        }
//
//        if(differenceWidth != 0 ){
//            System.out.println("here1");
//            if(differenceWidth < 0)
//                low.padding(Math.abs(differenceWidth)/2, 0 , RGBColour.BLACK );
//            else high.padding(Math.abs(differenceWidth)/2, 0 , RGBColour.BLACK );
//        }

//        return high.overlay(low,0,0).trim();

        return low.add(high);
    }


    /**
     * apply a low frequency filter
     * @param image the image to be modified
     * @param sigma the amount of blurness
     * @return the resulted image
     */
    private static MBFImage lowPassFilter(MBFImage image, float sigma){
        int size = getSizeFromSigma(sigma);

        FImage kernelGaussian = Gaussian2D.createKernelImage(size, sigma);
//        MBFImage paddedImage = createZeroPadding(image, sigma);
//        return image
//                .process(new FConvolution(kernelGaussian.pixels));
        return image
                .process(new MyConvolution(kernelGaussian.pixels));
    }

    private static MBFImage highPassFilter(MBFImage image, float sigma){
        MBFImage clone = image.clone();
        MBFImage lowFilter = lowPassFilter(image, sigma);

//        DisplayUtilities.display(clone);
//        DisplayUtilities.display(lowFilter);

        clone.subtractInplace(lowFilter);
        return clone;
    }


    private static MBFImage createZeroPadding(MBFImage img, float sigma ){
        int size = getSizeFromSigma(sigma);
        if (size % 2 != 0) size--; // size must be odd
        System.out.println(size);
        return img.padding(size/2, size/2, RGBColour.BLACK );
    }

    private static int getSizeFromSigma(float sigma){
        int size = (int) (8.0f * sigma + 1.0f); // (this implies the window is +/- 4 sigmas from the centre of the Gaussian)
        if (size % 2 == 0) size++; // size must be odd

        return size;
    }



}