package jirin.ui;

import jirin.domain.JirinService;
import jirin.domain.DictEntry;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class JirinUI extends Application {

    Button searchBtn;
    TextField searchField, resultsWordReading, error;
    TextArea resultsMeaning;

    private void initUI(Stage stage) {

        var root = new GridPane();

        root.setGridLinesVisible(false);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        root.setVgap(10);
        root.setHgap(5);

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");

        // Search bar and button
        searchField = new TextField();
        searchField.setPromptText("Search here..");
        searchField.setPrefWidth(960);

        searchBtn = new Button();
        searchBtn.setPrefSize(85, 85);
        searchBtn.setId("search-button");

        var searchBtnRegion = new Region();
        searchBtnRegion.setId("search-icon");
        searchBtn.setGraphic(searchBtnRegion);

        var helpText = new TextField("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");

        // Results
        ScrollPane resultsMeaningArea = new ScrollPane();
        error = new TextField();
        resultsWordReading = new TextField();
        resultsMeaning = new TextArea();
        resultsMeaning.setPrefSize(960, 300);
        resultsMeaning.setWrapText(true);
        resultsMeaningArea.setContent(resultsMeaning);

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
        resultsMeaning.setFont(resultsFont);
        resultsMeaning.getStyleClass().add("copyable-area");
        resultsMeaning.setEditable(false);
        setFieldStyles(resultsFont, resultsWordReading, error, helpText);

        // Do not focus on anything at app launch.
        searchField.setFocusTraversable(false);
        searchBtn.setFocusTraversable(false);
        helpText.setFocusTraversable(false);

        root.add(helpText, 0, 0);
        root.add(searchField, 0, 1);
        root.add(searchBtn, 1,1);
        root.add(error, 0, 2);
        root.add(resultsWordReading, 0, 2);
        root.add(resultsMeaning, 0, 3);

        // Actions
        searchField.setOnKeyPressed(new EventHandler<>() {
            JirinService jirinService = new JirinService();

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    showSearchResults(jirinService);
                }
            }
        });

        searchBtn.setOnAction(new EventHandler<>() {
            JirinService jirinService = new JirinService();

            @Override
            public void handle(ActionEvent arg0) {
                showSearchResults(jirinService);
            }
        } );

        var scene = new Scene(root, 1280, 720, Color.WHITESMOKE);

        // Unfocus out of any element when clicking anywhere.
        scene.setOnMousePressed(event -> root.requestFocus());

        stage.setMinHeight(400);
        stage.setMinWidth(800);
        stage.setTitle("辞林・Jirin");
        stage.setScene(scene);
        stage.show();
    }

    private void showSearchResults(JirinService service) {
        String searchInput = searchField.getText();
        DictEntry entry = service.queryDict(searchInput);

        if (entry == null) {
            resultsWordReading.setText("");
            resultsMeaning.setText("");
            error.setText(service.getException());
        } else {
            error.setText("");
            resultsWordReading.setText("【" + entry.getWord() + "】" + entry.getReading());
            String meaningFormatted = "";

            for (String m : entry.getMeanings()) {
                meaningFormatted = meaningFormatted.concat(m + "\n");
            }

            resultsMeaning.setText(meaningFormatted);
        }
    }

    private void setFieldStyles(Font font, TextField... fields) {
        for (TextField f : fields) {
            f.getStyleClass().add("copyable-label");
            f.setEditable(false);
            f.setFont(font);
        }
    }

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
