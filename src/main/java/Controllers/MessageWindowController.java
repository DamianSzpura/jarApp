package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MessageWindowController {


    Pane father_pane;

    public Label box_message;

    void setThisStage(String textToShowOnStage) {
        box_message.setText(textToShowOnStage);

    }

    public void box_close(ActionEvent actionEvent) {
        father_pane.setDisable(false);
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
}
