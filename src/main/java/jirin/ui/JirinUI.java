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

import java.util.ArrayList;

public class JirinUI extends Application {

    private void initUI(Stage stage) {

        var root = new GridPane();

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");

        root.setGridLinesVisible(false);
        root.setVgap(10);
        root.setHgap(5);
        root.setPadding(new Insets(10));

        var searchFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 45
        );

        var resultsFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 20
        );

        // Search bar
        var searchField = new TextField();
        searchField.setPromptText("Search here..");
        searchField.setPrefWidth(600);
        searchField.setFont(searchFont);

        // Results
        Label error = new Label();
        Label resultWord = new Label();
        Label resultReading = new Label();
        ArrayList<Label> resultMeanings = new ArrayList<>();

        error.setFont(resultsFont);
        resultWord.setFont(resultsFont);
        resultReading.setFont(resultsFont);


        // By not setting the focus on this node, the hint will be displayed immediately.
        // searchField.getParent().requestFocus();

        // "Example words to try: 猫, 想像 and 聞く."
        // "As in 'cat', 'imagination' and 'to hear'."

        searchField.setOnKeyPressed(new EventHandler<>() {
            JirinService jirinService = new JirinService();
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String searchInput = searchField.getText();
                    DictEntry entry = jirinService.queryDict(searchInput);

                    if (entry == null) {
                        error.setText(jirinService.getException());
                    } else {
                        error.setText("");
                        resultWord.setText(entry.getWord());
                        resultReading.setText(entry.getReading());

                        for (String m : entry.getMeanings()) {
                            resultMeanings.add(new Label(m));
                        }
                    }
                }
            }
        });

        root.add(error, 0, 10);
        root.add(searchField, 0, 0);
        root.add(resultWord, 10, 0);
        root.add(resultReading, 5, 0);

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
