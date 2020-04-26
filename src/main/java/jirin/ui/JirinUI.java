package jirin.ui;

import javafx.application.HostServices;
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
    Button searchBtn, settingsBtn, favoritesBtn;
    GridPane header, content, favorites;
    Font searchFont, contentFont;
    Hyperlink sourceLink;
    Region settingsBtnRegion, favoritesBtnRegion, searchBtnRegion;
    Settings settings;
    String sourceURL;
    TextField searchField, resultsHeader, error;
    TextArea resultsMeaning;

    private void initUI(Stage stage) throws IOException {

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");
        settings = new Settings("settings.properties");

        // Basic UI settings
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

        // Search bar & buttons
        searchField = new TextField();
        searchField.setPromptText("Search here..");
        searchField.setPrefWidth(960);
        var helpText = new TextField("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");

        createButtons();

        // Results
        ScrollPane resultsMeaningArea = new ScrollPane();
        error = new TextField();
        resultsHeader = new TextField();
        resultsMeaning = new TextArea();
        resultsMeaning.setPrefSize(960, 300);
        resultsMeaning.setWrapText(true);
        resultsMeaningArea.setContent(resultsMeaning);

        sourceLink = new Hyperlink();
        sourceLink.setDisable(true);

        // Styling and fonts
        themeSwitch(settings.getTheme());

        layout.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        header.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        searchFont = fontSet(settings.getSearchFont(), 40);
        contentFont = fontSet(settings.getContentFont(), 25);

        header.getStyleClass().add("general-style");
        content.getStyleClass().add("general-style");

        resultsMeaning.getStyleClass().add("copyable-area");
        resultsMeaning.setEditable(false);
        resultsMeaning.setFont(contentFont);

        setFieldStyles(contentFont, resultsHeader, error, helpText);

        searchField.setFont(searchFont);

        sourceLink.getStyleClass().addAll("hyperlink");
        sourceLink.setFont(contentFont);

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

        settingsBtn.setOnAction(e -> spawnSettings());

        sourceLink.setOnAction(arg0 -> {
            HostServices services = getHostServices();
            services.showDocument(sourceURL);
        });

        // Close all child windows when exiting the main app
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        // Layout
        int row = 0;
        header.add(settingsBtn,     0, row);
        header.add(favoritesBtn,    1, row);
        header.add(sourceLink,      8, row);

        content.add(helpText,       0, row);
        content.add(searchField,    0, ++row);
        content.add(searchBtn,      1, row);
        content.add(error,          0, ++row);
        content.add(resultsHeader,  0, row);
        content.add(resultsMeaning, 0, ++row);

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
            resultsHeader.setText("");
            resultsMeaning.setText("");
            sourceLink.setText("");
            sourceLink.setDisable(true);
            error.setText(service.getException());
        } else {
            error.setText("");
            sourceLink.setDisable(false);
            sourceURL = "https://dictionary.goo.ne.jp/word/" + entry.hexEncodeWord();
            sourceLink.setText("Source for the word");
            resultsHeader.setText("【" + entry.getWord() + "】" + entry.getReading());

            String meaningFormatted = "";
            for (String m : entry.getMeanings()) {
                meaningFormatted = meaningFormatted.concat(m + "\n\n");
            }

            // Remove new lines from the last entry.
            resultsMeaning.setText(meaningFormatted.substring(0, meaningFormatted.length() - 4));
        }
    }

    private void setFieldStyles(Font font, TextField... fields) {
        for (TextField f : fields) {
            f.getStyleClass().add("copyable-label");
            f.getStyleClass().add("general-style");
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

    private void spawnSettings() {
        var stage = new Stage();
        var settingsContent = new GridPane();
        var scene = new Scene(settingsContent);

        Button saveBtn = new Button(), cancelBtn = new Button();
        Region saveBtnRegion = new Region(), cancelBtnRegion = new Region();
        Label contentFontLabel = new Label(), searchFontLabel = new Label(), themeLabel = new Label();
        Label notice = new Label();

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
        notice.setText("Restarting the app is required \nto apply any font changes.");

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
            themeSwitch(themeChoice.getValue());
            searchFont = fontSet(searchFontChoice.getValue(), 40);
            contentFont = fontSet(contentFontChoice.getValue(), 25);

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

        cancelBtn.setOnAction(e -> stage.close());

        int row = 0;
        settingsContent.add(themeLabel,        0, row);
        settingsContent.add(themeChoice,       1, row);
        settingsContent.add(searchFontLabel,   0, ++row);
        settingsContent.add(contentFontChoice, 1, row);
        settingsContent.add(searchFontChoice,  1, ++row);
        settingsContent.add(contentFontLabel,  0, row);
        settingsContent.add(notice,            1, ++row);
        settingsContent.add(saveBtn,           0, ++row);
        settingsContent.add(cancelBtn,         1, row);

        scene.setOnMousePressed(event -> settingsContent.requestFocus());
        stage.setMinWidth(350);
        stage.setMinHeight(250);
        stage.setTitle("Jirin | Settings");
        stage.setScene(scene);
        stage.show();
    }

    private void themeSwitch(String theme) {
        if (theme.toLowerCase().equals("light")) {
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
    }

    private Font fontSet(String fontName, int size) {
        if (fontName.toLowerCase().equals("noto serif jp")) {
            return Font.loadFont(
                    JirinUI.class.getClassLoader().getResourceAsStream("NotoSerifJP-Regular.otf"),
                    size
            );
        } else if (fontName.toLowerCase().equals("noto sans jp")) {
            return Font.loadFont(
                    JirinUI.class.getClassLoader().getResourceAsStream("NotoSansJP-Regular.otf"),
                    size
            );
        } else {
            return Font.loadFont(
                    JirinUI.class.getClassLoader().getResourceAsStream("MPLUSRounded1c-Regular.ttf"),
                    size
            );
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        initUI(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
