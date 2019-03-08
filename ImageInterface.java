import java.io.File;

public interface ImageInterface {
    /**
     * Method to change the contrast of an image.
     *
     * @param pixels3D 3D pixels array data of the original image
     * @param noRows Number of rows of pixels
     * @param noColumns number of columns of pixels
     * @param outputFile destination file(s) for the new image
     * @return 3D pixels array data of the new image (after contrast modification)
     */
    int[][][] imageModification(int[][][] pixels3D, int noRows, int noColumns, File... outputFile);

    /**
     * Method to calculate and return the mean of all the color values
     *
     * @param pixels3D 3D pixels array data of the image
     * @param noRows number of rows of pixels
     * @param noColumns number of columns of pixels
     * @return mean of all the color values
     */
    int calculateMean(int[][][] pixels3D, int noRows, int noColumns);

    /**
     * Method to remove the mean causing the new mean value to be 0
     *
     * @param pixels3D 3D pixels array data of the image
     * @param noRows number of rows of pixels
     * @param noColumns number of columns of pixels
     * @param mean original mean value
     */
    void subtractMean(int[][][] pixels3D, int noRows, int noColumns, int mean);

    /**
     * Method to scale the data and expand or compress the distribution
     *
     * @param pixels3D 3D pixels array data of the image
     * @param noRows number of rows of pixels
     * @param noColumns number of columns of pixels
     * @param scale scale factor
     */
    void modifyDistributionByScaling(int[][][] pixels3D, int noRows, int noColumns, double scale);

    /**
     * Method to maintain the color data between 0 and 255
     *
     * @param pixels3D 3D pixels array data of the image
     * @param noRows number of rows of pixels
     * @param noColumns number of columns of pixels
     */
    void range(int[][][] pixels3D, int noRows, int noColumns);

}



