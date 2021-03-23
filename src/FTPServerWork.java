import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPServerWork extends Application {
    private static ArrayList<String> listParametrs;

    private Label status = new Label();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Button directories;
    private Button startProcessWithEnteredData;
    private Task<Void> task;
    private TextArea txtConsole = new TextArea();

    public void start(Stage stage) {
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());
        exitItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));

        MenuItem aboutProgramItem = new MenuItem("_About this program");
        aboutProgramItem.setOnAction(event ->
        {
            TextArea areaInfo = new TextArea("Determine and display the total size of all " + "\n" +
                    "www server pages and their number. Print the addresses" + "\n" +
                    "of pages with the largest and smallest sizes");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Work information");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });
        aboutProgramItem.setAccelerator(KeyCombination.keyCombination("Shortcut+I"));

        MenuItem aboutProgramer = new MenuItem("About _programer");
        aboutProgramer.setOnAction(event ->
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Malakhov Georgey, 6302");
            alert.showAndWait();
        });
        aboutProgramer.setAccelerator(KeyCombination.keyCombination("Shortcut+A"));

        Menu helpMenu = new Menu("_Help", null, aboutProgramItem, aboutProgramer, new SeparatorMenuItem(), exitItem);

        Button startDefault = new Button("Default processing");
        startDefault.setMinSize(125, 35);
        startDefault.setOnAction(
                event -> {
                    status.setText("");
                    write(stage);
                });

        Button enterData = new Button("Enter your data");
        enterData.setMinSize(125, 35);
        enterData.setOnAction(
                event -> {
                    EnterParametrToConnection enterDataClass = new EnterParametrToConnection();
                    startProcessWithEnteredData.setDisable(false);
                    myLaunch(enterDataClass);
                });

        directories = new Button("Directories");
        directories.setMinSize(125, 35);
        directories.setOnAction(event -> {
            TreeViewClass tree = new TreeViewClass();
            myLaunch(tree);
        });

        Button clearTextArea = new Button("Clear text area");
        clearTextArea.setMinSize(125, 35);
        clearTextArea.setOnAction(event -> {
            txtConsole.setText("");
            Processing.cleanList();
        });

        startProcessWithEnteredData = new Button("Start process");
        startProcessWithEnteredData.setMinSize(125, 35);
        startProcessWithEnteredData.setDisable(true);
        startProcessWithEnteredData.setOnAction(event -> {
            status.setText("");
            write(stage, listParametrs);
        });

        final double rem = new Text("").getLayoutBounds().getHeight();
        VBox buttons = new VBox(0.8 * rem, startDefault, enterData, startProcessWithEnteredData, clearTextArea, directories);
        buttons.setPadding(new Insets(0.8 * rem));
        txtConsole.setMinWidth(550);


        MenuBar bar = new MenuBar(helpMenu);
        HBox horizontal = new HBox(txtConsole, buttons);
        VBox root = new VBox(bar, horizontal, status);
        stage.setScene(new Scene(root));
        stage.setWidth(750);
        stage.setTitle("Parser");
        stage.show();
    }

    private void write(Stage stage) {

        txtConsole.clear();
        task = new Task<>() {
            @Override
            public Void call() {
                try {
                    Processing.ftpInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        var fileIn = new FileReader("textConsole.txt");

                        StringBuffer buffer = readTransportVehicle(fileIn);
                        txtConsole.appendText(buffer.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        };
        executor.execute(task);
        task.setOnSucceeded(event ->
        {
            status.textProperty().unbind();
            status.setText("Done writing");
            task = null;

            TextArea areaInfo = new TextArea();
            for (Processing.NodeSrc directory : Processing.directoryName) {
                areaInfo.appendText("Name: " + directory.getName() + " type: " + directory.getType() + " byte size: " + directory.getSize() +
                        " User: " + directory.getUser() + " rawListing: " + directory.getRawListing() + " hard-link count: " +
                        directory.getHardLinkCount() + "\n");
            }
            areaInfo.deleteText(0, 76);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Result of process");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });
    }

    private void write(Stage stage, ArrayList<String> listParametrs) {

        txtConsole.clear();
        task = new Task<>() {
            @Override
            public Void call() {
                String ipData = listParametrs.get(0);
                String portData = listParametrs.get(1);
                String userData = listParametrs.get(2);
                String passwordData = listParametrs.get(3);
                try {
                    Processing.ftpInfo(ipData, portData, userData, passwordData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        var fileIn = new FileReader("textConsole.txt");

                        StringBuffer buffer = readTransportVehicle(fileIn);
                        txtConsole.appendText(buffer.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        };
        executor.execute(task);
        task.setOnSucceeded(event ->
        {
            status.textProperty().unbind();
            status.setText("Done writing");
            task = null;

            TextArea areaInfo = new TextArea();
            for (Processing.NodeSrc directory : Processing.directoryName) {
                areaInfo.appendText("Name: " + directory.getName() + " type: " + directory.getType() + " byte size: " + directory.getSize() +
                        " User: " + directory.getUser() + " rawListing: " + directory.getRawListing() + " hard-link count: " +
                        directory.getHardLinkCount() + "\n");
            }
            //areaInfo.deleteText(0, 76);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Result of process");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });
    }

    public static void myLaunch(Application applicationClass) {
        Platform.runLater(() -> {
            try {
                Application application = applicationClass;
                Stage primaryStage = new Stage();
                application.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static StringBuffer readTransportVehicle(Reader in) {
        BufferedReader br = new BufferedReader(in);
        StringBuffer bufferTXT = new StringBuffer();
        try {
            while (br.ready()) {
                bufferTXT.append(br.readLine());
                bufferTXT.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferTXT;
    }

    public static void getListParametrs(ArrayList<String> list) {
        listParametrs = list;
    }
}
