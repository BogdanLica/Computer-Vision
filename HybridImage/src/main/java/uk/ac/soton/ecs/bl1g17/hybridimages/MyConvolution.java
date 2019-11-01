package uk.ac.soton.ecs.bl1g17.hybridimages;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {
    private float[][] kernel;

    public MyConvolution(float[][] kernel) {
        //note that like the image pixels kernel is indexed by [row][column]
        this.kernel = kernel;
    }

    //TODO:
    /*
    * kernel[templateRows - jwin][templateCols - iwin] a bit different from book
     */
    @Override
    public void processImage(FImage image) {
        // get the sizes of the image
        int imageRows = image.getRows();
        int imageCols = image.getCols();

        // get the sizes of the template
        int templateRows = kernel.length;
        int templateCols = kernel[0].length;
        int tr = templateRows / 2;
        int tc = templateCols / 2;

        // create the buffered image
        double[] res = new double[imageRows * imageCols];
        FImage tmpImage = new FImage(res, imageCols, imageRows);


        for(int x = tc+1; x < imageCols - tc; x++){
            for(int y = tr+1; y < imageRows - tr; y++){
                float sum = 0;

                for(int iwin = 1; iwin <= templateCols; iwin++){
                    for(int jwin = 1; jwin <= templateRows; jwin++){
                        float originalImageVal = image.pixels[y + jwin - tr - 1][x + iwin - tc - 1];
                        float templateImageVal = kernel[templateRows - jwin][templateCols - iwin];
                        sum += originalImageVal * templateImageVal;
                    }
                }
                tmpImage.pixels[y][x] = sum;
            }
        }

//        System.out.println(tmpImage.toString());

        image.internalAssign(tmpImage);
        return;
    }
}