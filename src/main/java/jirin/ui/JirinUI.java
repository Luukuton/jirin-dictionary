package jirin.ui;

import jirin.domain.JirinService;
import jirin.domain.DictEntry;
import jirin.dao.Settings;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.Objects;

public class JirinUI extends Application {
    GridPane header, content, favorites;
    Button searchBtn, settingsBtn, favoritesBtn;
    Region settingsBtnRegion, favoritesBtnRegion, searchBtnRegion;
    TextField searchField, resultsWordReading, error;
    TextArea resultsMeaning;
    Font contentFont, searchFont;


    private void initUI(Stage stage) {

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");

        var layout = new BorderPane();
        content = new GridPane();
        header = new GridPane();
        favorites = new GridPane();

        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);
        content.setVgap(10);
        content.setHgap(5);

        header.setPadding(new Insets(10));
        header.setVgap(10);
        header.setHgap(5);

        // Search bar
        searchField = new TextField();
        searchField.setPromptText("Search here..");
        searchField.setPrefWidth(960);
        var helpText = new TextField("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");

        // Buttons
        createButtons();

        // Results
        ScrollPane resultsMeaningArea = new ScrollPane();
        error = new TextField();
        resultsWordReading = new TextField();
        resultsMeaning = new TextArea();
        resultsMeaning.setPrefSize(960, 300);
        resultsMeaning.setWrapText(true);
        resultsMeaningArea.setContent(resultsMeaning);

        // Styling and fonts
        header.getStyleClass().add("light");
        content.getStyleClass().add("light");
        header.getStyleClass().add("general-style");
        content.getStyleClass().add("general-style");

        layout.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        header.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        searchFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 40
        );

        contentFont = Font.loadFont(
                JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"), 25
        );

        resultsMeaning.getStyleClass().add("copyable-area");
        resultsWordReading.getStyleClass().add("general-style");
        helpText.getStyleClass().add("general-style");

        resultsMeaning.setEditable(false);
        resultsMeaning.setFont(contentFont);
        searchField.setFont(searchFont);
        setFieldStyles(contentFont, resultsWordReading, error, helpText);

        // Do not focus on anything at app launch.
        helpText.setFocusTraversable(false);
        searchField.setFocusTraversable(false);
        searchBtn.setFocusTraversable(false);
        settingsBtn.setFocusTraversable(false);
        favoritesBtn.setFocusTraversable(false);

        // Actions
        searchField.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER)) {
                JirinService jirinService = new JirinService();
                showSearchResults(jirinService);
            }
        });

        searchBtn.setOnAction(arg0 -> {
            JirinService jirinService = new JirinService();
            showSearchResults(jirinService);
        });

        settingsBtn.setOnAction(e -> {
            try {
                spawnSettings();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        // Close all child windows when exiting the main app
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        // Layout
        header.add(settingsBtn, 0, 0);
        header.add(favoritesBtn, 1, 0);
        content.add(helpText, 0, 1);
        content.add(searchField, 0, 3);
        content.add(searchBtn, 1, 3);
        content.add(error, 0, 4);
        content.add(resultsWordReading, 0, 4);
        content.add(resultsMeaning, 0, 5);

        header.setId("scene-light");

        layout.setTop(header);
        layout.setCenter(content);
        layout.setRight(favorites);

        var scene = new Scene(layout, 1280, 720);

        // Unfocus out of any element when clicking anywhere.
        scene.setOnMousePressed(event -> content.requestFocus());

        stage.setMinHeight(400);
        stage.setMinWidth(800);
        stage.setTitle("Jirin");
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

    private void createButtons() {
        searchBtn = new Button();
        searchBtn.setPrefSize(50, 85);
        searchBtn.getStyleClass().add("button");
        searchBtnRegion = new Region();
        searchBtnRegion.setId("search-icon");
        searchBtn.setGraphic(searchBtnRegion);

        settingsBtn = new Button();
        settingsBtn.setPrefSize(40, 40);
        settingsBtn.getStyleClass().add("button");
        settingsBtnRegion = new Region();
        settingsBtnRegion.setId("settings-icon");
        settingsBtn.setGraphic(settingsBtnRegion);

        favoritesBtn = new Button();
        favoritesBtn.setPrefSize(40, 40);
        favoritesBtn.getStyleClass().add("button");
        favoritesBtnRegion = new Region();
        favoritesBtnRegion.setId("favorites-icon");
        favoritesBtn.setGraphic(favoritesBtnRegion);
    }

    private void spawnSettings() throws IOException {
        Settings settings = new Settings();

        var stage = new Stage();
        var settingsContent = new GridPane();
        var scene = new Scene(settingsContent);

        Button saveBtn = new Button(), cancelBtn = new Button();
        Region saveBtnRegion = new Region(), cancelBtnRegion = new Region();
        Label contentFontLabel = new Label(), searchFontLabel = new Label(), themeLabel = new Label();

        settingsContent.setPadding(new Insets(10));
        settingsContent.setAlignment(Pos.TOP_CENTER);
        settingsContent.setVgap(10);
        settingsContent.setHgap(5);

        saveBtn.setPrefSize(30, 30);
        saveBtn.getStyleClass().add("button");
        saveBtnRegion.setId("confirm-icon");
        saveBtn.setGraphic(saveBtnRegion);

        cancelBtn.setPrefSize(30, 30);
        cancelBtn.getStyleClass().add("button");
        cancelBtnRegion.setId("cancel-icon");
        cancelBtn.setGraphic(cancelBtnRegion);

        themeLabel.setText("Theme");
        searchFontLabel.setText("Font (search)");
        contentFontLabel.setText("Font (content)");

        var themeChoice = new ChoiceBox<>(FXCollections.observableArrayList(
                "Dark",
                "Light"
        ));

        var searchFontChoice = new ChoiceBox<>(FXCollections.observableArrayList(
                "M Plus 1p",
                "Noto Sans JP",
                "Noto Serif JP"
        ));

        var contentFontChoice = new ChoiceBox<>(FXCollections.observableArrayList(
                "M Plus 1p",
                "Noto Sans JP",
                "Noto Serif JP"
        ));

        // Default choices
        String themeText = settings.getTheme();
        themeChoice.getSelectionModel().select(themeText.substring(0, 1).toUpperCase() + themeText.substring(1));
        searchFontChoice.getSelectionModel().select(settings.getSearchFont());
        contentFontChoice.getSelectionModel().select(settings.getContentFont());

        // Styling
        settingsContent.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        settingsContent.getStyleClass().add("settings");

        // Actions
        saveBtn.setOnAction(e -> {
            if (themeChoice.getValue().equals("Light")) {
                header.getStyleClass().add("light");
                content.getStyleClass().add("light");
                header.getStyleClass().remove("dark");
                content.getStyleClass().remove("dark");

            } else {
                header.getStyleClass().add("dark");
                content.getStyleClass().add("dark");
                header.getStyleClass().remove("light");
                content.getStyleClass().remove("light");
            }

            try {
                settings.saveSettings(
                        searchFontChoice.getValue(),
                        contentFontChoice.getValue(),
                        themeChoice.getValue().toLowerCase()
                );
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            stage.close();
        });

        cancelBtn.setOnAction(e -> {
            stage.close();
        });

        settingsContent.add(themeLabel, 0, 0);
        settingsContent.add(searchFontLabel, 0, 1);
        settingsContent.add(contentFontLabel, 0, 2);
        settingsContent.add(themeChoice, 1, 0);
        settingsContent.add(contentFontChoice, 1, 1);
        settingsContent.add(searchFontChoice, 1, 2);
        settingsContent.add(saveBtn, 0, 4);
        settingsContent.add(cancelBtn, 1, 4);

        scene.setOnMousePressed(event -> settingsContent.requestFocus());
        stage.setMinWidth(300);
        stage.setMinHeight(250);
        stage.setTitle("Jirin | Settings");
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
