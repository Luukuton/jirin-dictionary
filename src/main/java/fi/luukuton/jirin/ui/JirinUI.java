package fi.luukuton.jirin.ui;

import fi.luukuton.jirin.domain.Dictionaries;
import fi.luukuton.jirin.domain.JirinService;
import fi.luukuton.jirin.domain.DictEntry;
import fi.luukuton.jirin.dao.Settings;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class for the application GUI.
 */

public class JirinUI extends Application {
    private int currentIndex;
    private ArrayList<DictEntry> entries;
    private Button searchBtn, settingsBtn, forwardBtn, backwardBtn;
    private GridPane header, content;
    private ComboBox<String> modeChoice;
    private Hyperlink sourceLink;
    private Settings settings;
    private StackPane base;
    private Stage settingsStage;
    private String sourceURL;
    private TextField searchField, resultsHeader;
    private TextArea resultsDefinition;

    /**
     * The main window of the application.
     */

    private void initUI(Stage stage) throws IOException {

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");

        settings = new Settings("settings.properties");

        // Basic UI settings
        base = new StackPane();
        var layout = new VBox();
        content = new GridPane();
        header = new GridPane();

        layout.setSpacing(-11);

        content.setMinHeight(100);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10));
        content.setVgap(5);
        content.setHgap(5);

        header.setAlignment(Pos.TOP_CENTER);
        header.setPadding(new Insets(10));
        header.setVgap(5);
        header.setHgap(5);

        // Search bar & buttons
        var helpText = new TextField("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");
        searchField = new TextField();
        searchField.setPromptText("Search here..");
        modeChoice = new ComboBox<>(FXCollections.observableArrayList("Exact", "Forward", "Backward"));
        modeChoice.getSelectionModel().select("Exact");
        modeChoice.setPrefSize(150, 50);

        createButtons();

        // Results
        resultsHeader = new TextField();

        resultsDefinition = new TextArea();
        resultsDefinition.setPrefSize(960, 300);
        resultsDefinition.setWrapText(true);
        ScrollPane resultsDefinitionArea = new ScrollPane();
        resultsDefinitionArea.setContent(resultsDefinition);

        sourceLink = new Hyperlink();
        sourceLink.setDisable(true);

        // CSS
        themeSwitch(settings.getTheme());

        layout.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        header.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("style.css")).toExternalForm()
        );

        content.getStylesheets().add(
                Objects.requireNonNull(JirinUI.class.getClassLoader().getResource("scroll.css")).toExternalForm()
        );

        // Fonts
        Font searchFont = fontSet(settings.getSearchFont(), 30);
        Font headerFont = fontSet(settings.getContentFont(), 26);
        Font contentFont = fontSet(settings.getContentFont(), 22);

        helpText.setFont(contentFont);
        searchField.setFont(searchFont);
        resultsHeader.setFont(headerFont);
        resultsDefinition.setFont(contentFont);
        sourceLink.setFont(contentFont);

        // Styles
        header.getStyleClass().addAll("primary", "header");
        content.getStyleClass().add("primary");
        sourceLink.getStyleClass().add("hyperlink");

        setInputFieldStyles(resultsDefinition, resultsHeader, helpText);

        // Do not focus on anything at app launch.
        helpText.setFocusTraversable(false);
        searchField.setFocusTraversable(false);
        searchBtn.setFocusTraversable(false);
        settingsBtn.setFocusTraversable(false);

        // Actions
        searchField.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER)) {
                startSearchThread();
            }
        });

        searchBtn.setOnAction(e -> startSearchThread());

        settingsBtn.setOnAction(e -> {
            if (settingsStage != null) {
                settingsStage.close();
            }
            spawnSettings(stage);
        });

        sourceLink.setOnAction(e -> {
            HostServices services = getHostServices();
            services.showDocument(sourceURL);
        });

        forwardBtn.setOnAction(e -> {
            currentIndex = (currentIndex < entries.size() - 1) ? currentIndex + 1 : 0;
            showSearchResult(currentIndex);
        });

        backwardBtn.setOnAction(e -> {
            currentIndex = (currentIndex > 0) ? currentIndex - 1 : entries.size() - 1;
            showSearchResult(currentIndex);
        });

        // Close all child windows when exiting the main app
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        // Scaling GridPane elements while resizing window
        RowConstraints
                r1 = new RowConstraints(),
                r2 = new RowConstraints();

        ColumnConstraints
                c1 = new ColumnConstraints(),
                c2 = new ColumnConstraints();

        r1.setMaxHeight(100);
        r2.setMinHeight(100);
        r2.setPrefHeight(Integer.MAX_VALUE);

        c1.setPercentWidth(88);
        c2.setPercentWidth(90);

        header.getColumnConstraints().add(c1);
        content.getRowConstraints().addAll(r1, r2);
        content.getColumnConstraints().add(c2);

        // Layout
        int row = 0;
        header.add(modeChoice,         0, row);
        header.add(settingsBtn,        1, row);
        header.add(helpText,           0, ++row);
        header.add(searchField,        0, ++row);
        header.add(searchBtn,          1, row);
        header.add(sourceLink,         0, ++row);
        content.add(resultsHeader,     0, row = 0);
        content.add(resultsDefinition, 0, ++row);
        content.add(backwardBtn,       0, ++row);
        content.add(forwardBtn,        1, row);

        layout.getChildren().addAll(header, content);
        base.getChildren().add(layout);
        var scene = new Scene(base, 1280, 720);

        // Unfocus out of any element when clicking anywhere.
        scene.setOnMousePressed(e -> content.requestFocus());

        stage.setMinHeight(520);
        stage.setMinWidth(800);
        stage.setTitle("Jirin");
        stage.getIcons().add(new Image("jirin_icon.png"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates search results for the user input in the search field.
     *
     * @param service parsing logic
     */

    private void fetchSearchResults(JirinService service) {
        String searchInput = searchField.getText();
        String mode = modeChoice.getValue();
        if (mode.equals("Forward")) {
            mode = "m0u";
        } else if (mode.equals("Backward")) {
            mode = "m2u";
        } else {
            mode = "m1u";
        }

        entries = null;
        entries = service.queryDict(searchInput, mode);

        // The UI cannot be directly updated from a non-application thread.
        // It needs to be updated inside Platform.runLater as lambda.
        Platform.runLater(() -> {
            if (entries == null) {
                resultsHeader.setText(service.getException());
                resultsDefinition.setText("");
                sourceLink.setText("");
                sourceLink.setDisable(true);
            } else {
                currentIndex = 0;
                showSearchResult(currentIndex);
                if (entries.size() > 1) {
                    forwardBtn.visibleProperty().setValue(true);
                    backwardBtn.visibleProperty().setValue(true);
                }
            }
        });
    }

    /**
     * Show search result in main window.
     *
     * @param index index of the ArrayList<DictEntry> entries
     */

    private void showSearchResult(int index) {
        sourceLink.setDisable(false);
        sourceLink.setText("Web Source");
        sourceURL = Dictionaries.DICT_GOO + entries.get(index).getURL();

        resultsHeader.setText("【" + entries.get(index).getWord() + "】" + entries.get(index).getReading());

        String definitionFormatted = "";
        for (String m : entries.get(index).getDefinitions()) {
            definitionFormatted = definitionFormatted.concat(m + "\n\n");
        }

        resultsDefinition.setText(definitionFormatted);
    }

    /**
     * Starts a new thread, where the search is performed. Also spawns and removes a loading icon.
     */

    private void startSearchThread() {
        final ProgressIndicator progress = new ProgressIndicator();
        progress.setMinSize(60, 50);
        header.setDisable(true);
        content.setDisable(true);

        VBox overlay = new VBox(progress);
        overlay.setAlignment(Pos.CENTER);
        base.getChildren().add(overlay);

        forwardBtn.visibleProperty().setValue(false);
        backwardBtn.visibleProperty().setValue(false);

        JirinService jirinService = new JirinService();

        new Thread(() -> {
            fetchSearchResults(jirinService);
            Platform.runLater(() -> {
                base.getChildren().remove(overlay);
                header.setDisable(false);
                content.setDisable(false);
            });
        }).start();
    }

    /**
     * The settings window.
     *
     * @param mainStage the main stage of the application
     */

    private void spawnSettings(Stage mainStage) {
        settingsStage = new Stage();
        var settingsContent = new GridPane();
        var scene = new Scene(settingsContent);

        settingsContent.setPadding(new Insets(10));
        settingsContent.setAlignment(Pos.TOP_CENTER);
        settingsContent.setVgap(10);
        settingsContent.setHgap(5);

        Button saveBtn = new Button(), cancelBtn = new Button();
        Region saveBtnRegion = new Region(), cancelBtnRegion = new Region();

        var contentFontLabel = new Label("Content font");
        var searchFontLabel = new Label("Search font");
        var themeLabel = new Label("Theme");
        var notice = new Label("The app will restart itself \nif there are any font changes.");

        saveBtn.setPrefSize(30, 30);
        saveBtn.getStyleClass().add("button");
        saveBtnRegion.setId("confirm-icon");
        saveBtn.setGraphic(saveBtnRegion);

        cancelBtn.setPrefSize(30, 30);
        cancelBtn.getStyleClass().add("button");
        cancelBtnRegion.setId("cancel-icon");
        cancelBtn.setGraphic(cancelBtnRegion);


        var themeChoice = new ComboBox<>(FXCollections.observableArrayList(
                "Dark",
                "Light"
        ));

        var searchFontChoice = new ComboBox<>(FXCollections.observableArrayList(
                "M Plus 1p",
                "Noto Sans JP",
                "Noto Serif JP"
        ));

        var contentFontChoice = new ComboBox<>(FXCollections.observableArrayList(
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

        if (themeText.toLowerCase().equals("light")) {
            settingsContent.getStyleClass().add("light");
        } else {
            settingsContent.getStyleClass().add("dark");
        }

        settingsContent.getStyleClass().addAll("primary", "settings");
        contentFontLabel.getStyleClass().add("settings");
        searchFontLabel.getStyleClass().add("settings");
        themeLabel.getStyleClass().add("settings");
        notice.getStyleClass().add("settings");

        // Actions
        saveBtn.setOnAction(e -> {
            themeSwitch(themeChoice.getValue());

            boolean restartRequired = false;
            if (!settings.getContentFont().equals(contentFontChoice.getValue())
                    || !settings.getSearchFont().equals(searchFontChoice.getValue())) {
                restartRequired = true;
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
            settingsStage.close();

            // Restarts main stage for font changes to apply.
            if (restartRequired) {
                mainStage.close();
                Platform.runLater( () -> {
                    try {
                        new JirinUI().start( new Stage() );
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
            }
        });

        cancelBtn.setOnAction(e -> settingsStage.close());

        int row = 0;
        settingsContent.add(themeLabel,        0, row);
        settingsContent.add(themeChoice,       1, row);
        settingsContent.add(searchFontLabel,   0, ++row);
        settingsContent.add(searchFontChoice,  1, row);
        settingsContent.add(contentFontLabel,  0, ++row);
        settingsContent.add(contentFontChoice, 1, row);
        settingsContent.add(notice,            1, ++row);
        settingsContent.add(saveBtn,           0, ++row);
        settingsContent.add(cancelBtn,         1, row);

        scene.setOnMousePressed(e -> settingsContent.requestFocus());
        settingsStage.setX(mainStage.getX() + mainStage.getWidth() / 3);
        settingsStage.setY(mainStage.getY() + mainStage.getHeight() / 3);
        settingsStage.setMinWidth(400);
        settingsStage.setMinHeight(275);
        settingsStage.setMaxWidth(400);
        settingsStage.setMaxHeight(275);
        settingsStage.setTitle("Jirin | Settings");
        settingsStage.getIcons().add(new Image("jirin_icon.png"));
        settingsStage.setScene(scene);
        settingsStage.show();
    }

    /**
     * Switch themes between light and dark.
     *
     * @param theme theme name
     */

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

    /**
     * Sets styling for text input fields and them to be non-editable.
     *
     * @param area text area for definitions
     * @param fields other text fields
     */

    private void setInputFieldStyles(TextArea area, TextField... fields) {
        area.getStyleClass().add("copyable-area");
        area.setEditable(false);

        for (TextField f : fields) {
            f.getStyleClass().add("copyable-label");
            f.getStyleClass().add("primary");
            f.setEditable(false);
        }
    }

    /**
     * Creates clickable buttons to the main window.
     */

    private void createButtons() {
        searchBtn = new Button();
        searchBtn.setPrefSize(50, 85);
        searchBtn.getStyleClass().add("button");
        Region searchBtnRegion = new Region();
        searchBtnRegion.setId("search-icon");
        searchBtn.setGraphic(searchBtnRegion);

        settingsBtn = new Button();
        settingsBtn.setPrefSize(40, 40);
        settingsBtn.getStyleClass().add("button");
        Region settingsBtnRegion = new Region();
        settingsBtnRegion.setId("settings-icon");
        settingsBtn.setGraphic(settingsBtnRegion);

        forwardBtn = new Button();
        forwardBtn.setPrefSize(30, 30);
        forwardBtn.getStyleClass().add("button");
        Region forwardBtnRegion = new Region();
        forwardBtnRegion.setId("forward-icon");
        forwardBtn.setGraphic(forwardBtnRegion);
        forwardBtn.visibleProperty().setValue(false);

        backwardBtn = new Button();
        backwardBtn.setPrefSize(30, 30);
        backwardBtn.getStyleClass().add("button");
        Region backwardBtnRegion = new Region();
        backwardBtnRegion.setId("backward-icon");
        backwardBtn.setGraphic(backwardBtnRegion);
        backwardBtn.visibleProperty().setValue(false);
    }

    /**
     * Sets font with desired size.
     *
     * @param fontName font name
     * @param size size of the font or text size
     */

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
