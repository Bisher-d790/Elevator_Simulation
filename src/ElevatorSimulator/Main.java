package ElevatorSimulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Read file fxml and draw interface.
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GUI.fxml"));
            Parent root = fxmlLoader.load();
            //Reference to FXML frontEnd controller
            Controller controller = fxmlLoader.getController();
            //Pointer to backEnd Class, and passing the controller reference
            Elevator elevator = new Elevator(controller);
            //Passing the Elevator class reference to the frontEnd controller
            controller.setElevator(elevator);
            //Initializing, setting and showing the stage
            primaryStage.setTitle("Elevator Simulator");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
