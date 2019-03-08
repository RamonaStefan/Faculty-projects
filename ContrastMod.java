import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * The contrast of an image is determined by the width of the distribution of the
 * color values belonging to the image.
 * If all color values are grouped together in a very tight distribution, the details
 * in the image will tend to be washed out and the overall appearance of the image will
 * tend toward some shade of gray.
 * The shade will depend on the location of the grouping between the extremes of 0 and 255.
 * At the extremes, if the color values are closely grouped near zero, the colors will
 * range from black to dark gray.
 * If the color values are grouped near 255, the colors will range from very light gray to white.
 * The contrast of the image can be increased by increasing the width of the distribution
 * of the color values.
 * The contrast can be decreased by decreasing the width of the distribution.
 * A straightforward way to change the width of the distribution with, or without changing
 * the general location of the distribution consists of the following steps:
 * - Calculate the mean of all the color values.
 * - Subtract the mean from every color value, causing the mean value to be shifted to zero.
 * - Multiply every color value by the same scale factor
 * * If the scale factor is greater than 1.0, the width of the distribution will
 * be increased.
 * * If the scale factor is less than 1.0, the width of the distribution will be decreased.
 * - Add the original mean value to every color value to restore the distribution
 * to its original location or to move it to a new location.
 * A straightforward way to change the location of the distribution is to add (or subtract)
 * the same constant to every color value.
 * After performing these operations, it is important to make certain that all color values
 * fall within the range of an unsigned byte(0 - 255).
 * A simple way to do this is to simply clip the color values at those two limits if they fall outside the limits.
 * Slipping values to the limits will tend to narrow the distribution and to create a spike in
 * the distribution at the value of the limit.
 * The user can modify the contrast of the processed image by entering new values, named multiplicative factors.
 * The initial value is 1.  For this value, the processed image should be identical to the new image.
 * To increase the contrast, the user should type a value greater than 1.
 * To decrease the contrast, the user should type a value less than 1.
 */

public class ContrastMod implements ImageInterface {

    private double contrast;

    // Constructor without parameters
    ContrastMod() {
        setContrast(1);
    }

    // Constructor with parameters
    public ContrastMod(double contrast) {
        setContrast(contrast);
    }

    public int[][][] imageModification(int[][][] pixels3D, int noRows, int noColumns, File... outputFile) {
        long startTime = System.nanoTime();                         // time at the beginning of a phase
        long stopTime;                                              // time at the end of a phase
        String answer;                                              // user's answer
        boolean ok = false;                                                 // start image contrast modification or not
        Scanner input = new Scanner(System.in);                     // value imputed by the user
        int mean;                                                   // mean of all the color values
        int red;                                                    // red data component
        int green;                                                  // green data component
        int blue;                                                   // blue data component
        int value;                                                  // color value
        int[][][] output3D = new int[noRows][noColumns][3];         // 3D pixel array data of the new image
        BufferedImage finalImage = new BufferedImage(noColumns,
                noRows, BufferedImage.TYPE_INT_RGB);                // new image


        // Get user input values for contrast.
        // These values will be used as multipliers to change the contrast.
        System.out.println("Do you want to change the contrast? (y/n)");
        answer = input.nextLine();
        switch (answer) {
            case "Y":
            case "y":
                ok = true;
                System.out.println("Enter the contrast value:");
                setContrast(input.nextDouble());
                break;
            case "n":
            case "N":
                System.out.println("No changes.");
                output3D = pixels3D;
                break;
            default:
                System.out.println("You had not choose a correct option. No changes");
                break;
        }
        input.close();
        if (ok == true) {
            // Copy the 3D array to avoid making permanent changes to the original image data.
            for (int row = 0; row < noRows; row++) {
                for (int col = 0; col < noColumns; col++) {
                    System.arraycopy(pixels3D[row][col], 0,
                            output3D[row][col], 0, 3);
                }
            }
            // Get, save and remove the mean.
            mean = calculateMean(output3D, noRows, noColumns);
            subtractMean(output3D, noRows, noColumns, mean);

            // Scale each color value by the contrast multiplier in order to
            // either expand or compress the distribution.
            modifyDistributionByScaling(output3D, noRows, noColumns, getContrast());

            // Restore the mean to a non-zero value by adding the same value to each color value.
            shift(output3D, noRows, noColumns, mean);

            // Clip all color values at 0 and 255 to make certain that no color value is out of the range 0-255.
            range(output3D, noRows, noColumns);

            // Set the pixels of the new image to the specified RGB value
            for (int r = 0; r < noRows; r++)
                for (int c = 0; c < noColumns; c++) {
                    red = output3D[r][c][0];
                    green = output3D[r][c][1];
                    blue = output3D[r][c][2];
                    value = (red << 16) | (green << 8) | blue;
                    finalImage.setRGB(c, r, value);
                }
            stopTime = System.nanoTime();
            // Calculate and display the processing time.
            System.out.print("Processing image: ");
            System.out.println(calculateTime(startTime, stopTime) + " seconds");

            startTime = System.nanoTime();
            try {
                // Write image
                for (File outputFileVariable : outputFile) {
                    ImageIO.write(finalImage, "bmp", outputFileVariable);
                }
            } catch (IOException e) {
                System.out.println("Could not write the photo");
                System.err.println(e.getMessage());
            }
            stopTime = System.nanoTime();
            // Calculate and display the source file writing time.
            System.out.print("Destination file writing: ");
            System.out.println(calculateTime(startTime, stopTime) + " seconds");
        }
        // Return the modified 3D array of pixel data.
        return output3D;

    }

    public int calculateMean(int[][][] pixels3D, int noRows, int noColumns) {

        int counterPixels = 0;
        long sumPixels = 0;
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    sumPixels += pixels3D[r][c][i];
                }
                counterPixels += 3;
            }
        }
        return (int) (sumPixels / counterPixels);

    }

    public void subtractMean(int[][][] pixels3D, int noRows, int noColumns, int mean) {
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    pixels3D[r][c][i] -= mean;
                }
            }
        }
    }

    public void modifyDistributionByScaling(int[][][] pixels3D, int noRows, int noColumns, double scale) {
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    pixels3D[r][c][i] *= scale;
                }
            }
        }
    }

    public void range(int[][][] pixels3D, int noRows, int noColumns) {
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    if (pixels3D[r][c][i] < 0)
                        pixels3D[r][c][i] = 0;
                    if (pixels3D[r][c][i] > 255)
                        pixels3D[r][c][i] = 255;
                }
            }
        }
    }

    /**
     * Method to shift the mean to a new value in order to restore the distribution to its
     * original location or to move it to a new location.
     *
     * @param pixels3D  3D pixels array data of the image
     * @param noRows    number of rows of pixels
     * @param noColumns number of columns of pixels
     * @param mean      original mean value
     */

    private void shift(int[][][] pixels3D, int noRows, int noColumns, int mean) {
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    pixels3D[r][c][i] += mean;
                }
            }
        }
    }

    private void shift(int[][][] pixels3D, int noRows, int noColumns) {
        final int MEAN = 20;
        for (int r = 0; r < noRows; r++) {
            for (int c = 0; c < noColumns; c++) {
                for (int i = 0; i < 3; i++) {
                    pixels3D[r][c][i] = pixels3D[r][c][i] + MEAN;
                }
            }
        }
    }

    /**
     * Method to calculate and return the duration of a phase
     *
     * @param startTime time at the beginning of a phase
     * @param stopTime  time at the end of a phase
     * @return duration of a phase by calculating it and converting it from nanoseconds to seconds
     */
    private double calculateTime(long startTime, long stopTime) {
        return (stopTime - startTime) * Math.pow(10, -9);
    }

    /**
     * Method to calculate and return how much time has passed since a certain moment
     *
     * @param startTime time at the beginning of a phase
     * @return time passed since startTime in seconds
     */
    private static double calculateTime(long startTime) {
        return (System.nanoTime() - startTime) * Math.pow(10, -9);
    }

    //getters & setters
    private double getContrast() {
        return contrast;
    }

    private void setContrast(double contrast) {
        this.contrast = contrast;
    }

}