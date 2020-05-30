package sample;

import com.sun.security.ntlm.Server;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

    @FXML
    public TextField hostNameField;
    public TextField serverIPField;
    public Label hostNameLabel;
    public Label serverIPLabel;
    public static String IP;
    public static String hostName;

    public void initialize(){

    }

    public void button(ActionEvent actionEvent) throws Exception {

        System.out.println(("Home_Controller : IP and  hostname given"));
        hostName = hostNameField.getText();
        IP = serverIPField.getText();
        System.out.println("User has given Hostname: "+ hostName +" and IP: "+IP);

        if(IP.isEmpty() || hostName.isEmpty()){

            hostNameLabel.setText("Give a valid username:");
            serverIPLabel.setText("Give a valid server IP: (example 192.168.1.1)");

        }

        else {

            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/JavaFx/Client.fxml"));
            Scene newScene = new Scene(root);
            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(newScene);
            window.show();

        }




    }




    /*
    // action event
    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e)
        {
            //.setText("   button   selected    ");
        }
    };

    // when button is pressed
        button.setOnAction(event);

        */
}
