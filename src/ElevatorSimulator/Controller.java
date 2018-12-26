package ElevatorSimulator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


public class Controller implements Initializable {


    @FXML
    private ImageView arrowUpImg;
    @FXML
    private ImageView arrowDownImg;
    @FXML
    private Text operationText;
    @FXML
    private TextField sourceInput;
    @FXML
    private TextField destInput;
    @FXML
    private TextField nOfPeopleInput;
    @FXML
    private Button elevateBtn;
    @FXML
    private Text capacityText;
    @FXML
    private Text timeText;
    @FXML
    private Text levelText;
    @FXML
    private Button reportBtn;
    @FXML
    private Circle requestStatus;

    private int time;
    private Elevator elevator;

    public void initialize(URL location, ResourceBundle resources) {
        //Initializing local time for the counter
        time = 0;
    }

    public void setLevelText(int floor) {
        //Primary Level counter scale
        levelText.setText(floor + "");
    }

    public void setArrowImg(int direction) {
        //Sets the arrow indicators green and grey according to direction
        if (direction > 0) {
            arrowUpImg.setImage(new Image("/Images/ArrowGreen.png"));
            arrowDownImg.setImage(new Image("/Images/ArrowGrey.png"));
        } else if (direction == 0) {
            arrowUpImg.setImage(new Image("/Images/ArrowGrey.png"));
            arrowDownImg.setImage(new Image("/Images/ArrowGrey.png"));
        } else if (direction < 0) {
            arrowUpImg.setImage(new Image("/Images/ArrowGrey.png"));
            arrowDownImg.setImage(new Image("/Images/ArrowGreen.png"));
        }
    }

    public void setRequestStatus(int status) {
        if (status == 0) {
            requestStatus.setFill(Paint.valueOf("#e4e4e4"));
            return;
        }
        Thread changePaint = new Thread(() -> {
            try {
                if (status > 0) requestStatus.setFill(Paint.valueOf("#0be700"));
                if (status < 0) requestStatus.setFill(Paint.valueOf("#e50000"));
                Thread.sleep(1000);
                requestStatus.setFill(Paint.valueOf("#e4e4e4"));
            } catch (InterruptedException e) {
            }
        });
        changePaint.start();
    }

    public void setOperationText(String text) {
        operationText.setText(text);
    }

    public void setCapacityText(int capacity) {
        capacityText.setText(capacity + "");
    }

    public int getSource() {
        if (sourceInput.getText().isEmpty()) return elevator.getCurrentFloor();
        return Integer.parseInt(sourceInput.getText());
    }

    public int getDest() {
        if (destInput.getText().isEmpty()) return 0;
        return Integer.parseInt(destInput.getText());
    }

    public int getNofPeople() {
        if (nOfPeopleInput.getText().isEmpty()) return 1;
        return Integer.parseInt(nOfPeopleInput.getText());
    }

    public void elevateBtn(ActionEvent event) {
        if (!destInput.getText().isEmpty()) elevator.addRequest(getSource(), getDest(), getNofPeople());
        clear();
    }

    public void clear() {
        sourceInput.clear();
        destInput.clear();
        nOfPeopleInput.clear();
    }

    public void generateReportBtn(ActionEvent event) {
        elevator.GenerateReport();
    }

    public int getTime() {
        return time;
    }

    public void startTimer() {
        //Creating a timer thread which updates every 1000 ms
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeText.setText((++time) + "");
            }
        }, 1500, 1000);
    }

    public void setElevator(Elevator elevator) {
        this.elevator = elevator;
    }
}
