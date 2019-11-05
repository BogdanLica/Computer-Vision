package uk.ac.soton.ecs.bl1g17.hybridimages;

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
        int paddingWidth = kernel[0].length / 2;
        int paddingHeight = kernel.length / 2;
        FImage padded = image.padding(paddingWidth, paddingHeight, 0f);
        // get the sizes of the image
        int imageRows = padded.getRows();
        int imageCols = padded.getCols();

        // get the sizes of the template
        int templateRows = kernel.length;
        int templateCols = kernel[0].length;
        int tr = templateRows / 2;
        int tc = templateCols / 2;

        // create the buffered image
//        double[] res = new double[imageRows * imageCols];
        FImage tmpImage = new FImage(image.getCols(), image.getRows());
        tmpImage.fill(0f);


        for(int x = tc+1; x < imageCols - tc; x++){
            for(int y = tr+1; y < imageRows - tr; y++){
                float sum = 0;

                for(int iwin = 0; iwin < templateCols; iwin++){
                    for(int jwin = 0; jwin < templateRows; jwin++){
                        float originalImageVal = padded.pixels[y + jwin - tr - 1][x + iwin - tc - 1];

                        float templateImageVal = kernel[templateRows - 1 - jwin][templateCols - 1 -iwin] ;
                        sum += originalImageVal * templateImageVal;
                    }
                }

                tmpImage.pixels[y -paddingHeight ][x - paddingWidth] = sum;
            }
        }

        image.internalAssign(tmpImage);
        return;
    }
}