package uk.ac.soton.ecs.bl1g17;

import jogamp.opengl.glu.nurbs.Bin;
import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.Dataset;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.dataset.BingImageDataset;
import org.openimaj.image.dataset.FlickrImageDataset;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.BingAPIToken;
import org.openimaj.util.api.auth.common.FlickrAPIToken;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        try {
//            String fullPath = new File("src/data").getCanonicalPath();
//            System.out.println(fullPath);
//            VFSListDataset<FImage> images =
//                    new VFSListDataset<>(fullPath, ImageUtilities.FIMAGE_READER);
//
//
//            System.out.println(images.size());
//            DisplayUtilities.display(images.getRandomInstance(), "A random image from the dataset");
//            DisplayUtilities.display("My images", images);
//
//            VFSListDataset<FImage> faces =
//                    new VFSListDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
//            DisplayUtilities.display("ATT faces", faces);
//
//            VFSGroupDataset<FImage> groupedFaces =
//                    new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
//
//
//            for (final Map.Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()) {
//                DisplayUtilities.display(entry.getKey(), entry.getValue());
//            }
//
//            FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);
//            FlickrImageDataset<FImage> cats =
//                    FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
//            DisplayUtilities.display("Cats", cats);





            // 6.1.1. Exercise 1: Exploring Grouped Datasets
            VFSGroupDataset<FImage> allGroupedFaces =
                    new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
            List<FImage> result = new LinkedList<>();

            for (final Map.Entry<String, VFSListDataset<FImage>> entry : allGroupedFaces.entrySet()) {
                result.add(entry.getValue().getRandomInstance());
            }

            DisplayUtilities.display("Random People", result);


            // 6.1.2. Exercise 2: Find out more about VFS datasets
            // Supported sources:
            // 1. BZIP2
            // 2. File
            // 3. FTP
            // 4. FTPS
            // 5. GZIP
            // 6. HDFS
            // 7. HTTP
            // 8. HTTPS
            // 9. Jar
            // 10. RAM
            // 11. RES
            // 12. SFTP
            // 13. Tar
            // 14. Temp
            // 15. WebDAV
            // 16. Zip



            // 6.1.3. Exercise 3: Try the BingImageDataset dataset
            BingAPIToken bingToken = DefaultTokenFactory.get(BingAPIToken.class);
            BingImageDataset<FImage> pandas =
                    BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "panda", 10);
            DisplayUtilities.display("Pandas", pandas);



            // 6.1.4. Exercise 4: Using MapBackedDataset
            bingToken = DefaultTokenFactory.get(BingAPIToken.class);
            String[] famousPeople = new String[]{
                    "Stephen Hawking",
                    "Nikolas Tesla"
            };
            List<BingImageDataset<FImage>> celebrities = new LinkedList<>();


            for(String famous : famousPeople)
                celebrities.add(
                        BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, famous, 15)
                );


            MapBackedDataset<String, BingImageDataset<FImage>, FImage> map = MapBackedDataset.of(celebrities);
            for (final Map.Entry<String, BingImageDataset<FImage>> entry : map.entrySet()) {
                DisplayUtilities.display(entry.getKey(), entry.getValue());
            }


        } catch (FileSystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
