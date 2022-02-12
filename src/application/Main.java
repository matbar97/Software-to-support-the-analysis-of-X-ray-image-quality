package application;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	// the main stage
		private Stage primaryStage;
		protected BorderPane root;
		
		@Override
		public void start(Stage primaryStage)
		{
			try
			{
				// load the FXML resource
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Sample.fxml"));
				root = (BorderPane) loader.load();
				// set a background colour
				Scene scene = new Scene(root, 1700, 1000);
				
				root.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(0,0,0,0.3);");
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				// create the stage with the given title and the previously created
				// scene
				this.primaryStage = primaryStage;
				this.primaryStage.setTitle("Oprogramowanie do wspomagania analizy jakosci obrazow radiograficznych");
				this.primaryStage.setScene(scene);
				this.primaryStage.show();
				
				// init the controller
				SampleController controller = loader.getController();
				controller.setStage(this.primaryStage);
				controller.init();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public static void main(String[] args)
		{
			// load the native OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);			
			launch(args);
		}
}
