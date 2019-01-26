package Controllers;

import jarHandling.*;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javassist.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HandleJarFileWindowController {

    static HandleJarFileWindowController thisMainController;
    private static FileChooser fileChooser;
    static JarExplorer jarExplorer;


    ArrayList<JarClass> newCreatedClassItems;

    public Pane main_pane;

    public ListView<JarClass> classItems;

    public ListView<JarMethod> tabPane_methodItems;
    public ListView<JarField> tabPane_fieldItems;
    public ListView<JarConstructor> tabPane_constructorItems;

    public Pane pane_optionsBox;
    public ListView<String> optionsBox;

    public Pane messagePane;
    public Label messagePane_textLabel;

    public MenuItem menuBar_save;

    public MenuItem menuBar_package;
    public MenuItem menuBar_interface;
    public MenuItem menuBar_class;
    public MenuItem menuBar_method;
    public MenuItem menuBar_field;
    public MenuItem menuBar_constructor;

    static JarClass selectedClass;
    static JarClassAttribute selectedClassItem;

    public void initialize() {
        createFileChooser();
        newCreatedClassItems = new ArrayList<>();
        tabPane_methodItems.setItems(tabPane_methodItems.getItems());
        tabPane_fieldItems.setItems(tabPane_fieldItems.getItems());
        tabPane_constructorItems.setItems(tabPane_constructorItems.getItems());

        optionsBox.setItems(optionsBox.getItems());
    }

    public void menuBar_openFile(){

        main_pane.setDisable(true);
        File selectedFile = fileChooser.showOpenDialog(null);
        main_pane.setDisable(false);

        if (selectedFile != null) {
            classItems.getItems().clear();
            tabPane_constructorItems.getItems().clear();
            tabPane_methodItems.getItems().clear();
            tabPane_fieldItems.getItems().clear();

            jarExplorer = new JarExplorer(selectedFile.getAbsolutePath());
            ArrayList<JarClass> classesFromJar = null;
            try {
                classesFromJar = jarExplorer.getJarClass();
            } catch (IOException exception) {
                showMessage("Cannot load jar file. "+exception.getCause(), main_pane);
            } catch (NotFoundException exception) {
               showMessage("Cannot find jar file. "+exception.getCause(), main_pane);
            }
            classItems.getItems().setAll(classesFromJar);
            menuBar_save.setDisable(false);
            menuBar_interface.setDisable(false);
            menuBar_class.setDisable(false);
            menuBar_package.setDisable(false);
        }
    }

    public void menuBar_saveFile() {
        File fileToSave = fileChooser.showSaveDialog(null);

        if (fileToSave != null) {
            try {
                jarExplorer.replaceJarFile(classItems, fileToSave);
                showMessage("File has been saved to \"" + fileToSave.getAbsolutePath() +"\". ", main_pane);
            } catch (IOException exception) {
                showMessage("Cannot save file. " + exception.getCause(), main_pane);
            } catch (CannotCompileException exception) {
                showMessage("Cannot compile file. " + exception.getCause(), main_pane);
            }
        }
        else
            showMessage("Cannot save file. Please try again. ", main_pane);
    }

    public void menuBar_classAdd() {
        createWorkingBox("Add class");
    }

    public void menuBar_interfaceAdd() {
        createWorkingBox("Add interface");
    }

    public void menuBar_packageAdd() {
        createWorkingBox("Add package");
    }

    public void menuBar_methodAdd() {
        createWorkingBox("Add method");
    }

    public void menuBar_fieldAdd() {
        createWorkingBox("Add field");
    }

    public void menuBar_constructorAdd() {
        createWorkingBox("Add constructor");
    }

    public void classItems_showClassProperties(MouseEvent mouseEvent) {
        tabPane_fieldItems.getSelectionModel().clearSelection();
        tabPane_methodItems.getSelectionModel().clearSelection();
        tabPane_constructorItems.getSelectionModel().clearSelection();
        if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            if(!classItems.getSelectionModel().isEmpty()) {
                selectedClass = classItems.getSelectionModel().getSelectedItem();
                selectedClassItem = null;
                if(newCreatedClassItems.contains(selectedClass))
                    showOptionsBox(mouseEvent.getSceneX(), mouseEvent.getSceneY(), true);
            }
        } else {
            if (!classItems.getSelectionModel().isEmpty()) {
                menuBar_method.setDisable(false);
                menuBar_field.setDisable(false);
                menuBar_constructor.setDisable(false);
            } else {
                menuBar_method.setDisable(true);
                menuBar_field.setDisable(true);
                menuBar_constructor.setDisable(true);
            }
            refreshClassProperties();
        }
    }

    public void methodItems_showOptionsBox(MouseEvent mouseEvent) {
        tabPane_fieldItems.getSelectionModel().clearSelection();
        tabPane_constructorItems.getSelectionModel().clearSelection();
        if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            if(!tabPane_methodItems.getSelectionModel().isEmpty()) {
                showOptionsBox(mouseEvent.getSceneX(), mouseEvent.getSceneY(), false);
                selectedClassItem = tabPane_methodItems.getSelectionModel().getSelectedItem();
            }
        }
    }

    public void fieldItems_showOptionsBox(MouseEvent mouseEvent) {
        tabPane_methodItems.getSelectionModel().clearSelection();
        tabPane_constructorItems.getSelectionModel().clearSelection();
        if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            if( !tabPane_fieldItems.getSelectionModel().isEmpty()) {
                showOptionsBox(mouseEvent.getSceneX(), mouseEvent.getSceneY(), true);
                selectedClassItem = tabPane_fieldItems.getSelectionModel().getSelectedItem();
            }
        }
    }

    public void constructorItems_showOptionsBox(MouseEvent mouseEvent) {
        tabPane_fieldItems.getSelectionModel().clearSelection();
        tabPane_methodItems.getSelectionModel().clearSelection();
        if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            if(!tabPane_constructorItems.getSelectionModel().isEmpty()) {
                showOptionsBox(mouseEvent.getSceneX(), mouseEvent.getSceneY(), true);
                selectedClassItem = tabPane_constructorItems.getSelectionModel().getSelectedItem();
            }
        }
    }

    public void optionsBox_hide() {
        pane_optionsBox.setVisible(false);
    }

    public void optionsBox_selectAction() {
        if(selectedClass.getCtClass().isFrozen())
            selectedClass.getCtClass().defrost();

        switch(optionsBox.getSelectionModel().getSelectedItem()) {
            case "Edit": {
                createWorkingBox("Editing method");
                break;
            }
            case "Delete": {
                try {
                    if(selectedClassItem == null && selectedClass != null) {
                        classItems.getItems().remove(classItems.getSelectionModel().getSelectedItem());
                        newCreatedClassItems.remove(classItems.getSelectionModel().getSelectedItem());
                    } else if(Objects.requireNonNull(selectedClassItem).getClassField() != null) {
                        selectedClass.getCtClass().removeField(selectedClassItem.getClassField());
                        tabPane_fieldItems.getItems().remove(selectedClassItem);
                    } else if(selectedClassItem.getClassMethod() != null) {
                        selectedClass.getCtClass().removeMethod(selectedClassItem.getClassMethod());
                        tabPane_methodItems.getItems().remove(selectedClassItem);
                    } else if(selectedClassItem.getClassConstructor() != null) {
                        selectedClass.getCtClass().removeConstructor(selectedClassItem.getClassConstructor());
                        tabPane_constructorItems.getItems().remove(selectedClassItem);
                    }
                } catch (NotFoundException exception) {
                    showMessage("Cannot find \"" + Objects.requireNonNull(selectedClassItem).getName() + "\" in selected class. " + exception.getCause(), main_pane);
                }
            }
        }

        pane_optionsBox.setVisible(false);
        tabPane_fieldItems.getSelectionModel().clearSelection();
        tabPane_methodItems.getSelectionModel().clearSelection();
        tabPane_constructorItems.getSelectionModel().clearSelection();
    }

    private void showOptionsBox(double x, double y, boolean onlyDeleteOption) {
            pane_optionsBox.setVisible(true);
            pane_optionsBox.setLayoutX(x-25);
            pane_optionsBox.setLayoutY(y-25);

            optionsBox.getItems().clear();
            if(onlyDeleteOption) {
                optionsBox.getItems().addAll("Delete");
            }
            else
                optionsBox.getItems().addAll("Edit", "Delete");

            optionsBox.setPrefHeight(optionsBox.getItems().size() * 25 - ((optionsBox.getItems().size()-1) * 2));
            pane_optionsBox.setPrefHeight(optionsBox.getPrefHeight() + 50);
    }

    private void createFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(".jar", "*.jar")
        );
    }

    private void showClassMethods(List<CtMethod> methods) {
        tabPane_methodItems.getItems().clear();

        for (CtMethod method : methods) {
            tabPane_methodItems.getItems().add(new JarMethod(method));
        }

        final SortedList<JarMethod> tempSorted = tabPane_methodItems.getItems().sorted();
        tabPane_methodItems.getItems().setAll(tempSorted);

    }

    private void showClassFields(List<CtField> fields) {
        tabPane_fieldItems.getItems().clear();

        for (CtField field : fields) {
            tabPane_fieldItems.getItems().add(new JarField(field));
        }

        final SortedList<JarField> tempSorted = tabPane_fieldItems.getItems().sorted();
        tabPane_fieldItems.getItems().setAll(tempSorted);
    }

    private void showClassConstructors(List<CtConstructor> constructors) {
        tabPane_constructorItems.getItems().clear();

        for (CtConstructor constructor : constructors) {
            tabPane_constructorItems.getItems().add(new JarConstructor(constructor));
        }

        final SortedList<JarConstructor> tempSorted = tabPane_constructorItems.getItems().sorted();
        tabPane_constructorItems.getItems().setAll(tempSorted);
    }

    public void setController(HandleJarFileWindowController mainController){
        thisMainController = mainController;
    }

    void refreshClassProperties(){
        try {
            selectedClass = classItems.getSelectionModel().getSelectedItem();

            showClassMethods(Arrays.asList(selectedClass.getCtClass().getMethods()));
            showClassFields(Arrays.asList(selectedClass.getCtClass().getFields()));
            showClassConstructors(Arrays.asList(selectedClass.getCtClass().getConstructors()));
        }
        catch(NullPointerException ignored) {
        }
    }

    private void createWorkingBox(String title){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("ChangeJarItemsWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Cannot find ChangeJarItemsWindow.fxml.");
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image("images/icon.png"));
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root), 600, 400));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        ChangeJarItemsWindowController controller = fxmlLoader.getController();
        controller.primary_main_pane = main_pane;
        controller.setThisStage(title);

        main_pane.setDisable(true);
    }

    void showMessage(String textToShowOnStage, Pane paneToDisable) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MessageWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Cannot find MessageWindow.fxml.");
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image("images/icon.png"));

        stage.setScene(new Scene(Objects.requireNonNull(root), 300, 200));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        if(paneToDisable != null) {
            MessageWindowController controller = fxmlLoader.getController();
            controller.father_pane = paneToDisable;
            controller.setThisStage(textToShowOnStage);

            paneToDisable.setDisable(true);
        }
    }
}
