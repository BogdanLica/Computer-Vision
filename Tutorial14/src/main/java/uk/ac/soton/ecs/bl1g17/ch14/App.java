package uk.ac.soton.ecs.bl1g17.ch14;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.time.Timer;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;
import org.openimaj.util.parallel.partition.RangePartitioner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        Parallel.forIndex(0, 10, 1, System.out::println);

        try {
            VFSGroupDataset<MBFImage> allImages = Caltech101.getImages(ImageUtilities.MBFIMAGE_READER);
            GroupedDataset<String, ListDataset<MBFImage>, MBFImage> splitImages = GroupSampler.sample(allImages, 8, false);

            GroupedDataset<String, ListDataset<MBFImage>, MBFImage> allSplitImages = GroupSampler.sample(allImages, allImages.size(), false);

            List<MBFImage> output = new ArrayList<MBFImage>();
            ResizeProcessor resize = new ResizeProcessor(200);
            Timer t1 = Timer.timer();

            // no parallel processing
//            for (ListDataset<MBFImage> clzImages : splitImages.values()) {
//                MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
//
//                for (MBFImage i : clzImages) {
//                    MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
//                    tmp.fill(RGBColour.WHITE);
//
//                    MBFImage small = i.process(resize).normalise();
//                    int x = (200 - small.getWidth()) / 2;
//                    int y = (200 - small.getHeight()) / 2;
//                    tmp.drawImage(small, x, y);
//
//                    current.addInplace(tmp);
//                }
//                current.divideInplace((float) clzImages.size());
//                output.add(current);
//            }
//
//            System.out.println("Time: " + t1.duration() + " ms");
//            DisplayUtilities.display("Result", output);


            for (ListDataset<MBFImage> clzImages : allSplitImages.values()) {
                final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
                Parallel.forEachPartitioned(new RangePartitioner<MBFImage>(clzImages), it -> {
                    MBFImage tmpAccum = new MBFImage(200, 200, 3);
                    MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);

                    while (it.hasNext()) {
                        final MBFImage i = it.next();
                        tmp.fill(RGBColour.WHITE);

                        final MBFImage small = i.process(resize).normalise();
                        final int x = (200 - small.getWidth()) / 2;
                        final int y = (200 - small.getHeight()) / 2;
                        tmp.drawImage(small, x, y);
                        tmpAccum.addInplace(tmp);
                    }
                    synchronized (current) {
                        current.addInplace(tmpAccum);
                    }
                });
                current.divideInplace((float) clzImages.size());
                output.add(current);
            }

            System.out.println("Time: " + t1.duration() + " ms");
            DisplayUtilities.display("Result", output);



            // 14.1.1. Exercise 1: Parallelise the outer loop
            // Does this make the code faster? What are the pros and cons of doing this?
            // Time - simmilar
            // small sample -> time 8028ms (inner loop) vs 8259ms (outer loop)
            // full sample -> time 34608ms (inner loop) vs 35449ms (outer loop)
            // adv:
            // * paralelising the outer loop means the code is more scalable when run on multiple computers (a cluster)
            // * concurrency on a machine is limited by the total number of threads that can run at the same time, while
            // this does not apply for a cluster
            // cons:
            // * for small samples, the implementation is less efficient than parallelising the inner loop
            output.clear();
            Timer t3 = Timer.timer();
            Parallel.forEachPartitioned(new RangePartitioner<ListDataset<MBFImage>>(allSplitImages.values()), list -> {
                final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);

                while (list.hasNext()){
                    ListDataset<MBFImage> subset = list.next();
                    for (MBFImage i : subset) {
                        MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
                        tmp.fill(RGBColour.WHITE);

                        MBFImage small = i.process(resize).normalise();
                        int x = (200 - small.getWidth()) / 2;
                        int y = (200 - small.getHeight()) / 2;
                        tmp.drawImage(small, x, y);

                        current.addInplace(tmp);
                    }
                    current.divideInplace((float) subset.size());

                    synchronized (output){
                        output.add(current);
                    }
                }

            });

            System.out.println("Time: " + t3.duration() + " ms");
            DisplayUtilities.display("Result", output);





        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
