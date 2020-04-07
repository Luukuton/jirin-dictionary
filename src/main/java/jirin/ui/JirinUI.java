package jirin.ui;

import jirin.domain.JirinService;
import jirin.domain.DictEntry;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class JirinUI extends Application {

    private void initUI(Stage stage) {

        var root = new GridPane();

        root.setGridLinesVisible(false);
        root.setVgap(10);
        root.setHgap(5);
        root.setPadding(new Insets(10));

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");

        // Search bar
        var searchField = new TextField();

        searchField.setPromptText("Search here..");
        searchField.setPrefWidth(960);

        // Results
        var error = new Label();
        var resultWordReading = new Label();
        var resultMeaning = new Label();

        resultMeaning.setWrapText(true);
        resultMeaning.setPrefSize(960, 200);
        resultMeaning.setMaxHeight(720);

        // Other elements
        var guide = new TextField();

        guide.setText("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");
        guide.getStyleClass().add("copyable-label");
        guide.setEditable(false);

        // Styling and fonts
        root.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        var searchFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 40
        );

        var resultsFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 25
        );

        searchField.setFont(searchFont);
        resultWordReading.setFont(resultsFont);
        resultMeaning.setFont(resultsFont);
        error.setFont(resultsFont);
        guide.setFont(resultsFont);

        // Do not focus on anything at app launch.
        guide.setFocusTraversable(false);
        searchField.setFocusTraversable(false);

        searchField.setOnKeyPressed(new EventHandler<>() {
            JirinService jirinService = new JirinService();
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String searchInput = searchField.getText();
                    DictEntry entry = jirinService.queryDict(searchInput);

                    if (entry == null) {
                        resultWordReading.setText("");
                        resultMeaning.setText("");
                        error.setText(jirinService.getException());
                    } else {
                        error.setText("");
                        resultWordReading.setText("【" + entry.getWord() + "】" + entry.getReading());

                        String meaningFormatted = "";
                        for (String m : entry.getMeanings()) {
                            meaningFormatted = meaningFormatted.concat(m + "\n");
                        }

                        resultMeaning.setText(meaningFormatted);
                    }
                }
            }
        });

        root.add(guide, 0, 0);
        root.add(searchField, 0, 1);
        root.add(error, 0, 2);
        root.add(resultWordReading, 0, 2);
        root.add(resultMeaning, 0, 3);


        root.setAlignment(Pos.CENTER);

        var scene = new Scene(root, 1280, 720, Color.WHITESMOKE);

        stage.setTitle("辞林・Jirin");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
