package fi.luukuton.jirin.ui;

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
    private Font searchFont, headerFont, contentFont;
    private Hyperlink sourceLink;
    private Settings settings;
    private Stage settingsStage;
    private String sourceURL;
    private TextField searchField, resultsHeader, error;
    private TextArea resultsMeaning;

    /**
     * The main window of the application.
     */

    private void initUI(Stage stage) throws IOException {

        // Better antialiasing for the text.
        System.setProperty("prism.lcdtext", "false");
        settings = new Settings("settings.properties");

        // Basic UI settings
        var layout = new VBox();
        content = new GridPane();
        header = new GridPane();

        content.setMinHeight(100);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10));
        content.setVgap(5);
        content.setHgap(5);

        header.setPadding(new Insets(10));
        header.setVgap(5);
        header.setHgap(5);

        // Search bar & buttons
        searchField = new TextField();
        searchField.setPromptText("Search here..");
        var helpText = new TextField("Examples: 猫, 楽観, 聞く. As in 'cat', 'optimism', 'to hear'.");
        modeChoice = new ComboBox<>(FXCollections.observableArrayList("Exact", "Forward", "Backward"));
        modeChoice.getSelectionModel().select("Exact");

        createButtons();

        // Results
        error = new TextField();

        resultsHeader = new TextField();

        resultsMeaning = new TextArea();
        resultsMeaning.setPrefSize(960, 300);
        resultsMeaning.setWrapText(true);
        ScrollPane resultsMeaningArea = new ScrollPane();
        resultsMeaningArea.setContent(resultsMeaning);

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
        searchFont = fontSet(settings.getSearchFont(), 40);
        headerFont = fontSet(settings.getContentFont(), 30);
        contentFont = fontSet(settings.getContentFont(), 25);

        helpText.setFont(contentFont);
        error.setFont(contentFont);
        searchField.setFont(searchFont);
        resultsHeader.setFont(headerFont);
        resultsMeaning.setFont(contentFont);
        sourceLink.setFont(contentFont);

        // Styles
        header.getStyleClass().addAll("general-style", "header");
        content.getStyleClass().add("general-style");
        sourceLink.getStyleClass().add("hyperlink");

        setInputFieldStyles(resultsMeaning, resultsHeader, error, helpText);

        // Do not focus on anything at app launch.
        helpText.setFocusTraversable(false);
        searchField.setFocusTraversable(false);
        searchBtn.setFocusTraversable(false);
        settingsBtn.setFocusTraversable(false);

        // Actions
        searchField.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER)) {
                JirinService jirinService = new JirinService();
                fetchSearchResults(jirinService);
            }
        });

        searchBtn.setOnAction(e -> {
            JirinService jirinService = new JirinService();
            fetchSearchResults(jirinService);
        });

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

        // Close all child windows when exiting the main app
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        // Scaling GridPane elements while resizing window
        RowConstraints
                r1 = new RowConstraints(),
                r2 = new RowConstraints(),
                r3 = new RowConstraints(),
                r4 = new RowConstraints();

        ColumnConstraints c1 = new ColumnConstraints();

        r4.setMinHeight(100);
        r4.setPrefHeight(Integer.MAX_VALUE);

        c1.setPercentWidth(80);

        content.getRowConstraints().addAll(r1, r2, r3, r4);
        content.getColumnConstraints().add(c1);

        // Layout
        int row = 0;
        header.add(settingsBtn,     0, row);
        header.add(modeChoice,      11, row);
        header.add(sourceLink,      12, row);

        content.add(helpText,       0, row);
        content.add(searchField,    0, ++row);
        content.add(searchBtn,      1, row);
        content.add(error,          0, ++row);
        content.add(resultsHeader,  0, row);
        content.add(resultsMeaning, 0, ++row);

        VBox.setVgrow(header, Priority.NEVER);
        VBox.setVgrow(content, Priority.ALWAYS);
        layout.getChildren().addAll(header, content);

        var scene = new Scene(layout, 1280, 720);

        // Unfocus out of any element when clicking anywhere.
        scene.setOnMousePressed(e -> content.requestFocus());

        stage.setMinHeight(450);
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

        if (entries == null) {
            resultsHeader.setText("");
            resultsMeaning.setText("");
            sourceLink.setText("");
            sourceLink.setDisable(true);
            error.setText(service.getException());
            content.getChildren().remove(forwardBtn);
            content.getChildren().remove(backwardBtn);
        } else {
            currentIndex = 0;
            showSearchResult(currentIndex);

            if (entries.size() > 1) {
                forwardBtn = new Button();
                backwardBtn = new Button();

                forwardBtn.setPrefSize(30, 30);
                forwardBtn.getStyleClass().add("button");
                Region forwardBtnRegion = new Region();
                forwardBtnRegion.setId("forward-icon");
                forwardBtn.setGraphic(forwardBtnRegion);

                backwardBtn.setPrefSize(30, 30);
                backwardBtn.getStyleClass().add("button");
                Region backwardBtnRegion = new Region();
                backwardBtnRegion.setId("backward-icon");
                backwardBtn.setGraphic(backwardBtnRegion);

                content.add(backwardBtn,  0, 4);
                content.add(forwardBtn,  1, 4);

                forwardBtn.setOnAction(e -> {
                    currentIndex = (currentIndex < entries.size() - 1) ? currentIndex + 1 : 0;
                    showSearchResult(currentIndex);
                });

                backwardBtn.setOnAction(e -> {
                    currentIndex = (currentIndex > 0) ? currentIndex - 1 : entries.size() - 1;
                    showSearchResult(currentIndex);
                });
            } else {
                content.getChildren().remove(forwardBtn);
                content.getChildren().remove(backwardBtn);
            }
        }
    }

    /**
     * Show search result in main window.
     *
     * @param index index of the ArrayList<DictEntry> entries
     */

    private void showSearchResult(int index) {
        error.setText("");
        sourceLink.setDisable(false);
        sourceURL = "https://dictionary.goo.ne.jp/word/" + entries.get(index).hexEncodeWord();
        sourceLink.setText("Source for the word");
        resultsHeader.setText("【" + entries.get(index).getWord() + "】" + entries.get(index).getReading());

        String meaningFormatted = "";
        for (String m : entries.get(index).getMeanings()) {
            meaningFormatted = meaningFormatted.concat(m + "\n\n");
        }

        resultsMeaning.setText(meaningFormatted);
    }

    /**
     * The settings window.
     */

    private void spawnSettings(Stage mainStage) {
        settingsStage = new Stage();
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
        searchFontLabel.setText("Search font");
        contentFontLabel.setText("Content font");
        notice.setText("The app will restart itself \nif there are any font changes.");

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

        settingsContent.getStyleClass().add("settings");

        // Actions
        saveBtn.setOnAction(e -> {
            themeSwitch(themeChoice.getValue());
            searchFont = fontSet(searchFontChoice.getValue(), 40);
            headerFont = fontSet(settings.getContentFont(), 30);
            contentFont = fontSet(contentFontChoice.getValue(), 25);

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
        settingsContent.add(contentFontChoice, 1, row);
        settingsContent.add(searchFontChoice,  1, ++row);
        settingsContent.add(contentFontLabel,  0, row);
        settingsContent.add(notice,            1, ++row);
        settingsContent.add(saveBtn,           0, ++row);
        settingsContent.add(cancelBtn,         1, row);

        scene.setOnMousePressed(e -> settingsContent.requestFocus());
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
     * @param area text area for meanings
     * @param fields other text fields
     */

    private void setInputFieldStyles(TextArea area, TextField... fields) {
        area.getStyleClass().add("copyable-area");
        area.setEditable(false);

        for (TextField f : fields) {
            f.getStyleClass().add("copyable-label");
            f.getStyleClass().add("general-style");
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
