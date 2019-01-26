package Controllers;

import jarHandling.JarClass;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javassist.*;

import java.util.Arrays;

public class ChangeJarItemsWindowController {


    Pane primary_main_pane;

    public Pane main_pane;
    public Label box_title;

    public Label item_visibilityTypeLabel;
    public ChoiceBox<String> item_visibilityType;
    public TextField item_type;
    public TextField item_name;

    public Label item_parametersLabel;
    public TextField item_parameters;
    public TextArea item_body;

    private String typeOfAdding;

    void setThisStage(String typeOfAdding) {
        this.typeOfAdding = typeOfAdding;
        item_visibilityType.setDisable(false);
        item_type.setDisable(false);
        item_name.setDisable(false);
        item_parameters.setDisable(false);
        item_body.setDisable(false);

        item_visibilityType.getItems().setAll("public", "private", "protected");
        switch(typeOfAdding) {
            case "Add method":{
                box_title.setText("Adding method to " + HandleJarFileWindowController.selectedClass.getCtClass().getName());
                break;
            }
            case "Add field":{
                box_title.setText("Adding field to " + HandleJarFileWindowController.selectedClass.getCtClass().getName());
                item_parameters.setDisable(true);
                break;
            }
            case "Add constructor":{
                box_title.setText("Adding constructor to " + HandleJarFileWindowController.selectedClass.getCtClass().getName());
                item_visibilityType.getItems().add("Make default constructor");
                item_type.setDisable(true);
                break;
            }
            case "Add class":{
                box_title.setText("Adding class");
                item_visibilityType.setDisable(true);
                item_type.setDisable(true);
                item_parameters.setDisable(false);
                item_parametersLabel.setText("Implements");
                item_body.setDisable(true);
                break;
            }
            case "Add interface":{
                box_title.setText("Adding interface");
                item_visibilityType.setDisable(true);
                item_type.setDisable(true);
                item_parameters.setDisable(true);
                item_body.setDisable(true);
                break;
            }
            case "Add package": {
                box_title.setText("Adding package");
                item_visibilityType.setDisable(true);
                item_type.setDisable(true);
                item_parameters.setDisable(true);
                item_body.setDisable(true);
                break;
            }
            case "Editing method": {
                box_title.setText("Editing method");
                CtMethod selectedMethod = HandleJarFileWindowController.selectedClassItem.getClassMethod();
                item_visibilityTypeLabel.setText("Editing type");
                item_visibilityType.getItems().setAll("Add before method body", "Add after method body");
                item_type.setDisable(true);
                try {
                    item_type.setText(selectedMethod.getReturnType().getName());
                } catch (NotFoundException e) {
                    item_type.setText("void");
                }
                item_name.setDisable(true);
                item_name.setText(selectedMethod.getName());
                item_parameters.setDisable(true);
                try {
                    item_parameters.setText(Arrays.toString(selectedMethod.getParameterTypes()));
                } catch (NotFoundException e) {
                    item_parameters.setText("void");
                }
                break;
            }
        }
        item_visibilityType.getSelectionModel().selectFirst();
    }

    public void box_saveAndExit(ActionEvent actionEvent){

            switch (typeOfAdding) {
                case "Add method": {
                    try {
                        String addThis = item_visibilityType.getValue() + " " + item_type.getText() + " " + item_name.getText() + " (" + item_parameters.getText() + ") {" +item_body.getText() + "}";
                        HandleJarFileWindowController.selectedClass.getCtClass().addMethod(CtNewMethod.make(addThis, HandleJarFileWindowController.selectedClass.getCtClass()));
                    } catch (CannotCompileException exception) {
                        HandleJarFileWindowController.thisMainController.showMessage("Cannot compile method. Please check if everything is correct and try again. " + exception.getCause(),main_pane);
                    }
                    break;
                }
                case "Add field": {
                    try {
                        String addThis = item_visibilityType.getValue() + " " + item_type.getText() + " " + item_name.getText() + " " + item_body.getText() + ";";
                        HandleJarFileWindowController.selectedClass.getCtClass().addField(CtField.make(addThis, HandleJarFileWindowController.selectedClass.getCtClass()));
                    } catch (CannotCompileException exception) {
                        HandleJarFileWindowController.thisMainController.showMessage("Cannot compile field. Please check if everything is correct and try again. " + exception.getCause(),main_pane);
                    }
                    break;
                }
                case "Add constructor": {
                    try {
                        if(item_visibilityType.getValue() == "Make default constructor") {
                            HandleJarFileWindowController.selectedClass.getCtClass().addConstructor(CtNewConstructor.defaultConstructor(HandleJarFileWindowController.selectedClass.getCtClass()));
                        }
                        else {
                            String addThis = item_visibilityType.getValue() + " " + item_name.getText() + " (" + item_parameters.getText() + ") {" + item_body.getText() + "}";
                            HandleJarFileWindowController.selectedClass.getCtClass().addConstructor(CtNewConstructor.make(addThis, HandleJarFileWindowController.selectedClass.getCtClass()));
                        }
                    } catch (CannotCompileException exception) {
                        HandleJarFileWindowController.thisMainController.showMessage("Cannot compile constructor. Please check if everything is correct and try again. " + exception.getCause(),main_pane);
                    }
                    break;
                }
                case "Add class": {

                    boolean foundCopy = false;
                    JarClass newClassToAdd = new JarClass(HandleJarFileWindowController.jarExplorer.classPool.makeClass(item_name.getText()));

                    if(item_parameters.getText() != "")
                    {
                        try {
                            newClassToAdd.getCtClass().addInterface(HandleJarFileWindowController.jarExplorer.classPool.get(item_parameters.getText()));
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    for(JarClass item : HandleJarFileWindowController.thisMainController.newCreatedClassItems){
                        if(item.toString().equals(newClassToAdd.toString())){
                            foundCopy = true;
                            HandleJarFileWindowController.thisMainController.showMessage("That class is already here, please pick another name and try again. ",main_pane);
                            break;
                        }
                    }
                    if(!foundCopy) {
                        HandleJarFileWindowController.thisMainController.classItems.getItems().add(newClassToAdd);
                        HandleJarFileWindowController.thisMainController.newCreatedClassItems.add(newClassToAdd);
                    }
                    break;
                }
                case "Add interface": {
                    boolean foundCopy = false;
                    JarClass newInterfaceToAdd = new JarClass(HandleJarFileWindowController.jarExplorer.classPool.makeInterface(item_name.getText()));
                    for(JarClass item : HandleJarFileWindowController.thisMainController.newCreatedClassItems){
                        if(item.toString().equals(newInterfaceToAdd.toString())){
                            foundCopy = true;
                            HandleJarFileWindowController.thisMainController.showMessage("That interface is already here, please pick another name and try again. ",main_pane);
                            break;
                        }
                    }
                    if(!foundCopy) {
                        HandleJarFileWindowController.thisMainController.classItems.getItems().add(newInterfaceToAdd);
                        HandleJarFileWindowController.thisMainController.newCreatedClassItems.add(newInterfaceToAdd);
                    }
                    break;
                }
                case "Add package": {
                    HandleJarFileWindowController.jarExplorer.classPool.importPackage(item_name.getText());
                    break;
                }
                case "Editing method": {
                    switch(item_visibilityType.getValue()){
                        case "Add before method body": {
                            try {
                                HandleJarFileWindowController.selectedClassItem.getClassMethod().insertBefore(item_body.getText());
                            } catch (CannotCompileException exception) {
                                HandleJarFileWindowController.thisMainController.showMessage("Cannot compile method. Please check if everything is correct and try again. " + exception.getCause(),main_pane);
                            }
                            break;
                        }
                        case "Add after method body": {
                            try {
                                HandleJarFileWindowController.selectedClassItem.getClassMethod().insertAfter(item_body.getText());
                            } catch (CannotCompileException exception) {
                                HandleJarFileWindowController.thisMainController.showMessage("Cannot compile method. Please check if everything is correct and try again. " + exception.getCause(),main_pane);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        primary_main_pane.setDisable(false);
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        HandleJarFileWindowController.thisMainController.refreshClassProperties();
    }

    public void box_Exit(ActionEvent actionEvent) {
        primary_main_pane.setDisable(false);
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        HandleJarFileWindowController.thisMainController.refreshClassProperties();
    }
}
