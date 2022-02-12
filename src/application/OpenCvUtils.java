package application;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCvUtils {

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }


    public static Mat image2Mat( Image image) {


        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        return bufferedImage2Mat( bImage);

    }

    // http://www.codeproject.com/Tips/752511/How-to-Convert-Mat-to-BufferedImage-Vice-Versa
    public static Mat bufferedImage2Mat(BufferedImage in)
    {
          Mat out;
          byte[] data;
          int r, g, b;
          int height = in.getHeight();
          int width = in.getWidth();
//          if(in.getType() == BufferedImage.TYPE_INT_RGB || in.getType() == BufferedImage.TYPE_INT_ARGB)
//          {
//              out = new Mat(height, width, CvType.CV_8UC1);
//              data = new byte[height * width * (int)out.elemSize()];
//              int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
//              for(int i = 0; i < dataBuff.length; i++)
//              {
//                  data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
//                  data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
//                  data[i*3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
//              }
//          }
//          else
//          {
              out = new Mat(height, width, CvType.CV_16U);
              data = new byte[height * width * (int)out.elemSize()];
              int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
              for(int i = 0; i < dataBuff.length; i++)
              {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
              
           }
           out.put(0, 0, data);
           return out;
     } 

    public static String getOpenCvResource(Class<?> clazz, String path) {
        try {
            return Paths.get( clazz.getResource(path).toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert image to Mat
    // alternate version http://stackoverflow.com/questions/21740729/converting-bufferedimage-to-mat-opencv-in-java
    public static Mat bufferedImage2Mat_v2(BufferedImage im) {
    	
        im = toBufferedImageOfType(im, BufferedImage.TYPE_USHORT_GRAY);

        // Convert INT to SHORT
        //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
        short[] pixels = ((DataBufferUShort) im.getRaster().getDataBuffer()).getData();

        // Create a Matrix the same size of image
        Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_16U);
        // Fill Matrix with image values
        image.put(0, 0, pixels);
        // okienkowanie openCV 
        return image;

    }
public static Mat bufferedImage2Mat_8bit(BufferedImage im) {
    	
        im = toBufferedImageOfType(im, BufferedImage.TYPE_BYTE_GRAY);

        // Convert INT to BYTE
        //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
        byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();

        // Create a Matrix the same size of image
        Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8U);
        // Fill Matrix with image values
        image.put(0, 0, pixels);
        // okienkowanie openCV 
        return image;

    }
    
  public static Mat bufferedImage2MatNormalImage(BufferedImage im) {
    	
        im = toBufferedImageOfType(im, BufferedImage.TYPE_3BYTE_BGR);

        // Convert INT to BYTE
        //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
        byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();

        // Create a Matrix the same size of image
        Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
        // Fill Matrix with image values
        image.put(0, 0, pixels);
        // okienkowanie openCV 
        return image;

    }

    private static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() == type) {
            return original;
        }

        // Create a buffered image
        BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);

        // Draw the image onto the new buffer
        Graphics2D g = image.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(original, 0, 0, null);
        }
        finally {
            g.dispose();
        }

        return image;
    }
    protected static double getMedian(Mat hist) {
        // binapprox algorithm

        short n = (short) hist.total();
        short[] histBuff = new short[(int) n];
        hist.get(0, 0, histBuff);
        // Compute the mean and standard deviation
        // int n = x.length;
        double sum = 0;
        // int i;
        for (int i = 0; i < n; i++) {
            sum += histBuff[i];
        }
        double mu = sum / n;

        sum = 0;
        for (int i = 0; i < n; i++) {
            sum += (histBuff[i] - mu) * (histBuff[i] - mu);
        }
        double sigma = Math.sqrt(sum / n);

        // Bin x across the interval [mu-sigma, mu+sigma]
        short bottomcount = 0;
        short[] bincounts = new short[9437186];
        for (int i = 0; i < 9437185; i++) {
            bincounts[i] = 0;
        }
        double scalefactor = 1000 / (2 * sigma);
        double leftend = mu - sigma;
        double rightend = mu + sigma;
        int bin;

        for (int i = 0; i < n; i++) {
            if (histBuff[i] < leftend) {
            bottomcount++;
            } else if (histBuff[i] < rightend) {
            bin = (short) ((histBuff[i] - leftend) * scalefactor);
            bincounts[bin]++;
            }
        }

        double median = 0;
        // If n is odd
        if ((n % 2) != 0) {
            // Find the bin that contains the median
            short k = (short) ((n + 1) / 2);
            short count = bottomcount;

            for (int i = 0; i < 9437185; i++) {
            count += bincounts[i];

            if (count >= k) {
                median = (i + 0.5) / scalefactor + leftend;
            }
            }
        }

        // If n is even
        else {
            // Find the bins that contains the medians
            short k = (short) (n / 2);
            short count = bottomcount;

            for (int i = 0; i < 9437185; i++) {
            count += bincounts[i];

            if (count >= k) {
                int j = i;
                while (count == k) {
                j++;
                count += bincounts[j];
                }
                median = (i + j + 1) / (2 * scalefactor) + leftend;
            }
            }
        }
        return median;
        }

}