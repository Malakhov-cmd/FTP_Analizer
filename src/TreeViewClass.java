import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class TreeViewClass extends Application {
    private ArrayList<Processing.NodeSrc> newList;
    private int nodeCounter = 2;

    @Override
    public void start(Stage primaryStage) {
        newList = Processing.getDirectoryNameList();
        if (newList.size() == 0) {
            try {
                throw new IOException();
            } catch (IOException e) {
                System.out.println("Input data was incorrect");

                Button exiteButton = new Button("Exit");
                exiteButton.setMinSize(125, 35);
                exiteButton.setOnAction(event -> {
                    primaryStage.close();
                });

                TextArea textArea = new TextArea("Input data was incorrect. Please, try again");
                VBox root = new VBox(textArea, exiteButton);
                primaryStage.setTitle("JavaFX TreeView");
                primaryStage.setScene(new Scene(root, 450, 250));

                primaryStage.show();
            }
        } else {
            TreeItem<Processing.NodeSrc> rootTree = new TreeItem<>(newList.get(0));
            createTreeStructure(rootTree, 1, 1);

            TreeView<Processing.NodeSrc> tree = new TreeView<>(rootTree);

            StackPane root = new StackPane();
            root.setPadding(new Insets(5));
            root.getChildren().add(tree);

            primaryStage.setTitle("JavaFX TreeView");
            primaryStage.setScene(new Scene(root, 450, 250));

            primaryStage.show();
        }
    }

    public void createTreeStructure(TreeItem<Processing.NodeSrc> rootTree, int currentRoot, int i) {
        if (newList.size() > i) {
            TreeItem<Processing.NodeSrc> tree = new TreeItem<>(newList.get(i));
            if (newList.get(i).getLevel() == currentRoot) {
                rootTree.getChildren().add(tree);
                createTreeStructure(rootTree, currentRoot, i + 1);
            } else {
                rootTree.getChildren().get(i - nodeCounter).getChildren().add(tree);
                nodeCounter++;
                createTreeStructure(rootTree, currentRoot, i + 1);
            }
        }
    }
}
