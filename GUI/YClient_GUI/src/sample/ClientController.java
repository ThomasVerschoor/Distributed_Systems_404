package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ClientController extends Thread{


    @FXML
    public Button getMap;
    public Button help;
    public Button exit;

    public void initialize(){

    }

    public void getMap(ActionEvent actionEvent) throws Exception {
        YClient.command = "getMap";

    }

    public void help(ActionEvent actionEvent) throws Exception {
        YClient.command = "help";

    }

    public void exit(ActionEvent actionEvent) throws Exception {
        YClient.command ="Exit";
    }

    public static String pollString(){
        return YClient.command;
    }
}
