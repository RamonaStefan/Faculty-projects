import java.awt.image.BufferedImage;
import java.io.File;

class PixelImage extends OneImage {
    private int[][][] pixels3D = new int[getNoRows()][getNoColumns()][3];                // 3D pixels array data of the image
    private int[] pixels1D = new int[getNoRows() * getNoColumns()];                      // 1D pixels array data of the image

    // Constructor with parameters
    PixelImage(BufferedImage originalImage, File defaultImage, int noColumns, int noRows, int[][][] pixels3d, int[] pixels1d) {
        super(originalImage, defaultImage, noColumns, noRows);
        setPixels3D(pixels3d);
        setPixels1D(pixels1d);
    }

    // Constructor without parameters
    PixelImage() {
        super();
        setPixels1D(null);
        setPixels3D(null);
    }

    int[][][] convert3Dim(int[] pixels1D, int noColumns, int noRows) {
        int[][][] array3D = new int[noRows][noColumns][3];                  // New 3D array populated with color data

        for (int r = 0; r < noRows; r++) {
            // Extract a row of pixel data into a temporary array of ints
            int[] temporaryArray = new int[noColumns];
            // Move the data into the 3D array.
            System.arraycopy(pixels1D, r * noColumns, temporaryArray, 0, noColumns);
            // Use the bitwise AND and bitwise right shift operations to mask all but the correct set of eight bits.
            for (int c = 0; c < noColumns; c++) {
                //Red data
                array3D[r][c][0] = (temporaryArray[c] >> 16) & 0xFF;
                //Green data
                array3D[r][c][1] = (temporaryArray[c] >> 8) & 0xFF;
                //Blue data
                array3D[r][c][2] = (temporaryArray[c]) & 0xFF;
            }
        }
        return array3D;
    }

    //getters & setters
    int[][][] getPixels3D() {
        return pixels3D;
    }

    void setPixels3D(int[][][] pixels3d) {
        pixels3D = pixels3d;
    }

    void setPixels3D() {
        for (int r = 0; r < getNoRows(); r++) {
            for(int c = 0; c < getNoColumns(); c++) {
                pixels3D[r][c][0] = 0;
                pixels3D[r][c][1] = 0;
                pixels3D[r][c][2] = 0;
            }
        }
    }

    int[] getPixels1D() {
        return pixels1D;
    }

    void setPixels1D(int[] pixels1d) {
        pixels1D = pixels1d;
    }

}
