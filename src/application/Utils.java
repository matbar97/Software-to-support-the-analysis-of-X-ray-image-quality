package application;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a>
 * @version 1.1 (2017-03-10)
 * @since 1.0 (2016-09-17)
 * 
 */
public final class Utils {
	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		} catch (Exception e) {
			// show the exception details
			System.err.println("Cannot convert the Mat object:");
			e.printStackTrace();
			return null;
		}
	}

	public static Image mat2ImageDicom(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(DCMmatToBufferedImage(frame), null);
		} catch (Exception e) {
			// show the exception details
			System.err.println("Cannot convert the Mat object:");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property a {@link ObjectProperty}
	 * @param value    the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(() -> {
			property.set(value);
		});
	}

	public static Image convertMat2Image(Mat frame) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (frame.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = frame.channels() * frame.cols() * frame.rows();
		byte[] b = new byte[bufferSize];
		frame.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(frame.cols(), frame.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return SwingFXUtils.toFXImage(image, null);
	}

	/**
	 * Support for the {@link mat2image()} method
	 * 
	 * @param original the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 */
	private static BufferedImage matToBufferedImage(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

//		if (original.channels() > 1) {
//			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//		} else 
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	private static BufferedImage DCMmatToBufferedImage(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		short[] sourcePixels = new short[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
		}
		final short[] targetPixels = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}
	private static BufferedImage DCMmatToBufferedImage8U(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	static BufferedImage createBufferedImageFromDicomFile(File dicomFile) {
		Raster raster = null;
		System.out.println("Input: " + dicomFile.getName());

		// Open the DICOM file and get its pixel data
		try {

			Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
			ImageReader reader = (ImageReader) iter.next();
			DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
			ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);

			DicomInputStream dis = new DicomInputStream(iis);
			DicomObject dicom = dis.readDicomObject();
//			double WC = dicom.getDouble(Tag.WindowCenter);
//			double WW = dicom.getDouble(Tag.WindowWidth);
//			System.out.println("WC= " + WC);
//			System.out.println("WW= " + WW);

//			dicom.putDouble(0, VR.US, 270);

			iis.seek(0);
			reader.setInput(iis, false);
			// Returns a new Raster (rectangular array of pixels) containing the raw pixel
			// data from the image stream
			raster = reader.readRaster(0, param);
			if (raster == null)
				System.out.println("Error: couldn't read Dicom image!");
			iis.close();
		} catch (Exception e) {
			System.out.println("Error: couldn't read dicom image! " + e.getMessage());
			e.printStackTrace();
		}
		return get16bitBuffImage(raster);

	}

//
	public static BufferedImage get16bitBuffImage(Raster raster) {
		short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();

		ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] { 16 },
				false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
		DataBufferUShort db = new DataBufferUShort(pixels, pixels.length);
		WritableRaster outRaster = Raster.createInterleavedRaster(db, raster.getWidth(), raster.getHeight(),
				raster.getWidth(), 1, new int[] { 0 }, null);
		return new BufferedImage(colorModel, outRaster, colorModel.isAlphaPremultiplied(), null);

//		short[] rawShorts = new short[rawBytes.length / 2];
//
//	    ByteBuffer.wrap(rawBytes)
//	            // .order(ByteOrder.LITTLE_ENDIAN) // Depending on the data's endianness
//	            .asShortBuffer()
//	            .get(rawShorts);
//
//	    DataBuffer dataBuffer = new DataBufferUShort(rawShorts, rawShorts.length);
//	    int stride = 1;
//	    WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, w, h, w * stride, stride, new int[] {0}, null);
//	    ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
//
//	    return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
	}
	
	public static double getMedian(Mat hist) {
	    // binapprox algorithm

	    long n = hist.total();
	    int[] histBuff = new int[(int) n];
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
	    int bottomcount = 0;
	    int[] bincounts = new int[1001];
	    for (int i = 0; i < 1001; i++) {
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
	        bin = (int) ((histBuff[i] - leftend) * scalefactor);
	        bincounts[bin]++;
	        }
	    }

	    double median = 0;
	    // If n is odd
	    if ((n % 2) != 0) {
	        // Find the bin that contains the median
	        int k = (int) ((n + 1) / 2);
	        int count = bottomcount;

	        for (int i = 0; i < 1001; i++) {
	        count += bincounts[i];

	        if (count >= k) {
	            median = (i + 0.5) / scalefactor + leftend;
	        }
	        }
	    }

	    // If n is even
	    else {
	        // Find the bins that contains the medians
	        int k = (int) (n / 2);
	        int count = bottomcount;

	        for (int i = 0; i < 1001; i++) {
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

///** čŁťčĽ‰DicomćŞ”ćˇ�ĺ’Śč§Łćž�Dicom tagçš„Service. * @version 1.0.0 */
//@Service("dicomParseService")
//@Scope("prototype")
//public class DicomParseService extends BaseService implements IDicomParseService {
//	private static final String[] NATIVEFORMATSYNTAXUID = new String[] { "1.2.840.10008.1.2", "1.2.840.10008.1.2.1",
//			"1.2.840.10008.1.2.2" };
//	private DicomObject dicomObject;
//	private InputStream inputStream;
//	private DicomInputStream dicomInputStream;
//
//	/** @ loadDicomFile,č®€ĺ…ĄDicomćŞ”ćˇ�,ä¸¦ĺ°‡čł‡ć–™ćµ�ĺŻ«ĺ…ĄDicomObject. */ public void loadDicomFile(String filePath) throws IOException { try { inputStream = new FileInputStream(new File(filePath)); dicomInputStream = new DicomInputStream(inputStream); dicomObject = dicomInputStream.readDicomObject(); } catch (IOException e) { 
//		// ĺ¦‚ćžśč®€ĺ…ĄćŞ”ćˇ�ç™Ľç”źç•°ĺ¸¸ć™‚,ĺ‰‡ĺ°‡IOExceptionĺŻ«ĺ…Ąlogä¸­,č€Śä¸”ĺś¨logä¸­č¦�č¨»ć�ŽćŞ”ćˇ�çš„č·Żĺľ‘
//		filePath. log.error(e.toString()); throw e; } };
//
//	/** @ é—śé–‰ćŞ”ćˇ�ćµ�. */ public void closeDicomFile() { try { if (inputStream != null) { inputStream.close(); } if (dicomInputStream != null) { dicomInputStream.close(); } } catch (IOException e) { 
//		// é—śé–‰ćŞ”ćˇ�ĺ¤±ć•—ĺ‰‡ĺŻ«log 
//		log.error("dicomParseService close stream failed."); } } 
//	/** @ getDicomJson, čż”ĺ›žjsonć ĽĺĽŹçš„DICOMčł‡č¨Š. */ 
//	public String getDicomJson(String filePath) { StringWriter jsonWriter = new StringWriter(); 
//	try { File f = new File(filePath); 
//	DicomInputStream is = new DicomInputStream(f); 
//	JsonGenerator gen = Json.createGenerator(jsonWriter); 
//	DcmJsonWriter jsw = new DcmJsonWriter(gen); is.setHandler(jsw); 
//	is.readDicomObject(); is.close(); jsw.generatorOver(); } catch (IOException e) { log.error("in getDicomJson method, read dicom file occured IOException."); return ""; } return jsonWriter.toString(); } 
//	/** @çŤ˛ĺŹ–Stringĺž‹ĺ�Ąçš„tagĺ€Ľ. */ 
//	public String getString(int tag) { byte[] byteTagValue = dicomObject.getBytes(tag); 
//	// ĺ¦‚ćžśçŤ˛ĺŹ–çš„byteĺ€Ľç‚şç©ş,ĺ‰‡čż”ĺ›žnull;ĺ¦‚ćžśä¸Ťç‚şç©ş,ĺ‰‡ć ąć“šä¸Ťĺ�Śçš„ĺ­—ĺ…�ç·¨ç˘Ľé›†é€˛čˇŚĺ°Ťć‡‰č™•ç�†. 
//	if (null == byteTagValue) { return null; } 
//	else { SpecificCharacterSet 
//		characterSet = dicomObject.getSpecificCharacterSet(); String tagValue = null; 
//		String unicodeTagValue = null; try { if (null == characterSet) { 
//			// ĺ¦‚ćžśDCMçš„TAGĺ€Ľç·¨ç˘Ľé›†ç‚şnull,ĺ‰‡é �č¨­ĺ…�ç”¨GBKč§Łç˘Ľ,ĺ†Ťĺ°‡ĺ…¶č˝‰ĺŚ–ç‚şUTF-8. 
//			unicodeTagValue = new String(byteTagValue, "GBK"); } else 
//			{ unicodeTagValue = characterSet.decode(byteTagValue); } 
//		byte[] utfBytes = unicodeTagValue.getBytes("UTF-8"); tagValue = new String(utfBytes, "UTF-8"); 
//		} catch (UnsupportedEncodingException e) { this.log.info(e); } return tagValue; } }; 
//		/** @çŤ˛ĺŹ–String[]ĺž‹ĺ�Ąçš„tagĺ€Ľ. */ public String[] getStrings(int tag) { return dicomObject.getStrings(tag); }; /** @çŤ˛ĺŹ–Dateĺž‹ĺ�Ąçš„tagĺ€Ľ. */ public Date getDate(int tag) { return dicomObject.getDate(tag); }; /** @çŤ˛ĺŹ–Integerĺž‹ĺ�Ąçš„tagĺ€Ľ. */ public Integer getInteger(int tag) { DicomElement dcmElement = dicomObject.get(tag); if (dcmElement == null) { return null; } return Integer.valueOf(dicomObject.getInt(tag)); }; /** çŤ˛ĺŹ–ĺą€čł‡č¨Š * @return List<Frame> */ public List<Frame> getFrames(String filePath) { List<Frame> listFrames = new ArrayList<Frame>(); boolean isMultiFrame = false; try { int numberOfFrames = getNumberOfFrames(); if (numberOfFrames > 1) { isMultiFrame = true; } long[] offsetLength = new long[2]; for (int i = 0; i < numberOfFrames; i++) { offsetLength = getFrameOffsetLength(filePath, i, isMultiFrame); if (offsetLength[1] <= 0) { log.error("DicomParseService getFrames dataLen <= 0!"); break; } Frame frameObj = new Frame(); frameObj.setStartPos(offsetLength[0]); frameObj.setDataLen(offsetLength[1]); frameObj.setFrameIndex(i); listFrames.add(frameObj); } } catch (IOException e) { log.error(e); } return listFrames; } /** çŤ˛ĺŹ–ĺą€çš„čµ·ĺ§‹ä˝Ťç˝®ĺŹŠé•·ĺş¦ * @param filePath * @param frameIndex * @return long[] * @throws IOException */ private long[] getFrameOffsetLength(String filePath, int frameIndex, boolean isMultiFrame) throws IOException { long[] offsetLength = new long[2]; File fileObj = new File(filePath); DicomInputStream dcmInputStreamObj = new DicomInputStream(fileObj); dcmInputStreamObj.setHandler(new StopTagInputHandler(Tag.PixelData)); dcmInputStreamObj.readDicomObject(); String transferSyntaxUID = getString(Tag.TransferSyntaxUID).trim(); boolean isCompressFormat = originalIsCompress(transferSyntaxUID); if (!isMultiFrame &;&; !isCompressFormat) { offsetLength[0] = dcmInputStreamObj.getStreamPosition(); offsetLength[1] = dcmInputStreamObj.valueLength(); } else { offsetLength = fetchFrameOffsetAndLength(fileObj, frameIndex); } if (isCompressFormat) { boolean isLastZeroByte = isLastByteZeroValue(fileObj, offsetLength[0], offsetLength[1]); if (isLastZeroByte) { offsetLength[1] = offsetLength[1] - 1; } } dcmInputStreamObj.close(); return offsetLength; } /** ĺ�¤ć–·ĺŽźĺ§‹ĺś–ć�Żĺ�¦ć�ŻĺŁ“ç¸®çš„ * @param transferSyntaxUID * @return */ private boolean originalIsCompress(String transferSyntaxUID) { boolean flag = true; for (String syntaxUID : NATIVEFORMATSYNTAXUID) { if (syntaxUID.equals(transferSyntaxUID)) { flag = false; } } return flag; } /** çŤ˛ĺŹ–ĺą€çš„ĺ€‹ć•¸ * @return int */ private int getNumberOfFrames() { Integer nFrames = getInteger(Tag.NumberOfFrames); // tagä¸Ťĺ­�ĺś¨ć™‚,é �č¨­ç‚şĺ–®ĺą€çš„ă€‚ int numberOfFrames = 1; if (null != nFrames) { numberOfFrames = nFrames.intValue(); } return numberOfFrames; } /** ĺ¦‚ćžśćś€ĺľŚä¸€ĺ€‹ä˝Ťĺ…�çµ„ć�Ż0,ĺ‰‡é•·ĺş¦ć¸›1 * @param inputStream * @param startPos * @param dataLen * @return boolean */ private boolean isLastByteZeroValue(File fileObj, long startPos, long dataLen) { byte[] byteVal = new byte[1]; boolean result = false; try { FileInputStream inStream = new FileInputStream(fileObj); inStream.skip(startPos + dataLen - 1); inStream.read(byteVal); if (byteVal[0] == 0) { result = true; } inStream.close(); } catch (Exception e) { log.error(e); log.error("DicomParseService isLastByteZeroValue failed!"); } return result; } /** çŤ˛ĺŹ–čł‡ć–™é•·ĺş¦ * @param fileObj * @param frameIndex * @return long[] */ private long[] fetchFrameOffsetAndLength(File fileObj, int frameIndex) { long[] offsetLength = new long[2]; try { ImageInputStream imageInputStream = ImageIO.createImageInputStream(fileObj); Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM"); DicomImageReader reader = (DicomImageReader) iter.next(); reader.setInput(imageInputStream, false); offsetLength = reader.getImageInputStreamOffsetLength(frameIndex); imageInputStream.close(); } catch (IOException e) { log.error(e); log.error("DicomParseService getDataLen failed"); } return offsetLength; }} 
//		}
//	}
//	}
//	}