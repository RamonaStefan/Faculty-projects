import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

abstract class OneImage {
    private BufferedImage originalImage;    // Original image
    private int noColumns;                  // Number of columns of pixels
    private int noRows;                     // Number of rows of pixels
    private static File defaultImage;       // Source file

    // Constructor with parameters
    OneImage(BufferedImage originalImage, File defaultImage, int noColumns, int noRows) {
        setOriginalImage(originalImage);
        setDefaultImage(defaultImage);
        setNoColumns(noColumns);
        setNoRows(noRows);
    }

    // Constructor without parameters
    OneImage() {
        setDefaultImage(new File("resources/tiger.bmp"));
        try {
            setOriginalImage(ImageIO.read(getDefaultImage()));
        } catch (IOException e) {
            System.out.println("Could not find any photo with that name.");
        }
        assert originalImage != null;
        setNoColumns(originalImage.getWidth());
        setNoRows(originalImage.getHeight());
    }

    /**
     * Method to convert the data in the int pixels1D array into a 3D array of ints.
     * The dimensions of the 3D array are row, col, and color.
     * Row and col correspond to the rows and columns of the image.
     * Color corresponds to color information at the following index levels in the third dimension: 0-red, 1-green, 2-blue.
     *
     * @param pixels1D  1D pixels array data of the image
     * @param noColumns number of columns of pixels
     * @param noRows    Number of rows of pixels
     * @return a vector of pixels with three dimensions
     */
    abstract int[][][] convert3Dim(int[] pixels1D, int noColumns, int noRows);

    //getters & setters
    static File getDefaultImage() {
        return defaultImage;
    }

    static void setDefaultImage(File defaultImage) {
        OneImage.defaultImage = defaultImage;
    }

    BufferedImage getOriginalImage() {
        return originalImage;
    }

    void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    int getNoColumns() {
        return noColumns;
    }

    void setNoColumns(int noColumns) {
        this.noColumns = noColumns;
    }

    int getNoRows() {
        return noRows;
    }

    void setNoRows(int noRows) {
        this.noRows = noRows;
    }
}
