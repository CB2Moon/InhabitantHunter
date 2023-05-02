package researchsim.display;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import researchsim.entities.Entity;
import researchsim.entities.Fauna;
import researchsim.entities.User;
import researchsim.scenario.AnimalController;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * View for the Research Simulation GUI.
 *
 * @given
 */
public class View {
    /**
     * Stage containing the application scene
     */
    private final Stage stage;

    /**
     * ViewModel that manages interaction with the model
     */
    private final ViewModel viewModel;

    /**
     * Custom canvas that represents the state of the simulation graphically
     */
    private ScenarioCanvas canvas;

    /**
     * Creates a new view for the given view model and adds the associated GUI elements to the given
     * stage.
     *
     * @param stage     stage to add GUI elements to
     * @param viewModel view model to display
     * @given
     */
    public View(Stage stage, ViewModel viewModel) throws BadSaveException {
        this.stage = stage;
        this.viewModel = viewModel;

        stage.setResizable(false);

        stage.titleProperty().bind(Bindings.concat("Research Simulation: "
            + ScenarioManager.getInstance().getScenario().getName()));
        Scene rootScene = new Scene(createWindow());
        stage.setScene(rootScene);
    }

    /**
     * Initialises the view and begins the timer responsible for performing ticks
     *
     * @given
     */
    public void run() {
        final long nanosPerSecond = 1000000000;

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                if (viewModel.isChanged()) {
                    viewModel.notChanged();
                    viewModel.updateScenarioLog();
                    canvas.draw();
                }
            }
        }.start();

        this.stage.show();
        this.canvas.draw();
    }

    /***
     * Prompts the user for a textual response via a dialog box.
     *
     * @param title title of dialog box window
     * @param header header text of dialog box
     * @param label label text to display beside input box
     * @param defaultValue initial contents of the input box
     * @return value entered by the user
     * @given
     */
    public Optional<String> getResponse(String title, String header,
                                        String label, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(label);
        dialog.setGraphic(null);
        return dialog.showAndWait();
    }

    /***
     * Prompts the user for a numeric response via a dialog box.
     *
     * @param title title of dialog box window
     * @param header header text of dialog box
     * @param label label text to display beside input box
     * @param defaultValue initial contents of the input box
     * @return value entered by the user
     * @given
     */
    public Optional<Integer> getResponse(String title, String header,
                                         String label, int defaultValue) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(defaultValue));
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(label);
        dialog.setGraphic(null);
        // Only allow numeric values to be entered
        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dialog.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        return dialog.showAndWait().map(Integer::valueOf);
    }

    /* Creates the root window containing all GUI elements */
    private Pane createWindow() throws BadSaveException {
        this.canvas = new ScenarioCanvas(viewModel, 680, 680);
        BorderPane.setAlignment(canvas, Pos.CENTER_RIGHT);

        var simulation = new HBox();
        simulation.getChildren().add(createSimMenu());
        simulation.getChildren().add(canvas);

        var pane = new VBox();
        pane.getChildren().add(createMenuBar());
        pane.getChildren().add(simulation);

        return pane;
    }

    /* Creates the windows containing the controls of the simulation */
    private Pane createSimMenu() {
        var root = new VBox();

        // a spacing section
        var space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        VBox.setVgrow(space, Priority.ALWAYS);

        var unitInfoBox = createInfoBox(viewModel.getEntityInfoText(), 10);
        root.getChildren().add(unitInfoBox);

        var buttons = new HBox();
        buttons.setPadding(new Insets(10, 10, 10, 10));
        buttons.setSpacing(10);

        var moveButton = new Button("_Move");
        moveButton.setPrefWidth(150);
        moveButton.setMnemonicParsing(true);
        moveButton.setAlignment(Pos.CENTER);
        moveButton.setOnAction((event) -> {
            if (!viewModel.getHasMoved()) {
                Entity entity = viewModel.getSelectedEntity().get();
                if (entity instanceof User || entity instanceof Fauna) {
                    canvas.drawEntityMove(entity);
                }
            }
        });

        var collectButton = new Button("_Collect");
        collectButton.setPrefWidth(150);
        collectButton.setMnemonicParsing(true);
        collectButton.setAlignment(Pos.CENTER);
        collectButton.setOnAction((event) -> {
            Entity entity = viewModel.getSelectedEntity().get();
            if (entity instanceof User) {
                canvas.drawUserCollect((User) entity);
            }
        });

        buttons.getChildren().add(moveButton);
        buttons.getChildren().add(space);
        buttons.getChildren().add(collectButton);

        root.getChildren().add(buttons);

        var turns = new HBox();
        turns.setPadding(new Insets(10, 10, 10, 10));
        turns.setSpacing(10);

        var endTurnButton = new Button("End _Turn");
        endTurnButton.setPrefWidth(300);
        endTurnButton.setMnemonicParsing(true);
        endTurnButton.setAlignment(Pos.CENTER);
        endTurnButton.setOnAction((event) -> {
            AnimalController manager = ScenarioManager.getInstance().getScenario().getController();
            manager.move();
            viewModel.registerChange();
            viewModel.setHasMoved(false);
        });

        endTurnButton.setAlignment(Pos.CENTER);
        turns.getChildren().add(endTurnButton);

        root.getChildren().add(turns);

        var scenarioStatistics = createInfoBox(viewModel.getScenarioStatisticsText(), 4);
        root.getChildren().add(scenarioStatistics);

        var scenarioLogBox = createInfoBox(viewModel.getScenarioLogText(), 13);
        root.getChildren().add(scenarioLogBox);

        return root;
    }

    /* Creates a menu bar that allows actions to be taken within the GUI */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        MenuItem save = new MenuItem("_Save");
        save.setMnemonicParsing(true);
        save.setOnAction(event -> {
            try {
                viewModel.save();
            } catch (IOException e) {
                viewModel.createErrorDialog("Error saving to file",
                    e.getMessage());
                return;
            }
            viewModel.createSuccessDialog("Saved successfully",
                "Saved to default provided file locations successfully.");
        });

        MenuItem exit = new MenuItem("_Exit");
        exit.setMnemonicParsing(true);
        exit.setOnAction(event -> System.exit(0));
        exit.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));

        Menu menuFile = new Menu("_File");
        menuFile.setMnemonicParsing(true);
        menuFile.getItems().add(save);
        menuFile.getItems().add(createSaveAsMenuItem());
        menuFile.getItems().add(new SeparatorMenuItem());
        menuFile.getItems().add(exit);

        Menu menuScenario = new Menu("_Scenario");
        menuScenario.getItems().addAll(createScenarioMenuItems());

        MenuItem showGrid = new MenuItem("Show Map Grid");
        showGrid.setOnAction(event -> {
            viewModel.showGridProperty().set(!viewModel.showGrid());
            viewModel.registerChange();
        });
        MenuItem showGridCoordinate = new MenuItem("Show Map Grid Coordinates");
        showGridCoordinate.setOnAction(event -> {
            viewModel.showCoordinateProperty().set(!viewModel.showCoordinate());
            viewModel.registerChange();
        });

        Menu menuOptions = new Menu("_Options");
        menuOptions.setMnemonicParsing(true);
        menuOptions.getItems().add(showGrid);
        menuOptions.getItems().add(showGridCoordinate);


        menuBar.getMenus().add(menuFile);
        menuBar.getMenus().add(menuScenario);
        menuBar.getMenus().add(menuOptions);
        return menuBar;
    }

    /* Creates a menu item that, when clicked, prompts for the state of the model to be saved */
    private MenuItem createSaveAsMenuItem() {
        MenuItem saveAs = new MenuItem("Save _As...");
        saveAs.setMnemonicParsing(true);
        saveAs.setOnAction(event -> {
            var filename = getResponse("Save to scenario file",
                "Please enter the path of the file to save to", "scenario file name", "");
            if (filename.isEmpty()) {
                return;
            }
            try {
                viewModel.saveAs(new FileWriter(filename.get()));
            } catch (IOException e) {
                viewModel.createErrorDialog("Error saving to file",
                    e.getMessage());
                return;
            }
            viewModel.createSuccessDialog("Saved files successfully",
                "Saved to \"" + filename.get() + "\" successfully.");
        });
        saveAs.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        return saveAs;
    }

    /* Creates menu items that, when clicked, changes the currently loaded scenario */
    private List<MenuItem> createScenarioMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        ScenarioManager manager = ScenarioManager.getInstance();
        for (Map.Entry<String, Scenario> pairs :
            manager.getLoadedScenarios().entrySet()) {
            MenuItem item = new MenuItem("Scenario: _" + pairs.getKey());
            item.setMnemonicParsing(true);
            item.setOnAction(event -> {
                try {
                    manager.setScenario(pairs.getKey());
                } catch (BadSaveException ignored) {
                    // ignored
                }
                viewModel.registerChange();
                stage.titleProperty().bind(Bindings.concat("Research Simulation: "
                    + ScenarioManager.getInstance().getScenario().getName()));
                viewModel.getSelectedEntity().set(null);
            });
            items.add(item);
        }
        return items;
    }

    /*
     * Creates a non-editable text area to display some text information
     * Automatically scrolls to the bottom
     */
    private TextArea createInfoBox(StringProperty contents, int rowCount) {
        var infoBox = new TextArea();
        infoBox.textProperty().bind(contents);
        contents.addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                infoBox.selectPositionCaret(infoBox.getLength());
                infoBox.deselect();
            }
        });
        infoBox.setEditable(false);
        infoBox.setFocusTraversable(false);
        infoBox.setWrapText(false);
        infoBox.setFont(Font.font(14));
        infoBox.setPrefRowCount(rowCount);
        infoBox.setPrefWidth(300);
        return infoBox;
    }

    /* Prompts the user for a choice from a list of options */
    @SafeVarargs
    private <T> Optional<T> getChoice(String title, String header, String label,
                                      T defaultChoice, T... choices) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(label);
        dialog.setGraphic(null);
        return dialog.showAndWait();
    }

}
