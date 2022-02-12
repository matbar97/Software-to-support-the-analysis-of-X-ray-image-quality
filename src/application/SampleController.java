package application;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;
//import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

//import javax.imageio.ImageReader;
//import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
//import org.dcm4che2.data.Tag;
//import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.CloseUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
//import org.jfree.data.xy.XYSeries;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Point;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
//import java.awt.Point;
//import org.opencv.core.Rect;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.highgui.HighGui;
//import org.opencv.highgui.ImageWindow;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

//import ij.IJ;
//import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.gui.PlotWindow;
//import ij.gui.Roi;
//import ij.measure.Measurements;
//import ij.plugin.RoiReader;
//import ij.plugin.filter.Analyzer;
//import ij.process.ByteProcessor;
//import ij.process.ImageConverter;
//import ij.process.ImageStatistics;
//import ij.process.ShortProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
//import javafx.collections.ObservableList;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.chart.CategoryAxis;
//import javafx.scene.chart.LineChart;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.XYChart;
//import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
//import javafx.scene.control.SplitPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.scene.image.PixelReader;
//import javafx.scene.image.PixelWriter;
//import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

//import javax.media.jai.PlanarImage;
import javax.swing.AbstractAction;
//import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
//import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.Scrollable;

//import com.sun.media.jai.codec.FileSeekableStream;
//import com.sun.media.jai.codec.ImageCodec;
//import com.sun.media.jai.codec.ImageDecoder;
//import com.sun.media.jai.codec.SeekableStream;

public class SampleController {

	/**
	 * 
	 */
	@FXML
	Button btn_loadImg, btn_toDefaultParams, windowStatistics, btnDo, scaleDown, scaleUp;

	@FXML
	CheckBox hist_equal, canny, blur, dilateErode, brightness, gauss, scale, thrAdaptive, makeRectangle, makeLine,
			hist_normalize, scale2Selection;

	@FXML
	ImageView imageView, histogramView;

	@FXML
	Slider threshold, brightnessSlider, contrastSlider, hueSlider, saturationSlider, scaleSlider, alphaVal, betaVal;

	@FXML
	ScrollPane scrollPane;

	@FXML
	AnchorPane anchorPaneImageView;

	@FXML
	TextField pixelSizeTextField, originalWidth, originalHeight, pixelSizeCmText;

	private Mat imageMat, dstMat, imageMatU8;
	private FileChooser fileChooser;
	private Stage stage;
	private List<Mat> planes;
	private File src;

	double initX, endX;
	double initY, endY;

	DicomInputStream dis;
	DicomObject dcm;

	private static final int ADJUST_TYPE_HUE = 1;
//	private static final int ADJUST_TYPE_CONTRAST = 2;
	private static final int ADJUST_TYPE_SATURATION = 3;
//	private static final int ADJUST_TYPE_BRIGHTNESS = 4;
	private ColorAdjust colorAdjust;

	double[] lineData = null;
	double starting_point_x, starting_point_y;
	Group group_for_rectangles = new Group();
	Rectangle rect;
	int color_index = 0;

	protected double deltaX;
	protected double deltaY;
	Double pixelSize;
	protected Line line;
	protected Series<Number, Number> series;// = new XYChart.Series<Number,Number>();
	Point p;
	double val;
	Plot plot;
	Plot plot2;
	Plot plot_histogram = null;
	ArrayList<Double> vals;
	ArrayList<Double> Xses;
	Alert alert;
	ArrayList<Double> linee, lineValues;
	Mat rectangle, afterScale, scale2SelectedRegion;
	ArrayList<Plot> plotList;

	protected void init() {
		this.fileChooser = new FileChooser();
		this.imageMat = new Mat();
		this.dstMat = new Mat();
		this.planes = new ArrayList<>();
		this.threshold.setShowTickLabels(true);

		this.hueSlider.setShowTickLabels(true);
		this.saturationSlider.setShowTickLabels(true);
		this.scaleSlider.setShowTickLabels(true);
		alphaVal.setShowTickLabels(true);
		betaVal.setShowTickLabels(true);

		this.createSlider(ADJUST_TYPE_HUE);
		this.createSlider(ADJUST_TYPE_SATURATION);
		this.colorAdjust = new ColorAdjust();
		this.adjustSlider(scaleSlider, 0.25, 20, 1);
		Group group = new Group(anchorPaneImageView);
		Group g = new Group(imageView);
//		anchorPaneImageView.getChildren().add(imageView);
//		anchorPaneImageView = (AnchorPane) createZoomPane(g);

		this.scrollPane.setContent(g);
		this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

	}

	private void adjustSlider(Slider slider, double min, double max, double startVal) {
		// TODO Auto-generated method stub
		slider.setMin(min);
		slider.setMax(max);
		slider.setValue(startVal);
	}

	private void createSlider(final int adjustType) {
		// TODO Auto-generated method stub

		hueSlider.setMin(-1);
		hueSlider.setMax(1);
		hueSlider.setBlockIncrement(0.1);
		hueSlider.setValue(0);
		hueSlider.valueProperty().addListener(new ChangeListener<Number>() {

			public void changed(ObservableValue<? extends Number> observable, //
					Number oldValue, Number newValue) {
				switch (adjustType) {
				case ADJUST_TYPE_HUE:
					colorAdjust.setHue(newValue.doubleValue());
					break;
				}
			}
		});
		saturationSlider.setMin(-1);
		saturationSlider.setMax(1);
		saturationSlider.setBlockIncrement(0.1);
		saturationSlider.setValue(0);
		saturationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, //
					Number oldValue, Number newValue) {
				switch (adjustType) {
				case ADJUST_TYPE_SATURATION:
					colorAdjust.setSaturation(newValue.doubleValue());
					break;
				}
			}
		});
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	@FXML
	private void loadImg() throws IOException {
		ExtensionFilter png = new ExtensionFilter("png", "*.png");
		ExtensionFilter jpg = new ExtensionFilter("jpg", "*.jpg");
		ExtensionFilter dcm = new ExtensionFilter("dcm", "*.dcm");
		ExtensionFilter tif = new ExtensionFilter("tif", "*.tif");

		if (this.fileChooser.getExtensionFilters().isEmpty())
			this.fileChooser.getExtensionFilters().addAll(dcm, tif, png, jpg);

		src = this.fileChooser.showOpenDialog(this.stage.getScene().getWindow());
		BufferedImage bufImage = null;

		if (src != null) {
			if (src.getAbsolutePath().endsWith(".dcm") || src.getAbsolutePath().endsWith(".DCM")) {
				this.adjustSlider(scaleSlider, 0.1, 10, 1);

//				bufImage = ImageIO.read(src);
				ImagePlus d = new ImagePlus(src.getAbsolutePath());
				pixelSize = d.getCalibration().pixelWidth / 10;
				Double pixHDcm = d.getCalibration().pixelHeight / 10;
				pixelSize = new BigDecimal(pixelSize).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

//				pixelSizeTextField.setText(value);

				System.out.println("pix W: " + pixelSize);
				System.out.println("pix H: " + pixHDcm);

				String pixSize = String.valueOf(pixelSize);

				pixelSizeTextField.setText(pixSize);

				DicomInputStream dis = null;
				DicomObject dicom = null;

				try {
					dis = new DicomInputStream(src);
					dicom = dis.readDicomObject();
				} catch (IOException e) {
					System.out.println("Error while reading DICOM tags");
				} finally {
					if (dis != null) {
						CloseUtils.safeClose(dis);
					}
				}
				double WC2 = dicom.getDouble(Tag.WindowCenter);
				double WW2 = dicom.getDouble(Tag.WindowWidth);
//				String pixelSizeDicom = dicom.getString(Tag.PixelSpacing/10);
//				pixelSizeTextField.setText(pixelSizeDicom);

//				String dcmImageSize = dicom.getString(Tag.AcquisitionMatrix);
//				System.out.println(dcmImageSize + " <-image size ");

//				d.run(src.getAbsolutePath());
//				System.out.println(" g³ebia = " + d.getBitDepth());

//				Double min = d.getDisplayRangeMin();
//				Double max = d.getDisplayRangeMax();

//				Double WW = (d.getDisplayRangeMax() - d.getDisplayRangeMin());
//				Double WC = (d.getDisplayRangeMax() + d.getDisplayRangeMin()) * 0.5;

//				System.out.println(" WC " + WC);
//				System.out.println(" WW " + WW);
				System.out.println(" WCDcm4 " + WC2);
				System.out.println(" WWDcm4 " + WW2);
//				System.out.println(" min " + min);
//				System.out.println(" max " + max);

				if (d.getBitDepth() > 8) {
					bufImage = Utils.createBufferedImageFromDicomFile(src);
					imageMatU8 = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
					imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);
					MinMaxLocResult mmr = Core.minMaxLoc(imageMat);
					System.out.println("max: " + mmr.maxVal + " min: " + mmr.minVal);
					int colsDicom = imageMat.cols();
					int rowsDicom = imageMat.rows();
					System.out.println(colsDicom + "X" + rowsDicom);
					int channels = imageMat.channels();
					System.out.println(channels);
					for (int i = 0; i < imageMat.rows(); i++) {
						for (int j = 0; j < imageMat.cols(); j++) {
							double[] data = imageMat.get(i, j);
							for (int k = 0; k < channels; k++) {
								if (data[k] <= WC2 - 0.5 - (WW2 - 1) / 2) {
									data[k] = 0;
								} else if (data[k] > WC2 - 0.5 + (WW2 - 1) / 2) {
									data[k] = 255;
								} else {
									data[k] = ((data[k] - (WC2 - 0.5)) / (WW2 - 1) + 0.5) * (255 - 0) + 0;
								}

//								if (data[k] < 0) {
//									data[k] = 0;
//								} else if (data[k] > 255) {
//									data[k] = 255;
//								} else {
//									data[k] = data[k];
//								}
//									System.out.println("data[k]: " + data[k]);
							}
							imageMatU8.put(i, j, data);
						}
					}
					System.out.println("16 bicików");
				} else {
					bufImage = ImageIO.read(src);
					imageMat = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
					imageMatU8 = imageMat;
					System.out.println("8 bicików");
				}
//				alert = new Alert(AlertType.INFORMATION);
//				alert.setTitle("Original size of a loaded Image: ");
//				alert.setHeaderText(imageMat.rows() + " x " + imageMat.cols());
//				alert.showAndWait();
//				alert = null;
				String originWidth = String.valueOf(imageMat.width());
				String originHeight = String.valueOf(imageMat.height());
				originalWidth.setText(originWidth);
				originalHeight.setText(originHeight);
//				if(!pixelSizeCmText.isVisible()) {
//				pixelSizeCmText.setVisible(true);
//				} else {
//					return;
//				}
//				System.out.println("16bit:" + imageMat.get(250, 250)[0]);
//				System.out.println("8bit:" + imageMatU8.get(250, 250)[0]);

				Histogram h = new Histogram();
				this.updateImageView(imageView, Utils.mat2Image(imageMatU8));
				this.showHistogram(imageMatU8);
				h.display();

				this.adjustSlider(alphaVal, 0, 10, 1);
				this.adjustSlider(betaVal, 0, 200, 1);
			}

			else if (src.getAbsolutePath().endsWith(".tif") || src.getAbsolutePath().endsWith(".tiff")) {

				ImagePlus other = new ImagePlus(src.getAbsolutePath());

				System.out.println("pixel W: " + pixelSize);
//				System.out.println("pixel H: " + pixelHOther);

//				bufImage= (BufferedImage) other.getImage();
				bufImage = ImageIO.read(src);
//			bufImage = Utils.createBufferedImgdFromDICOMfile(src);
				System.out.println(other.getFileInfo());
				imageMatU8 = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);

				if (other.getBitDepth() == 16) {
					imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);

					int channels = imageMat.channels();
					System.out.println(channels);

					Double WW = (other.getDisplayRangeMax() - other.getDisplayRangeMin());
					Double WC = (other.getDisplayRangeMax() + other.getDisplayRangeMin()) * 0.5;
					System.out.println("WW: " + WW + "	WC: " + WC);
					for (int i = 0; i < imageMat.rows(); i++) {
						for (int j = 0; j < imageMat.cols(); j++) {
							double[] data = imageMat.get(i, j);
							for (int k = 0; k < channels; k++) {
//							data[k] = data[k] / (255);
								if (data[k] <= WC - 0.5 - (WW - 1) / 2) {
									data[k] = 0;
								} else if (data[k] > WC - 0.5 + (WW - 1) / 2) {
									data[k] = 255;
								} else {
									data[k] = ((data[k] - (WC - 0.5)) / (WW - 1) + 0.5) * (255 - 0) + 0;
								}

								imageMatU8.put(i, j, data);
							}
						}
						System.out.println("16 bicików");
					}
				} else {
					imageMat = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
					System.out.println("8 bicików");
				}

				String originWidth = String.valueOf(imageMat.width());
				String originHeight = String.valueOf(imageMat.height());
				originalWidth.setText(originWidth);
				originalHeight.setText(originHeight);

				this.adjustSlider(scaleSlider, 0.1, 10, 0.25);
				pixelSize = other.getCalibration().pixelWidth * 2.54;
//				Double pixelHOther = other.getCalibration().pixelHeight * 2.54;
				pixelSize = new BigDecimal(pixelSize).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				Double pixelSizeWithScale = pixelSize / scaleSlider.getValue();
				pixelSizeWithScale = new BigDecimal(pixelSizeWithScale).setScale(4, BigDecimal.ROUND_HALF_UP)
						.doubleValue();

				String pixSize = String.valueOf(pixelSizeWithScale);
//			other.setDisplayRange(0, 65535);

				pixelSizeTextField.setText(pixSize);
//				if(!pixelSizeCmText.isVisible()) {
//					pixelSizeCmText.setVisible(true);
//					} else {
//						return;
//					}

				Histogram h = new Histogram();

//			this.imagePlus = MatImagePlusConverter.toImagePlus(imageMat);
				this.updateImageView(this.imageView, Utils.mat2Image(imageMatU8));

				this.showHistogram(imageMatU8);
//				this.displayHistogram(Utils.mat2Image(this.imageMat));
				this.adjustSlider(alphaVal, 0, 100, 1);
				this.adjustSlider(betaVal, 0, 200, 1);
				h.display();
			}

			else {
				ImagePlus other = new ImagePlus(src.getAbsolutePath());
				pixelSize = other.getCalibration().pixelWidth * 2.54;
				Double pixelHOther = other.getCalibration().pixelHeight * 2.54;
				pixelSize = new BigDecimal(pixelSize).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

				String pixSize = String.valueOf(pixelSize);
//			other.setDisplayRange(0, 65535);

				pixelSizeTextField.setText(pixSize);

				System.out.println("pixel W: " + pixelSize);
				System.out.println("pixel H: " + pixelHOther);

//				bufImage= (BufferedImage) other.getImage();
				bufImage = ImageIO.read(src);
//			bufImage = Utils.createBufferedImgdFromDICOMfile(src);
				System.out.println(other.getFileInfo());
				imageMatU8 = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
//			MinMaxLocResult mmr = Core.minMaxLoc(imageMat);

				if (other.getBitDepth() > 8) {
					imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);

					int channels = imageMat.channels();
					System.out.println(channels);

					Double WW = (other.getDisplayRangeMax() - other.getDisplayRangeMin());
					Double WC = (other.getDisplayRangeMax() + other.getDisplayRangeMin()) * 0.5;
					System.out.println("WW: " + WW + "	WC: " + WC);
					for (int i = 0; i < imageMat.rows(); i++) {
						for (int j = 0; j < imageMat.cols(); j++) {
							double[] data = imageMat.get(i, j);
							for (int k = 0; k < channels; k++) {
//							data[k] = data[k] / (255);
								if (data[k] <= WC - 0.5 - (WW - 1) / 2) {
									data[k] = 0;
								} else if (data[k] > WC - 0.5 + (WW - 1) / 2) {
									data[k] = 255;
								} else {
									data[k] = ((data[k] - (WC - 0.5)) / (WW - 1) + 0.5) * (255 - 0) + 0;
								}

//							if (data[k] < 0) {
//								data[k] = 0;
//							} else if (data[k] > 255) {
//								data[k] = 255;
//							} else {
//								data[k] = data[k];
//							}
//								System.out.println("data[k]: " + data[k]);
							}
							imageMatU8.put(i, j, data);
						}
					}
					System.out.println("16 bicików");
				} else {
					imageMat = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
					System.out.println("8 bicików");
				}
//				double maxx=mmlr.maxVal;
//				if(mmlr.maxVal > 255) {
//					imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);
//					System.out.println("maxVal" + mmlr.maxVal);
//				} else {
//					imageMat = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
//					System.out.println("maxVal" + mmlr.maxVal);
//				}

//				imageMat = Imgcodecs.imread(src.getCanonicalPath());
//			System.out.println("Wartosc piksela to: " + imageMat.get(10, 30)[0]);
//				bufImage=Utils.matToBufferedImage(imageMat);
//				bufImage=OpenCvUtils.toBufferedImageOfType(bufImage, BufferedImage.TYPE_USHORT_GRAY);
//			alert = new Alert(AlertType.INFORMATION);
//			alert.setTitle("Original size of a loaded Image: ");
//			alert.setHeaderText(imageMat.rows() + " x " + imageMat.cols());
//			
//			alert.showAndWait();
//			alert = null;
				String originWidth = String.valueOf(imageMat.width());
				String originHeight = String.valueOf(imageMat.height());
				originalWidth.setText(originWidth);
				originalHeight.setText(originHeight);
//				if(pixelSizeCmText.isVisible()) {
//					pixelSizeCmText.setVisible(true);
//					} else {
//						return;
//					}
				Histogram h = new Histogram();
//				scaleSlider.setValue(0.25);
//			this.imagePlus = MatImagePlusConverter.toImagePlus(imageMat);
				this.updateImageView(this.imageView, Utils.mat2Image(imageMatU8));

				this.showHistogram(imageMatU8);
//				this.displayHistogram(Utils.mat2Image(this.imageMat));
				this.adjustSlider(alphaVal, 0, 100, 1);
				this.adjustSlider(betaVal, 0, 200, 1);
				h.display();

			}

			this.imageView.setFitWidth(imageMat.cols());
			this.imageView.setFitHeight(imageMat.rows());

			imageView.setCache(true);
			imageView.setSmooth(true);
			this.scrollPane.setPrefSize(1072, 912);
//			this.scrollPane.relocate(100, 30);

			this.imageView.setEffect(colorAdjust);
			this.imageView.scaleXProperty().bindBidirectional(scaleSlider.valueProperty());
			this.imageView.scaleYProperty().bindBidirectional(scaleSlider.valueProperty());
			this.imageView.getTransforms().add(new Scale(1, 1, 0, 0));
//			this.imageView.setPreserveRatio(true);
//			this.anchorPaneImageView.setPrefHeight(this.imageView.getFitHeight());
//			this.anchorPaneImageView.setPrefWidth(this.imageView.getFitWidth());

//			anchorPaneImageView.scaleXProperty().bindBidirectional(scaleSlider.valueProperty());
//			anchorPaneImageView.scaleYProperty().bindBidirectional(scaleSlider.valueProperty());
//			anchorPaneImageView.getChildren().get(0).scaleXProperty().bindBidirectional(scaleSlider.valueProperty());
//			anchorPaneImageView.getChildren().get(0).scaleYProperty().bindBidirectional(scaleSlider.valueProperty());

//			imageView.translateXProperty().bindBidirectional(scaleSlider.valueProperty());
//			imageView.translateYProperty().bindBidirectional(scaleSlider.valueProperty());

			btn_toDefaultParams.setDisable(false);
			hist_equal.setDisable(false);
			canny.setDisable(false);
			blur.setDisable(false);
			dilateErode.setDisable(false);
			brightness.setDisable(false);
			gauss.setDisable(false);
			makeRectangle.setDisable(false);
			makeLine.setDisable(false);
			scaleSlider.setDisable(false);
			alphaVal.setDisable(false);
			betaVal.setDisable(false);
			hueSlider.setDisable(false);
			saturationSlider.setDisable(false);

//			// empty the image planes and the image views if it is not the first
//			// loaded image
//			if (!this.planes.isEmpty()) {
//				this.planes.clear();
//				 this.transformedImage.setImage(null);
//				 this.antitransformedImage.setImage(null);

		}
	}

	@FXML
	private void gaussianBlur() {
		if (this.gauss.isSelected()) {

			dstMat = doGaussianBlur(imageMatU8);
			updateImageView(imageView, Utils.mat2Image(dstMat));

		} else {
			System.out.println("powrot do normalnego");

			this.imageView.setImage(Utils.mat2Image(imageMatU8));
		}
	}

	private Parent createZoomPane(final Group group) {
		final double SCALE_DELTA = 1.1;
//		final StackPane zoomPane = new StackPane();

		anchorPaneImageView.getChildren().add(group);
		anchorPaneImageView.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				event.consume();

				if (event.getDeltaY() == 0) {
					return;
				}

				double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

				group.setScaleX(group.getScaleX() * scaleFactor);
				group.setScaleY(group.getScaleY() * scaleFactor);
				doScaleImage(imageMat, scaleFactor);
			}
		});

		anchorPaneImageView.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds bounds) {
				anchorPaneImageView.setClip(
						new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
			}
		});

		return anchorPaneImageView;
	}

	@FXML
	private void brightnessEnhancement() {

		if (this.brightness.isSelected()) {

			dstMat = doBrightnessEnhancement(imageMatU8, alphaVal.getValue(), betaVal.getValue());
			updateImageView(imageView, Utils.mat2Image(dstMat));
		} else {
			System.out.println("powrot do normalnego");
			this.imageView.setImage(Utils.mat2Image(imageMatU8));
			// this.imageView.setImage(Utils.mat2Image(imageMat));
			showHistogram(this.imageMat);
//			System.out.println("imageMat: " + imageMatU8.get(3, 2));

		}
	}

	@FXML
	private void scale2RectangleSelection() {
		if (scale2Selection.isSelected()) {
			rect = new Rectangle();
			rect.setStroke(Color.PINK);
			rect.setFill(null);

			scrollPane.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mE) {
					if (mE.getEventType() == MouseEvent.MOUSE_PRESSED && scale2Selection.isSelected()
							&& !makeLine.isSelected() && mE.getX() <= imageView.getFitWidth()
							&& mE.getY() <= imageView.getFitHeight() && mE.getX() >= 0 && mE.getY() >= 0) {
						rectangle = null;
						alert = null;
						afterScale = null;
						initX = mE.getX();
						initY = mE.getY();
						mE.consume();
						rect.setTranslateX(initX);
						rect.setTranslateY(initY);
						scale2SelectedRegion = null;
					}

					if (mE.getEventType() == MouseEvent.MOUSE_DRAGGED && scale2Selection.isSelected()
							&& !makeLine.isSelected() && mE.getX() <= scrollPane.getWidth() - 16
							&& mE.getY() <= scrollPane.getHeight() && mE.getX() >= 0 && mE.getY() >= 0) {

						rect.setWidth(mE.getX() - rect.getTranslateX());
						rect.setHeight(mE.getY() - rect.getTranslateY());

					}
					if (mE.getEventType() == MouseEvent.MOUSE_RELEASED && scale2Selection.isSelected()
							&& !makeLine.isSelected()) {
//						if (afterScale == null) {
//							afterScale = new Mat(imageMat.rows(), imageMat.cols(), imageMat.type());
//
//							afterScale = doScaleImage(imageMat, scaleSlider.getValue());
//							System.out.println("After scale:" + afterScale + " val: " + afterScale.get(100, 300)[0]);
//							System.out.println("before scale:" + imageMat + " val: " + imageMat.get(100, 300)[0]);
//						} else {
//							return;
//						}
						rect.setVisible(true);

//						System.out.println("Start of Rect: " + initX + " " + initY);
//						System.out
//								.println("Width of Rect: " + rect.getWidth() + " Height of Rect: " + rect.getHeight());
						endX = rect.getWidth() + initX;
						endY = rect.getHeight() + initY;
//						System.out.println("End of Rect: " + endX + " " + endY);
						Size size = new Size(rect.getWidth(), rect.getHeight());
//
						if (scale2SelectedRegion == null) {
							scale2SelectedRegion = new Mat(size, imageMatU8.type());

							scale2SelectedRegion = imageMatU8.submat((int) initX, (int) endX, (int) initY, (int) endY);
							updateImageView(imageView, Utils.mat2Image(scale2SelectedRegion));
						}
//							MatOfDouble mean = new MatOfDouble();
//							MatOfDouble stddev = new MatOfDouble();
//							MatOfDouble globalMean = new MatOfDouble();
//							MatOfDouble globalStddev = new MatOfDouble();
//
////							Scalar mean_val = Core.mean(rectangle);
//
//							MinMaxLocResult mmr = Core.minMaxLoc(rectangle);
//							double min = mmr.minVal;
//							double max = mmr.maxVal;
//
//							Core.meanStdDev(rectangle, mean, stddev);
//							double meanVal = mean.get(0, 0)[0];
//							double stdVal = stddev.get(0, 0)[0];
//							double SNR = meanVal / stdVal;
//
//							Core.meanStdDev(imageMat, globalMean, globalStddev);
//							double global_Mean = globalMean.get(0, 0)[0];
////							Scalar globalMean= Core.meanStdDev(imageMat);
//							String result;
//							if (global_Mean < meanVal) {
//								result = new String("Region is considered as homogeneous").toUpperCase();
//							} else {
//								result = new String("Region is not considered as homogeneous").toUpperCase();
//							}
//
//							if (alert == null) {
//								alert = new Alert(AlertType.INFORMATION);
//								alert.setTitle("Statistics calculation complete!");
//								alert.setHeaderText("The Values are: ");
//								alert.setContentText("Global mean value: " + global_Mean + "\n" + "Mean value of ROI: "
//										+ meanVal + "\n" + result + "\n" + "Standard Deviation value: " + stdVal + "\n"
//										+ "Max value: " + max + "\n" + "Min value: " + min + "\n" + "SNR: " + SNR);
//								alert.showAndWait();
//							} else {
//								return;
//							}
//						} else {
//							return;
//						}
					}
				}
			});
//			updateImageView(imageView, Utils.mat2Image(rectangle));
			this.anchorPaneImageView.getChildren().add(rect);
//			imageView.setViewport(new Rectangle2D(0, 0, rectangle.width(), rectangle.height()));
		} else if (!scale2Selection.isSelected()) {
//			makeLine.setDisable(false);
			this.rect.setVisible(false);
			this.anchorPaneImageView.getChildren().remove(rect);
		}
	}

	@FXML
	private void setToDefaultParameters() {

		hueSlider.setValue(0);
		saturationSlider.setValue(0);
		scaleSlider.setValue(1);
		alphaVal.setValue(1);
		betaVal.setValue(1);
		pixelSizeTextField.setText(pixelSize.toString());
		System.out.print("imageView: " + imageView.getImage().getHeight() + " x " + imageView.getImage().getWidth());
		System.out.print("imageView Fit: " + imageView.getFitHeight() + " x " + imageView.getFitWidth());
		System.out.print("imageMatrix: " + imageMat.rows() + " x " + imageMat.cols());
		System.out.print("imageMatrix height width: " + imageMat.height() + " x " + imageMat.width());
		System.out.print("imageMatrix8U height width: " + imageMatU8.height() + " x " + imageMatU8.width());

	}

	@FXML
	private void equHistogram() {
		if (this.hist_equal.isSelected()) {
			dstMat = doHistEqualization(imageMatU8);
			updateImageView(imageView, Utils.mat2Image(dstMat));
		} else {
			this.imageView.setImage(Utils.mat2Image(imageMatU8));
			showHistogram(this.imageMat);
		}
	}

//	@FXML
//	private void normalizeHistogram() {
//		if (this.hist_normalize.isSelected()) {
//			doHistogramNormalization(imageMat, 4);
////			if (plot_histogram == null) {
////				plot_histogram = new Plot("Histogram", "Range", "Pixel Values");
////				short[] tab = matToArray(imageMat);
//////				plot_histogram.addHistogram(tab);
////				plot_histogram.draw();
////				plot_histogram.show();
//
////			else {
////				return;
////			}
//		} else {
////			this.imageView.setImage(Utils.mat2ImageDicom(imageMat));
//			this.imageView.setImage(Utils.mat2Image(imageMatU8));
//			showHistogram(this.imageMat);
//		}
//
//	}

	@FXML
	private void scaleImage() {

		dstMat = doScaleImage(dstMat, scaleSlider.getValue());
//		Double temp=scaleSlider.getValue();
//		pixelSize = Double.valueOf(pixelSizeTextField.getText());

		Double pixSize = pixelSize / scaleSlider.getValue();
		pixSize = new BigDecimal(pixSize).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

		String pix_Size = String.valueOf(pixSize);
		pixelSizeTextField.setText(pix_Size);
		updateImageView(imageView, Utils.mat2Image(dstMat));
	}

	private void showHistogram(Mat imageMat) {
		List<Mat> bgrPlanes = new ArrayList<>();
		Core.split(imageMat, bgrPlanes);
		MinMaxLocResult mmr = Core.minMaxLoc(imageMat);
//		double min = mmr.minVal;
		double max = mmr.maxVal;
		int histSize = (int) max;
		float[] range = { 0, histSize }; // the upper boundary is exclusive
		MatOfFloat histRange = new MatOfFloat(range);
		boolean accumulate = false;
		Mat bHist = new Mat();// gHist = new Mat(), rHist = new Mat();

		Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
//        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
//        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
		int histW = 512, histH = 400;
		int binW = (int) Math.round((double) histW / histSize);
		Mat histImage = new Mat(histH, histW, CvType.CV_8U, new Scalar(0, 0, 0));
		Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
		// Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
		// Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
		float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
		bHist.get(0, 0, bHistData);
		// float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
		// gHist.get(0, 0, gHistData);
		// float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
		// rHist.get(0, 0, rHistData);
		for (int i = 1; i < histSize; i++) {
			Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
					new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(max - 1, 0, 0), 2);
//            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
//                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
//            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
//                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
		}
//		List<Mat> images = new ArrayList<Mat>();
//		Core.split(imageMat, images);
//		// set the number of bins at 256
//		MatOfInt histSize = new MatOfInt(256);
//		// only one channel
//		MatOfInt channels = new MatOfInt(0);
//		// set the ranges
//		MatOfFloat histRange = new MatOfFloat(0, 256);
//
//		// compute the histograms for the B, G and R components
//		Mat hist_b = new Mat();
//		Mat hist_g = new Mat();
//		Mat hist_r = new Mat();
//
//		Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);
//
//		// G and R components (if the image is not in gray scale)
//
//		//Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
//		//Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
//
//		// draw the histogram
//		int hist_w = 255; // width of the histogram image
//		int hist_h = 255; // height of the histogram image
//		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);
//
//		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
//		// normalize the result to [0, histImage.rows()]
//		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
//		Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
//		Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
//
//		// effectively draw the histogram(s)
//		for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
//			// B component or gray image
//			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
//					new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
////			// G and R components (if the image is not in gray scale)
////
////			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
////					new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8, 0);
////			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
////					new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8, 0);
//		}
//
		// display the histogram...
//		double[] histogram = new double[histImage.height() * histImage.width()];
////		ArrayList<Double> histogram = new ArrayList<>();
//		int channels = histImage.channels();

//		for (int i = 0; i < histImage.cols(); i++) {
//			for (int j = 0; j < histImage.rows(); j++) {
//				for (int k = 0; k < channels; k++)
//					histogram[k] = imageMat.get(i, j)[0];
//				plot_histogram.addHistogram(histogram);
//			}
//		}
		Image histImg = Utils.mat2Image(histImage);
		this.updateImageView(this.histogramView, histImg);

	}

	@FXML
	private void dilateErodeSelected() {
		if (this.dilateErode.isSelected()) {
			dstMat = doBackgroundRemoval(imageMatU8);
			updateImageView(imageView, Utils.mat2Image(dstMat));

		} else {
			this.imageView.setImage(Utils.mat2Image(imageMatU8));
		}
	}

	@FXML
	private void blurSelected() {
		if (this.blur.isSelected()) {
			dstMat = doBlur(imageMatU8);

			updateImageView(imageView, Utils.mat2Image(dstMat));
		}

		else if (this.blur.isSelected() || this.gauss.isSelected()) {
			dstMat = doBlur(imageMatU8);
			dstMat = doGaussianBlur(dstMat);
			updateImageView(imageView, Utils.mat2Image(dstMat));

		} else {
			this.imageView.setImage(Utils.mat2Image(imageMatU8));
		}
	}

	int i;

	@FXML
	private void doUpScale() {
//		dstMat = imageMatU8;
//		dstMat = Imgproc.resize(dstMat, dstMat, new Size(5, 5));
		int i = 1;
		dstMat = scale_Up(imageMatU8);
		if (scaleUp.isArmed()) {
			i = 2 * i;
		}
		imageView.setScaleX(i);
		imageView.setScaleY(i);
		updateImageView(imageView, Utils.mat2Image(dstMat));
	}

	private Mat scale_Up(Mat mat) {
		dstMat = mat;
//		dstMat = new Mat(src.height(), src.width(), src.type());
		Imgproc.resize(dstMat, dstMat, new Size(dstMat.width() * 2, dstMat.height() * 2), 0, 0, Imgproc.INTER_CUBIC);

		return dstMat;
	}

	@FXML
	void doDownScale() {
//		dstMat = imageMatU8;
		dstMat = scale_Down(imageMatU8);
		imageView.setScaleX(0.5);
		imageView.setScaleY(0.5);
		updateImageView(imageView, Utils.mat2Image(dstMat));
	}

	private Mat scale_Down(Mat mat) {
		dstMat = mat;
//		dstMat = new Mat(src.height(), src.width(), src.type());
		Imgproc.resize(dstMat, dstMat, new Size(dstMat.width() * 0.5, dstMat.height() * 0.5), 0, 0,
				Imgproc.INTER_CUBIC);

		return dstMat;
	}

	@FXML
	private void cannySelected() {
		// enable the threshold slider

		if (this.canny.isSelected()) {
			this.threshold.setDisable(false);
			dstMat = doCanny(imageMatU8);
			updateImageView(imageView, Utils.mat2Image(dstMat));

		} else
			this.threshold.setDisable(true);
		this.imageView.setImage(Utils.mat2Image(imageMatU8));

	}

	@FXML
	private void drawRect() {
		if (makeRectangle.isSelected()) {
			makeLine.setDisable(true);
			rect = new Rectangle();
			rect.setStroke(Color.PINK);
			rect.setFill(null);
//			pixelSizeTextField.setEditable(true);
			pixelSizeTextField.setDisable(false);
//			TextFormatter textFormatter = new TextFormatter<>(new DoubleStringConverter());
			anchorPaneImageView.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mE) {
					if (mE.getEventType() == MouseEvent.MOUSE_PRESSED && makeRectangle.isSelected()
							&& !makeLine.isSelected() && mE.getX() <= scrollPane.getWidth()
							&& mE.getY() <= scrollPane.getHeight() && mE.getX() >= 0 && mE.getY() >= 0) {
						rectangle = null;
						alert = null;
						afterScale = null;
						initX = mE.getX();
						initY = mE.getY();
						mE.consume();
						rect.setTranslateX(initX);
						rect.setTranslateY(initY);
						if (afterScale == null) {
							afterScale = new Mat(imageMat.rows(), imageMat.cols(), imageMat.type());

							afterScale = doScaleImage(imageMat, scaleSlider.getValue());
							System.out.println("After scale:" + afterScale + " val: " + afterScale.get(100, 300)[0]);
							System.out.println("before scale:" + imageMat + " val: " + imageMat.get(100, 300)[0]);
						} else {
							return;
						}
					}

					if (mE.getEventType() == MouseEvent.MOUSE_DRAGGED && makeRectangle.isSelected()
							&& !makeLine.isSelected() && mE.getX() <= scrollPane.getWidth() - 16
							&& mE.getY() <= scrollPane.getHeight() && mE.getX() >= 0 && mE.getY() >= 0) {

						rect.setWidth(mE.getX() - rect.getTranslateX());
						rect.setHeight(mE.getY() - rect.getTranslateY());

					}
					if (mE.getEventType() == MouseEvent.MOUSE_RELEASED && makeRectangle.isSelected()
							&& !makeLine.isSelected()) {

						rect.setVisible(true);

						System.out.println("Start of Rect: " + initX + " " + initY);
						System.out
								.println("Width of Rect: " + rect.getWidth() + " Height of Rect: " + rect.getHeight());
						endX = rect.getWidth() + initX;
						endY = rect.getHeight() + initY;
						System.out.println("End of Rect: " + endX + " " + endY);
						Size size = new Size(rect.getWidth(), rect.getHeight());
						double ROI_size = rect.computeAreaInScreen();

						if (rectangle == null) {
							rectangle = new Mat(size, afterScale.type());

							rectangle = afterScale.submat((int) initY, (int) endY, (int) initX, (int) endX);
							MatOfDouble mean = new MatOfDouble();
							MatOfDouble stddev = new MatOfDouble();
							MatOfDouble globalMean = new MatOfDouble();
							MatOfDouble globalStddev = new MatOfDouble();

							MinMaxLocResult mmr = Core.minMaxLoc(rectangle);
							double min = mmr.minVal;
							double max = mmr.maxVal;

							Core.meanStdDev(rectangle, mean, stddev);
							double meanVal = mean.get(0, 0)[0];
							meanVal = new BigDecimal(meanVal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

							double stdVal = stddev.get(0, 0)[0];
							stdVal = new BigDecimal(stdVal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

							Core.meanStdDev(imageMat, globalMean, globalStddev);
							double global_Mean = globalMean.get(0, 0)[0];
							global_Mean = new BigDecimal(global_Mean).setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							String result;
							String upToNCharacters;

							if (!pixelSizeTextField.getText().isEmpty()
									|| pixelSizeTextField.getText().matches("[^0-9\\.]")) {

//								if(pixelSizeTextField.getText().length() >= 9) {
//								upToNCharacters = String.format("%.6s", pixelSizeTextField.getText());
//								}
//								else if(pixelSizeTextField.getText().length() == 8) {
//									upToNCharacters = String.format("%.5s", pixelSizeTextField.getText());
//								}
//								else if(pixelSizeTextField.getText().length() == 7) {
//									upToNCharacters = String.format("%.4s", pixelSizeTextField.getText());
//								}
//								else if(pixelSizeTextField.getText().length() == 6) {
//									upToNCharacters = String.format("%.3s", pixelSizeTextField.getText());
//								}
//								else {
//									upToNCharacters = String.format("%.2s", pixelSizeTextField.getText());
//
//								}
//								ROI_size = ROI_size * Double.parseDouble(pixelSizeTextField.getText())
//										* Double.parseDouble(pixelSizeTextField.getText());
								ROI_size = ROI_size * Double.parseDouble(pixelSizeTextField.getText())
										* Double.parseDouble(pixelSizeTextField.getText());
								ROI_size = new BigDecimal(ROI_size).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							} else {
								alert = new Alert(AlertType.ERROR);
								alert.setTitle("Error!");
								alert.setHeaderText(
										"Set the proper Value of Pixel. Pixel Value can be only a numeric.");
								alert.showAndWait();
								alert = null;
								ROI_size = 0;
							}

							if (global_Mean < meanVal) {
								result = new String("Region is considered as homogeneous").toUpperCase();
							} else {
								result = new String("Region is not considered as homogeneous").toUpperCase();
							}
							Double SNR = meanVal / stdVal;
							if (SNR.isNaN()) {
								SNR = 0.0;
							} else {
								SNR = new BigDecimal(SNR).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							}
							if (alert == null) {
								alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Statistics calculation complete!");
								alert.setHeaderText("The Values are: ");
								alert.setContentText("Global mean value: " + global_Mean + "\n" + "Mean value of ROI: "
										+ meanVal + "\n" + result + "\n" + "Standard Deviation value: " + stdVal + "\n"
										+ "Max value: " + max + "\n" + "Min value: " + min + "\n" + "SNR: " + SNR + "\n"
										+ "Area: " + ROI_size + " cm2");
								alert.showAndWait();
							} else {
								return;
							}
						} else {
							return;
						}
					}
				}
			});
//			updateImageView(imageView, Utils.mat2Image(rectangle));
			this.anchorPaneImageView.getChildren().add(rect);
//			imageView.setViewport(new Rectangle2D(0, 0, rectangle.width(), rectangle.height()));
		} else if (!makeRectangle.isSelected()) {
			makeLine.setDisable(false);
			this.rect.setVisible(false);
			this.anchorPaneImageView.getChildren().remove(rect);
			pixelSizeTextField.setDisable(true);
		}
	}

	@FXML
	private <T> void drawLine() {
		if (makeLine.isSelected() && !makeRectangle.isSelected()) {
			line = new Line();
			line.setStroke(Color.PINK);
			plotList = null;
			makeRectangle.setDisable(true);
			pixelSizeTextField.setDisable(false);

			anchorPaneImageView.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
				@SuppressWarnings("deprecation")
				@Override
				public void handle(MouseEvent mouseEvent) {
					if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && makeLine.isSelected()) {
						plot = null;
						plot2 = null;
						vals = new ArrayList<Double>();
						Xses = new ArrayList<Double>();

						alert = null;
						afterScale = null;

						initX = mouseEvent.getX();
						initY = mouseEvent.getY();

						linee = new ArrayList<Double>();
						lineValues = new ArrayList<Double>();
						mouseEvent.consume();

						line.setTranslateX(initX);
						line.setTranslateY(initY);
					}
					if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && makeLine.isSelected()
							&& mouseEvent.getX() <= scrollPane.getWidth() && mouseEvent.getY() <= scrollPane.getHeight()
							&& mouseEvent.getX() >= 0 && mouseEvent.getY() >= 0) {
						deltaX = mouseEvent.getX();
						deltaY = mouseEvent.getY();
						mouseEvent.consume();

						line.setEndX(deltaX - line.getTranslateX());
						line.setEndY(deltaY - line.getTranslateY());
					}
					if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED && makeLine.isSelected()) {
						if (afterScale == null) {
							afterScale = new Mat(imageMat.rows(), imageMat.cols(), imageMat.type());
							afterScale = imageMat;

							afterScale = doScaleImage(imageMat, scaleSlider.getValue());
							System.out.println("After scale:" + afterScale + " val: " + afterScale.get(100, 300)[0]);
							System.out.println("before scale:" + imageMat + " val: " + imageMat.get(100, 300)[0]);
						} else {
							return;
						}
						endX = line.getEndX() + line.getTranslateX();
						endY = line.getEndY() + line.getTranslateY();
						double lastX = mouseEvent.getX();
						double lastY = mouseEvent.getY();
						System.out.println("End(X,Y): " + afterScale.get((int) lastX, (int) lastY)[0]);

						System.out.println("getX: , getY: " + lastX + " " + lastY);
						System.out.println("lineXend: , lineYend: " + endX + " " + endY);

						line.setVisible(true);
						double len = 0;

						if (scale2SelectedRegion != null) {
							linee = BresenhamLine((int) initX, (int) initY, (int) lastX, (int) lastY);
//						lineValues = pixelsValues((int) initX, (int) initY, (int) lastX, (int) lastY, afterScale);
							lineValues = BresenhamLineValues((int) initX, (int) initY, (int) lastX, (int) lastY,
									scale2SelectedRegion);
						} else {
							linee = BresenhamLine((int) initX, (int) initY, (int) lastX, (int) lastY);
//							lineValues = pixelsValues((int) initX, (int) initY, (int) lastX, (int) lastY, afterScale);
							lineValues = BresenhamLineValues((int) initX, (int) initY, (int) lastX, (int) lastY,
									afterScale);
						}

						if (plot == null) {
							plot = new Plot("Profiler", "Range", "Pixel Values");
							plot.addPoints(linee, lineValues, PlotWindow.LINE);
							plot.draw(); // Comment this line and then the red line is displayed.

							len = Plot.calculateDistance((int) initX, (int) initY, (int) endX, (int) endY);

							plot.show();
							if (!pixelSizeTextField.getText().isEmpty()
									|| pixelSizeTextField.getText().matches("[^0-9\\.]")) {
								String upToNCharacters = String.format("%.6s", pixelSizeTextField.getText());

								len = len * Double.parseDouble(upToNCharacters);
								len = new BigDecimal(len).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

							} else {
								alert = new Alert(AlertType.ERROR);
								alert.setTitle("Error!");
								alert.setHeaderText(
										"Set the proper Value of Pixel. Pixel Value can be only a numeric.");
								alert.showAndWait();
								alert = null;
								len = 0;
							}
//							len = len * Double.parseDouble(pixelSizeTextField.getText());
							if (alert == null) {
								alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Line properties");
								alert.setHeaderText("Line characteristics: ");
								alert.setContentText("X Start Value: " + initX + "\n" + "X End Values: " + endX + "\n"
										+ "Y Start Value: " + initY + "\n" + "Y End Value: " + endY + "\n"
										+ "Line length (cm): " + len);
								alert.showAndWait();
							} else {
								return;
							}
						}
					}
				}
			});

			this.anchorPaneImageView.getChildren().add(line);

		} else if (!makeLine.isSelected()) {
			makeRectangle.setDisable(false);
			this.anchorPaneImageView.getChildren().remove(line);
			line.setVisible(false);
			pixelSizeTextField.setDisable(true);

		}
	}

//	private ArrayList<Double> drawLine(int x1, int y1, int x2, int y2) {
//		int dx = Math.abs(x2 - x1);
//		int dy = Math.abs(y2 - y1);
//		int delta = dx - dy;
//		List<Point> pixels = new ArrayList<Point>();
//		int move_x, move_y;
//		if ((x1 == x2) && (y1 == y2)) {
//			pixels.add(new Point(x1, y1));
//		} else {
//			if (x1 < x2)
//				move_x = 1;
//			else
//				move_x = -1;
//			if (y1 < y2)
//				move_y = 1;
//			else
//				move_y = -1;
//
//			while ((x1 != x2) || (y1 != y2)) {
//
//				int p = 2 * delta;
//
//				if (p > -dy) {
//					delta = delta - dy;
//					x1 = x1 + move_x;
//				}
//				if (p < dx) {
//					delta = delta + dx;
//					y1 = y1 + move_y;
//				}
//				pixels.add(new Point(x1, y1));
//			}
//		}
//		double[] pixelTab = new double[pixels.size()];
//		ArrayList<Double> pixell = new ArrayList<Double>();
//
//		for (int i = 0; i < pixels.size(); i++) {
//			pixelTab[i] = i;
//			pixell.add((double) i);
//		}
//
//		return pixell;
//	}

	// x1 , y1 - wspó³rzêdne pocz¹tku odcinka
	// x2 , y2 - wspó³rzêdne koñca odcinka
	private ArrayList<Double> BresenhamLine(int x1, int y1, int x2, int y2) {
		// zmienne pomocnicze
		int d, dx, dy, ai, bi, xi, yi;
		int x = x1, y = y1;
		ArrayList<Point> pixels = new ArrayList<Point>();

		// ustalenie kierunku rysowania
		if (x1 < x2) {
			xi = 1;
			dx = x2 - x1;
		} else {
			xi = -1;
			dx = x1 - x2;
		}
		// ustalenie kierunku rysowania
		if (y1 < y2) {
			yi = 1;
			dy = y2 - y1;
		} else {
			yi = -1;
			dy = y1 - y2;
		}
		// pierwszy piksel
		pixels.add(new Point(x, y));
		// oœ wiod¹ca OX
		if (dx > dy) {
			ai = (dy - dx) * 2;
			bi = dy * 2;
			d = bi - dx;
			// pêtla po kolejnych x
			while (x != x2) {
				// test wspó³czynnika
				if (d >= 0) {
					x += xi;
					y += yi;
					d += ai;
				} else {
					d += bi;
					x += xi;
				}
				pixels.add(new Point(x, y));
			}
		}
		// oœ wiod¹ca OY
		else {
			ai = (dx - dy) * 2;
			bi = dx * 2;
			d = bi - dy;
			// pêtla po kolejnych y
			while (y != y2) {
				// test wspó³czynnika
				if (d >= 0) {
					x += xi;
					y += yi;
					d += ai;
				} else {
					d += bi;
					y += yi;
				}
				pixels.add(new Point(x, y));
			}
		}
		double[] pixelTab = new double[pixels.size()];
		ArrayList<Double> pixell = new ArrayList<Double>();

		for (int i = 0; i < pixels.size(); i++) {
			pixelTab[i] = i;
			pixell.add((double) i);
		}

		return pixell;
	}

	private ArrayList<Double> BresenhamLineValues(int x1, int y1, int x2, int y2, Mat imageMat) {
		// zmienne pomocnicze
		int d, dx, dy, ai, bi, xi, yi;
		int x = x1, y = y1;
		List<Double> pixels = new ArrayList<Double>();

		// ustalenie kierunku rysowania
		if (x1 < x2) {
			xi = 1;
			dx = x2 - x1;
		} else {
			xi = -1;
			dx = x1 - x2;
		}
		// ustalenie kierunku rysowania
		if (y1 < y2) {
			yi = 1;
			dy = y2 - y1;
		} else {
			yi = -1;
			dy = y1 - y2;
		}
		// pierwszy piksel
		pixels.add(imageMat.get(y, x)[0]);
		// oœ wiod¹ca OX
		if (dx > dy) {
			ai = (dy - dx) * 2;
			bi = dy * 2;
			d = bi - dx;
			// pêtla po kolejnych x
			while (x != x2) {
				// test wspó³czynnika
				if (d >= 0) {
					x += xi;
					y += yi;
					d += ai;
				} else {
					d += bi;
					x += xi;
				}
				pixels.add(imageMat.get(y, x)[0]);
			}
		}
		// oœ wiod¹ca OY
		else {
			ai = (dx - dy) * 2;
			bi = dx * 2;
			d = bi - dy;
			// pêtla po kolejnych y
			while (y != y2) {
				// test wspó³czynnika
				if (d >= 0) {
					x += xi;
					y += yi;
					d += ai;
				} else {
					d += bi;
					y += yi;
				}
				pixels.add(imageMat.get(y, x)[0]);
			}
		}

		return (ArrayList<Double>) pixels;
	}

//	private ArrayList<Double> pixelsValues(int x1, int y1, int x2, int y2, Mat imageMat) {
//		int dx = Math.abs(x2 - x1);
//		int dy = Math.abs(y2 - y1);
//		int delta = dx - dy;
//		List<Double> pixels = new ArrayList<Double>();
//		int move_x, move_y;
//		if ((x1 == x2) && (y1 == y2)) {
//			pixels.add(imageMat.get(y1, x1)[0]);
//		} else {
//			if (x1 < x2)
//				move_x = 1;
//			else
//				move_x = -1;
//			if (y1 < y2)
//				move_y = 1;
//			else
//				move_y = -1;
//
//			while ((x1 != x2) || (y1 != y2)) {
//
//				int p = 2 * delta;
//
//				if (p > -dy) {
//					delta = delta - dy;
//					x1 = x1 + move_x;
//				}
//				if (p < dx) {
//					delta = delta + dx;
//					y1 = y1 + move_y;
//				}
//				pixels.add(imageMat.get(y1, x1)[0]);
//			}
//		}
//		return (ArrayList<Double>) pixels;
//	}

	private Mat doBackgroundRemoval(Mat frame) {
		// init
		Mat hsvImg = new Mat();
		List<Mat> hsvPlanes = new ArrayList<>();
		Mat thresholdImg = new Mat();

		// int thresh_type = Imgproc.THRESH_BINARY_INV;
		int thresh_type = Imgproc.THRESH_BINARY;
		/*
		 * if (this.inverse.isSelected()) thresh_type = Imgproc.THRESH_BINARY;
		 */
		// threshold the image with the average hue value
		hsvImg.create(frame.size(), CvType.CV_8U);
		Core.split(frame, hsvPlanes);

		// get the average hue value of the image
		double threshValue = this.getHistAverage(frame, hsvPlanes.get(0));

		Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

		Imgproc.blur(thresholdImg, thresholdImg, new Size(21, 21));

		// dilate to fill gaps, erode to smooth edges
		Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
		Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);

		Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

		// create the new image
		Mat foreground = new Mat(frame.size(), CvType.CV_8U, new Scalar(255, 255, 255));
		frame.copyTo(foreground, thresholdImg);

		showHistogram(foreground);
		return foreground;

	}

	private Mat doBlur(Mat frame) {
		Mat src = frame;
		Mat dest = new Mat();
		Imgproc.blur(src, dest, new Size(11, 11), new Point(0, 0)/* , Core.BORDER_DEFAULT */);
		Mat dst = new Mat();
		dest.copyTo(dst);
		return dst;
	}

	private Mat doCanny(Mat frame) {
		Mat grayImage = frame;
		Mat detectedEdges = new Mat();
		// convert to grayscale
		// reduce noise with a 3x3 kernel
		Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
		// canny detector, with ratio of lower:upper threshold of 3:1
		Imgproc.Canny(detectedEdges, detectedEdges, this.threshold.getValue(), this.threshold.getValue() * 3);
		// using Canny's output as a mask, display the result
		Mat dest = new Mat();
		grayImage.copyTo(dest, detectedEdges);
		return dest;
	}

	private Mat doBrightnessEnhancement(Mat frame, double alpha, double beta) {
		alpha = alphaVal.getValue();
		beta = betaVal.getValue();
		Mat src = frame;
		Mat destination = new Mat(src.rows(), src.cols(), src.type());

		// applying contrast and brightness enhancement
		src.convertTo(destination, -1, alpha, beta);
		Image brightImage = Utils.mat2Image(destination);

		updateImageView(imageView, brightImage);
		showHistogram(destination);
		return destination;
	}

	private Mat doGaussianBlur(Mat frame) {
		Mat src = frame;
		Mat dst = new Mat(src.rows(), src.cols(), src.type());

		// filtering
		Imgproc.GaussianBlur(src, dst, new Size(0, 0), 10);
		Core.addWeighted(src, 1.5, dst, -0.5, 0, dst);

		return dst;
	}

	private Mat doScaleImage(Mat frame, double size) {
		Mat src = frame;
		Mat dst = new Mat(src.height(), src.width(), src.type());
		Imgproc.resize(src, dst, new Size(src.width() * size, src.height() * size), 0, 0, Imgproc.INTER_CUBIC);
//		System.out.println(imageView.getViewport() + "<- viewPort");

		System.out.println("udalo sie");
		return dst;
	}

	private Mat doHistEqualization(Mat imageMat) {
		Mat src = imageMat;
		Mat dst = new Mat();

		Imgproc.equalizeHist(src, dst);

		showHistogram(dst);
		return dst;
	}

//	private void doHistogramNormalization(Mat imageMat, double percent) {
//
//		if (percent <= 0)
//			percent = 5;
//		List<Mat> channels = new ArrayList<>();
//		int rows = imageMat.rows(); // number of rows of image
//		int cols = imageMat.cols(); // number of columns of image
//		int chnls = imageMat.channels(); // number of channels of image
//		double halfPercent = percent / 200.0;
//		if (chnls == 3)
//			Core.split(imageMat, channels);
//		else
//			channels.add(imageMat);
//		List<Mat> results = new ArrayList<>();
//		for (int i = 0; i < chnls; i++) {
//			// find the low and high precentile values (based on the input percentile)
//			Mat flat = new Mat();
//			channels.get(i).reshape(1, 1).copyTo(flat);
//			Core.sort(flat, flat, Core.SORT_ASCENDING);
//			double lowVal = flat.get(0, (int) Math.floor(flat.cols() * halfPercent))[0];
//			double topVal = flat.get(0, (int) Math.ceil(flat.cols() * (1.0 - halfPercent)))[0];
//			// saturate below the low percentile and above the high percentile
//			Mat channel = channels.get(i);
//			for (int m = 0; m < rows; m++) {
//				for (int n = 0; n < cols; n++) {
//					if (channel.get(m, n)[0] < lowVal)
//						channel.put(m, n, lowVal);
//					if (channel.get(m, n)[0] > topVal)
//						channel.put(m, n, topVal);
//				}
//			}
//			Core.normalize(channel, channel, 0.0, 65536.0 / 2, Core.NORM_MINMAX);
//			channel.convertTo(channel, CvType.CV_16U);
//			results.add(channel);
//		}
//		Mat dest = new Mat();
//		Core.merge(results, dest);
//		Image normImage = Utils.mat2ImageDicom(dest);
//		updateImageView(imageView, normImage);
//		showHistogram(dest);
//
//	}

	private double getHistAverage(Mat hsvImg, Mat hueValues) {
		// init
		double average = 0.0;
		Mat hist_hue = new Mat();
		// 0-180: range of Hue values
		MatOfInt histSize = new MatOfInt(180);
		List<Mat> hue = new ArrayList<>();
		hue.add(hueValues);

		// compute the histogram
		Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

		// get the average Hue value of the image
		// (sum(bin(h)*h))/(image-height*image-width)
		// -----------------
		// equivalent to get the hue of each pixel in the image, add them, and
		// divide for the image size (height and width)
		for (int h = 0; h < 180; h++) {
			// for each bin, get its value and multiply it for the corresponding
			// hue
			average += (hist_hue.get(h, 0)[0] * h);
		}

		// return the average hue of the image
		return average = average / hsvImg.size().height / hsvImg.size().width;
	}

	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	class Histogram {
		BufferedImage image = getImage();
		HistogramDataset dataset;
		XYBarRenderer renderer;
		Mat imageMat = OpenCvUtils.bufferedImage2Mat_v2(getImage());
		MinMaxLocResult mmr = Core.minMaxLoc(imageMat);
		int BINS = (int) mmr.maxVal;

		private BufferedImage getImage() {
			try {
				if (BINS > 256) {
					ImagePlus ip = new ImagePlus(src.getAbsolutePath());
					return ip.getBufferedImage();

				}
				return ImageIO.read(src);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			return null;
		}

//		if(getImage().getType()==BufferedImage.TYPE_USHORT_GRAY)
//		{

//		}else
//		{
//			Mat imageMat = OpenCvUtils.bufferedImage2Mat_8bit(getImage());
//		}

		private ChartPanel createChartPanel() {
			// dataset
			dataset = new HistogramDataset();
//			Raster raster = image.getRaster();

			int w = imageMat.cols();
			int h = imageMat.rows();
			List<Double> matVals = new ArrayList<Double>();
			double mat2D[][] = new double[h][w];
			for (int x = 0; x < imageMat.rows(); ++x) {
				for (int y = 0; y < imageMat.cols(); ++y) {
					mat2D[x][y] = imageMat.get(x, y)[0];
					matVals.add(mat2D[x][y]);
				}
			}

			double[] vector = new double[matVals.size()];
			for (int i = 0; i < vector.length; i++) {
				vector[i] = matVals.get(i);
			}

			dataset.addSeries("Gray", vector, BINS);

			// chart
			JFreeChart chart = ChartFactory.createHistogram("Histogram", "Value", "Count", dataset,
					PlotOrientation.VERTICAL, true, true, false);
			XYPlot plot = (XYPlot) chart.getPlot();
			renderer = (XYBarRenderer) plot.getRenderer();
			renderer.setBarPainter(new StandardXYBarPainter());

			plot.setDrawingSupplier(new DefaultDrawingSupplier());
			ChartPanel panel = new ChartPanel(chart);
			panel.setMouseWheelEnabled(true);
			return panel;
		}

		private JPanel createControlPanel() {
			JPanel panel = new JPanel();
			panel.add(new JCheckBox(new VisibleAction(0)));
			return panel;
		}

		@SuppressWarnings("serial")
		private class VisibleAction extends AbstractAction {

			private final int i;

			public VisibleAction(int i) {
				this.i = i;
				this.putValue(NAME, (String) dataset.getSeriesKey(i));
				this.putValue(SELECTED_KEY, true);
				renderer.setSeriesVisible(i, true);
			}

			public void actionPerformed(ActionEvent e) {
				renderer.setSeriesVisible(i, !renderer.getSeriesVisible(i));
			}
		}

		private void display() {
			JFrame f = new JFrame("Histogram");
			f.add(createChartPanel());
			f.add(createControlPanel(), BorderLayout.SOUTH);
			f.pack();
			f.setLocationRelativeTo(null);
			f.setVisible(true);
		}
	}
}

//tiff read
//else if (src.getAbsolutePath().endsWith(".tif") || src.getAbsolutePath().endsWith(".tiff")) {
//SeekableStream stream = new FileSeekableStream(src);
//String[] names = ImageCodec.getDecoderNames(stream);
//ImageDecoder decoder = ImageCodec.createImageDecoder(names[0], stream, null);
//
//for (int i = 0; i < decoder.getNumPages(); i++) {
//	RenderedImage im = decoder.decodeAsRenderedImage(i);
//
//	bufImage = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
//}
//this.imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);
//this.imageMatU8 = OpenCvUtils.bufferedImage2Mat_8bit(bufImage);
//
//int channels = imageMat.channels();
//System.out.println(channels);
//for (int i = 0; i < imageMat.rows(); i++) {
//	for (int j = 0; j < imageMat.cols(); j++) {
////		dstMat = (imageMat *(313.899617716846 + 0.5*545.478183495912))/(255/545.478183495912);
//		double[] data = imageMat.get(i, j);
//		for (int k = 0; k < channels; k++) {
//			data[k] = data[k];// -(313.899617716846 +
//								// 0.5*545.478183495912)/(255/545.478183495912);
////			if (data[k] < 0) {
////				data[k] = 0;
////			} else if (data[k] > 255) {
////				data[k] = 255;
////			}
////			else {
////				data[k] = data[k]*4;
////			}
////			System.out.println("data[k]: " + data[k]);
//		}
//		imageMat.put(i, j, data);
//	}
//}
////this.updateImageView(this.imageView, Utils.mat2ImageDicom(this.imageMat));
////Histogram h = new Histogram();
////h.display();
//this.updateImageView(this.imageView, Utils.mat2Image(this.imageMatU8));
//this.showHistogram(imageMat);
//this.adjustSlider(alphaVal, 0, 10, 1);
//this.adjustSlider(betaVal, 0, 200, 1);

//pixel_values = new CategoryAxis();
//number_of_pixels = new NumberAxis();
//Histogram_Chart = new LineChart<String, Number>(pixel_values, number_of_pixels);
//Histogram_Chart.setCreateSymbols(false);
//Histogram_Chart.getData().clear();
//ImageHistogram imageHistogram = new ImageHistogram(Utils.mat2ImageDicom(imageMat));
//if(imageHistogram.isSuccess()){
//    Histogram_Chart.getData().addAll(
//        imageHistogram.getSeriesAlpha(),
//        imageHistogram.getSeriesRed(),
//        imageHistogram.getSeriesGreen(),
//        imageHistogram.getSeriesBlue());
//}
//}

//System.out.println()

//d.getWindow();
//d.setIJMenuBar(true);
//d.createNewRoi(0, 100);
////d.show();
//d.draw();
//d.getStatistics();
//
//System.out.println(d.getStatistics());

//// Image dicom = SwingFXUtils.toFXImage(d.getBufferedImage(), null);

//bufImage = Utils.createBufferedImgdFromDICOMfile(src);
//this.imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);
// this.imageMat = OpenCvUtils.image2Mat(dicom);
//WritableImage wi = new WritableImage(bufImage.getWidth(), bufImage.getHeight());
//WritableImage wi2 = null;
//Histogram_Chart.getData().clear();
//btnDo.setOnMouseClicked(ActionEvent Event.ANY) -> {
//Histogram_Chart.setLegendVisible(true);
//Histogram_Chart.setVisible(true);
//
//ImageHistogram imageHistogram = new ImageHistogram(imageMatU8);
//if (imageHistogram.isSuccess()) {
//	Histogram_Chart.getData().add(
//        imageHistogram.getSeriesAlpha(),
//        imageHistogram.getSeriesRed(),
//        imageHistogram.getSeriesGreen(),
//			imageHistogram.getSeriesGray());
//
//	System.out.println("brawo" + imageHistogram.getSeriesGray());
//} else {
//	System.out.println("nie utworze histogramu");
//	;
//}

//// function for line generation
//public static ArrayList<Double> bresenham(int initX, int initY, int endX, int endY) {
//	int dy = 2 * (endY - initY);
//	int slope_error_new = dy - (endX - initX);
//	List<Point> pixels = new ArrayList<Point>();
////	if (initX < endX) {
//	for (int x = initX, y = initY; x <= endX; x++) {
//		System.out.print("(" + x + "," + y + ")\n");
//		pixels.add(new Point(x, y));
//		// Add slope to increment angle formed
//		slope_error_new += dy;
//		// Slope error reached limit, time to
//		// increment y and update slope error.
//		if (slope_error_new >= 0) {
//			y++;
//			slope_error_new -= 2 * (endX - initX);
//		}
//	}
////	} 
////	else if (initX > endX) {
////		for (int x = initX, y = initY; x >= endX; x--) {
////			System.out.print("(" + x + "," + y + ")\n");
////			slope_error_new -= m_new;
////			pixels.add(new Point(x, y));
////
////			if (slope_error_new >= 0) {
////				y--;
////				slope_error_new += 2 * (endX - initX);
////			}
////		}
////	}

//	double[] pixelTab = new double[pixels.size()];
//	ArrayList<Double> pixell = new ArrayList<Double>();
//
//	for (int i = 0; i < pixels.size(); i++) {
//		pixelTab[i] = i;
//		pixell.add((double) i);
//	}
//	System.out.println(pixell.size());
//	return pixell;
//}

//class ImageHistogram {
//
////        private Image image;
//	private Mat imageMat;
//
////        private long alpha[] = new long[256];
////        private long red[] = new long[256];
////        private long green[] = new long[256];
////        private long blue[] = new long[256];
//	private long gray[] = new long[256];
//
////        XYChart.Series seriesAlpha;
////        XYChart.Series seriesRed;
////        XYChart.Series seriesGreen;
////        XYChart.Series seriesBlue;
//	XYChart.Series seriesGray;
//
//	private boolean success;
//
//	ImageHistogram(Mat src) {
//		imageMat = src;
//		success = false;
//
//		// init
////            for (int i = 0; i < 256; i++) {
//////                alpha[i] = red[i] = green[i] = blue[i] = 0;
////                gray[i] = 0;
////            }
//// 
////            PixelReader pixelReader = image.getPixelReader();
////            if (pixelReader == null) {
////                return;
////            }
//
//		// count pixels
////            for (int y = 0; y < image.getHeight(); y++) {
////                for (int x = 0; x < image.getWidth(); x++) {
////                    int argb = pixelReader.getArgb(x, y);
////                    int a = (0xff & (argb >> 24));
////                    int r = (0xff & (argb >> 16));
////                    int g = (0xff & (argb >> 8));
////                    int b = (0xff & argb);
//// 
////                    alpha[a]++;
////                    red[r]++;
////                    green[g]++;
////                    blue[b]++;
//// 
////                }
////            }
//
//		for (int i = 0; i < imageMat.rows(); i++) {
//			for (int j = 0; j < imageMat.cols(); j++) {
//				double value = imageMat.get(i, j)[0];
//
//				gray[(int) value]++;
//			}
//		}
//
////            seriesAlpha = new XYChart.Series();
////            seriesRed = new XYChart.Series();
////            seriesGreen = new XYChart.Series();
////            seriesBlue = new XYChart.Series();
//		seriesGray = new XYChart.Series();
////            seriesAlpha.setName("alpha");
////            seriesRed.setName("red");
////            seriesGreen.setName("green");
////            seriesBlue.setName("blue");
//		seriesGray.setName("gray");
//
//		// copy alpha[], red[], green[], blue[]
//		// to seriesAlpha, seriesRed, seriesGreen, seriesBlue
//		for (int i = 0; i < 256; i++) {
////                seriesAlpha.getData().add(new XYChart.Data(String.valueOf(i), alpha[i]));
////                seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
////                seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
////                seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
//			seriesGray.getData().add(new XYChart.Data<String, Long>(String.valueOf(i), gray[i]));
//		}
//
//		success = true;
//	}
//
//	public boolean isSuccess() {
//		// TODO Auto-generated method stub
//		return success;
//	}
//
////		public XYChart.Series getSeriesAlpha() {
////            return seriesAlpha;
////        }
//// 
////        public XYChart.Series getSeriesRed() {
////            return seriesRed;
////        }
//// 
////        public XYChart.Series getSeriesGreen() {
////            return seriesGreen;
////        }
//// 
////        public XYChart.Series getSeriesBlue() {
////            return seriesBlue;
////        }
//	public Series<String, Number> getSeriesGray() {
//		return seriesGray;
//	}
//
//}

//Histogram_Chart.getData().clear();

//ImageHistogram imageHistogram = new ImageHistogram(imageMat);
//if (imageHistogram.isSuccess()) {
//	Histogram_Chart.getData().addAll(
////        imageHistogram.getSeriesAlpha(),
////        imageHistogram.getSeriesRed(),
////        imageHistogram.getSeriesGreen(),
//			imageHistogram.getSeriesGray());
//}

//(src,
// dst,
// new
//imageView.setViewport(
//new Rectangle2D(dst.get(0,0)[0], dst.get(0,0)[0], dst.width(), dst.height())); // Size(scaleSlider.getValue(),
//imageView.setViewport(new Rectangle2D(0, 0, scrollPane.getWidth(), scrollPane.getHeight()));
//class Display16BitImage extends JFrame {
//public Display16BitImage() {
//	super("Using HTML in JLabels");
//	Container content = getContentPane();
//	String filename = src.getAbsolutePath();
//	JLabel imageLabel = new JLabel();
//	BufferedImage bi = new BufferedImage(ImageIO.read(src));
//	if (bi != null)
//		imageLabel.setIcon(new ImageIcon(bi));
//	else
//		imageLabel.setText("not a 12- or 16-bits image");
//	content.add(imageLabel, BorderLayout.CENTER);
//	pack();
//	setVisible(true);
//}
//
//}

//public static Line bresenham2(int x1, int y1, int x2, int y2) {
//	int m_new = 2 * (y2 - y1);
//	int slope_error_new = m_new - (x2 - x1);
//
//	for (int x = x1, y = y1; x >= x2; x--) {
//		System.out.print("(" + x + "," + y + ")\n");
//		slope_error_new -= m_new;
//		if (slope_error_new >= 0) {
//			y--;
//			slope_error_new += 2 * (x2 - x1);
//		}
//	}
//	return new Line((double) x1, (double) y1, (double) x2, (double) y2);
//}
//@FXML
//private void openProfiler() {
////	profiler.setOnMouseClicked((event) -> {
////		try {
////			FXMLLoader fxmlLoader = new FXMLLoader();
////			fxmlLoader.setLocation(getClass().getResource("ProfilerWindow.fxml"));
////			/*
////			 * if "fx:controller" is not set in fxml
////			 * fxmlLoader.setController(NewWindowController);
////			 */
////			Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////			Stage stage = new Stage();
////			stage.setTitle("Profiler");
////			stage.setScene(scene);
////			stage.show();
////
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////	});
//}

//this.imageView.setViewport(
//		new Rectangle2D(0, 0, scrollPane.getWidth(), scrollPane.getWidth()));

//pixel_values = new CategoryAxis();
//number_of_pixels = new NumberAxis();
//Histogram_Chart = new LineChart<String, Number>(pixel_values, number_of_pixels);
//pixel_values.setLabel("Bins 0-255");
//number_of_pixels.setLabel("Counts");
//Histogram_Chart.setCreateSymbols(true);
//Histogram_Chart.setTitle("Histogram");

//final CategoryAxis range = new CategoryAxis();
//final NumberAxis pixels = new NumberAxis();
//series = new XYChart.Series<Number, Number>();
//pixelsProfiler = new LineChart<Number,Number>(range,pixels);
//pixelsProfiler.setTitle("Profiler");
//range.setLabel("Range");
//pixels.setLabel("Number of pixels");
//series.setName("Line");

//dcm
//HighGui.imshow("Macierz", imageMatU8);
//HighGui.waitKey(1);

//imageMatU8 = new Mat(imageMat.height(), imageMat.width(), CvType.CV_8U);
//		OpenCvUtils.bufferedImage2Mat_8bit(imageToAnalyse);

//Analyzer anal = new Analyzer(d);
//anal.displayResults();
//ShortProcessor sp = new ShortProcessor(bufImage);
//sp.convertToShort(true);
//bufImage = d.getBufferedImage();
//System.out.println(sp.getHistogram());
//bufImage = d.getBufferedImage();
//bufImage.createGraphics();

//imageMat = OpenCvUtils.bufferedImage2Mat_v2(bufImage);

//bufImage = d.getBufferedImage();
//bufImage = new BufferedImage(d.getBufferedImage().getWidth(), d.getBufferedImage().getHeight(),
//		BufferedImage.TYPE_USHORT_GRAY);

//ImageConverter ic = new ImageConverter(d);
//ic.convertToGray8();
//d.show();

//dcm windowing
//dstMat = imageMat;

////this.imagePlus = MatImagePlusConverter.toImagePlus(this.imageMat);
//
//}

//imageMat=OpenCvUtils.bufferedImage2Mat_v2(bufImage);

//int channels = imageMat.channels();
//System.out.println(channels);
//for (int i = 0; i < imageMat.rows(); i++) {
//for (int j = 0; j < imageMat.cols(); j++) {
////	dstMat = (imageMat *(313.899617716846 + 0.5*545.478183495912))/(255/545.478183495912);
//	double[] data = imageMat.get(i, j);
//	for (int k = 0; k < channels; k++) {
//		data[k] = data[k];
////		data[k] = data[k]-(WC + 0.5*WW)/(255/WW);
////		if (data[k] < WC - WW/2) {
////			data[k] = WC - WW/2;
////		} else if (data[k] > WC + WW/2) {
////			data[k] = WC - WW/2;
////		}
////		data[k] = data[k];
////		else {
////			data[k] = data[k]*4;					
////
////		}
////		System.out.println("data[k]: " + data[k]);
//	}
//	imageMat.put(i, j, data);
//}
//}

//int w = imageMat.cols();
//int h = imageMat.rows();
//List<Double> matVals = new ArrayList<Double>();
//double mat2D[][] = new double[h][w];
//for (int x = 0; x < imageMat.rows(); ++x) {
//	for (int y = 0; y < imageMat.cols(); ++y) {
//		mat2D[x][y] = imageMat.get(x, y)[0];
//		matVals.add(mat2D[x][y]);
//	}
//}
//
//double[] vector = new double[matVals.size()];
//for (int i = 0; i < vector.length; i++) {
//	vector[i] = matVals.get(i);
//}
//
//double middle = vector[vector.length/2];