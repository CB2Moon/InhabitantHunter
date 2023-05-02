package researchsim.display;

import javafx.beans.property.*;
import javafx.scene.control.Alert;
import researchsim.entities.Entity;
import researchsim.entities.Fauna;
import researchsim.logging.Logger;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;

import java.io.*;
import java.util.List;
import java.util.StringJoiner;

/**
 * View model for the Research Simulation GUI.
 * <p>
 * You must implement the following methods in this class:
 * <ul>
 *     <li>{@link #updateScenarioLog()}</li>
 *     <li>{@link #saveAs(Writer)}</li>
 * </ul>
 * @ass2
 */
public class ViewModel {

    /**
     * Whether the state of the model has changed
     */
    private final BooleanProperty changed = new SimpleBooleanProperty(false);

    /**
     * Whether the canvas grid is shown
     */
    private final BooleanProperty showGrid = new SimpleBooleanProperty(true);

    /**
     * Whether the canvas grid coordinates is shown
     */
    private final BooleanProperty showCoordinate = new SimpleBooleanProperty(true);

    /**
     * Contents of entities information text box
     */
    private final StringProperty entityInfoText = new SimpleStringProperty(
        "No collectable selected");

    /**
     * Contents of the scenario statistics text box
     */
    private final StringProperty scenarioStatisticsText = new SimpleStringProperty(
        "No statistics generated");

    /**
     * Contents of the scenario log text box
     */
    private final StringProperty scenarioLogText = new SimpleStringProperty(
        "No events logged");

    /**
     * The currently selected (clicked) entity
     */
    private final ObjectProperty<Entity> selectedEntity = new SimpleObjectProperty<>();

    /**
     * If the user has moved this turn
     */
    private boolean hasMoved = false;

    /**
     * Creates a new view model and constructs scenarios by reading from the given filenames.
     *
     * @param filenames list of filenames specifying the path to: the scenario files
     * @throws IOException      if loading from a file specified generates an
     *                          IOException
     * @throws BadSaveException if any file is invalid according to
     *                          {@link Scenario#load(Reader)}
     * @requires filenames != null &amp;&amp; filenames.size() >= 1
     * @given
     */
    public ViewModel(List<String> filenames) throws IOException, BadSaveException {
        for (String file : filenames) {
            Scenario.load(new FileReader(file));
        }

        // make sure set scenario was first loaded
        ScenarioManager manager = ScenarioManager.getInstance();
        manager.setScenario(manager.getLoadedScenarios().values().iterator().next().getName());

        this.selectedEntity.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                entityInfoText.set("No collectable selected");
            } else {
                Entity entity = selectedEntity.getValue();
                StringJoiner info = new StringJoiner(System.lineSeparator());
                info.add(entity.toString());
                info.add("-".repeat(25));
                info.add("Additional Information");
                info.add("Species Class: " + entity.getClass().getSimpleName());
                info.add("Name: " + entity.getName());
                info.add("Coordinate : " + entity.getCoordinate());
                info.add("Move Distance : " + entity.getSize().moveDistance);
                info.add("Possible Points : " + entity.getSize().points);
                entityInfoText.set(info.toString());
            }
            registerChange();
        });
    }

    /**
     * Returns if the user has moved this turn
     *
     * @return has moved
     * @given
     */
    public boolean getHasMoved() {
        return hasMoved;
    }

    /**
     * sets if the user has moved this turn
     *
     * @param hasMoved updated state
     * @given
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Returns the property storing the currently selected entity; or null if no
     * entity is selected.
     *
     * @return currently selected entity property
     * @given
     */
    public ObjectProperty<Entity> getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * Returns the property storing the contents of the entity info text box.
     *
     * @return entity info text box
     * @given
     */
    public StringProperty getEntityInfoText() {
        return entityInfoText;
    }

    /**
     * Returns the property storing the contents of the scenario statistics text box.
     *
     * @return scenario statistics text
     * @given
     */
    public StringProperty getScenarioStatisticsText() {
        return scenarioStatisticsText;
    }

    /**
     * Returns the property storing the contents of the scenario log text box.
     *
     * @return scenario log text
     * @given
     */
    public StringProperty getScenarioLogText() {
        return scenarioLogText;
    }

    /**
     * Returns whether or not the state of the model has changed since it was last checked for a
     * change.
     *
     * @return has the model changed since last check
     * @given
     */
    public boolean isChanged() {
        return changed.get();
    }

    /**
     * Updates the contents of the scenario log and statistics text. <br>
     * If the log has no contents the method returns immediately.<br>
     * The contents of the log text should be updated to:
     * <pre>
     * event1
     * event2
     * ...
     * </pre>
     * where:
     * <ul>
     *   <li>{@code event1} is the {@link #toString()} of the first event in the logger
     *   <li>{@code event2} is the {@link #toString()} of the second event in the logger
     *   <li>And so on...</li>
     * </ul>
     * <p>
     * The contents of the statistics text should be updated to:
     * <pre>
     * Entities Collected: [#entitiesCollected]
     * Tiles traversed: [#tilesTraversed]
     * Points Earned: [#pointsEarned]</pre>
     * where:
     * <ul>
     * <li>[#entitiesCollected] is the number of entities collected</li>
     * <li>[#tilesTraversed] is the number of tiles traversed</li>
     * <li>[#pointsEarned] is the number of points earned</li>
     * </ul>
     * Each item is separated by the system-dependent line separator. <br> No traling new line
     * after {@code "Points Earned:"}
     * <p>
     * <p>
     * For example:
     * <pre>
     * Dave [User] at (6,1)
     * MOVED TO (7,3)
     * -----
     * Crab [Fauna] at (1,1) [OCEAN]
     * MOVED TO (3,1)
     * -----</pre>
     *  and
     *  <pre>
     * Entities Collected: 2
     * Tiles Traversed: 10
     * Points Earned: 5</pre>
     * <br>
     * Variables you will need:<br><ul>
     *     <li>scenarioLogText</li>
     *     <li>scenarioStatisticsText</li>
     * </ul>
     * @ass2
     */
    public void updateScenarioLog() {
        Logger log = ScenarioManager.getInstance().getScenario().getLog();
        this.scenarioLogText.setValue(log.toString());
        StringJoiner statText = new StringJoiner(System.lineSeparator());
        statText.add(String.format("Entities Collected: %d", log.getEntitiesCollected()));
        statText.add(String.format("Tiles Traversed: %d", log.getTilesTraversed()));
        statText.add(String.format("Points Earned: %d", log.getPointsEarned()));
        this.scenarioStatisticsText.setValue(statText.toString());
    }

    /**
     * Returns the status of if the grid coordinate should be shown
     *
     * @return grid coordinate visibility
     * @given
     */
    public boolean showCoordinate() {
        return showCoordinate.get();
    }

    /**
     * Returns the status of if the grid should be shown
     *
     * @return grid visibility
     * @given
     */
    public boolean showGrid() {
        return showGrid.get();
    }


    /**
     * Saves the current state of the research simulation to the file "_default_save.txt"
     *
     * @throws IOException if an IOException occurs when writing to the files
     * @given
     */
    public void save() throws IOException {
        saveAs(new FileWriter("saves/_default_save.txt"));
    }

    /**
     * Saves the current state of the research simulation to the given writer.
     * <p>
     * The writer should be written to in the following format:
     * <pre>
     * {ScenarioName}
     * Width:{Width}
     * Height:{Height}
     * Seed:{Seed}
     * {Separator}
     * {map}
     * {Separator}
     * {entity}
     * {entity...}
     * </pre>
     * Where:
     * <ul>
     * <li>{@code {ScenarioName}} is the name of the scenario</li>
     * <li>{@code {Width}} is the width of the scenario</li>
     * <li>{@code {Height}} is the Height of the scenario</li>
     * <li>{@code {Seed}} is the seed of the scenario</li>
     * <li>NOTE: There is no whitespace between the ':' and value for the above conditions</li>
     * <li>{@code {Separator}} is a string of repeated equals {@code "="} characters where the
     * number of characters is equal to the width of the scenario <br>i.e. width == 5 -> separator
     * == "====="
     * </li>
     * <li>{@code {map}} is the tile map grid where:
     *      <ul>
     *          <li>Each tile is represented by its {@link TileType} encoding
     *          ({@link TileType#encode()})</li>
     *          <li>A system-dependent line separator is added after {@code Width} characters
     *          are written <br>(See example below)</li>
     *      </ul>
     * </li>
     * <li>{@code {entity}} is the {@link Entity#encode()} of each entity found in the map where:
     *      <ul>
     *          <li>Each entity is added in the order it appears in the array by index (i.e. an
     *          entity inhabiting a tile with index 1 appears before an entity inhabiting a tile
     *          with index 4</li>
     *          <li>A system-dependent line separator is added after {@code entity} EXCEPT the
     *          last entity</li>
     *      </ul>
     * </li>
     * </ul>
     * For example, a simple scenario with the following attributes:
     * <ul>
     *     <li>Name - Scenario X</li>
     *     <li>Width - 5</li>
     *     <li>Height - 5</li>
     *     <li>Seed - 0</li>
     *     <li>A Mouse located at Coordinate (1,1)
     *     <br> See {@link Fauna#getName()}</li>
     *     <li>The map is as shown in the save
     *     <br>Each Tile is represented by its {@link TileType#encode()} value</li>
     * </ul>
     * would be saved as
     * <pre>
     * Scenario X
     * Width:5
     * Height:5
     * Seed:0
     * =====
     * LLLLS
     * LLSSO
     * LLSOO
     * LLSSS
     * LLLLL
     * =====
     * Fauna-SMALL-1,1-LAND
     * </pre>
     *
     * @param scenarioWriter writer to which the scenario will be written
     * @throws IOException if an IOException occurs when writing to the writer
     * @ass2
     * @see Scenario#encode()
     */
    public void saveAs(Writer scenarioWriter) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(scenarioWriter)) {
            bufferedWriter.write(
                    ScenarioManager.getInstance().getScenario().encode()
            );
        }
    }

    /**
     * Creates and shows an error dialog.
     *
     * @param headerText  text to show in the dialog header
     * @param contentText text to show in the dialog content box
     * @given
     */
    public void createErrorDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    /**
     * Creates and shows a success dialog.
     *
     * @param headerText  text to show in the dialog header
     * @param contentText text to show in the dialog content box
     * @given
     */
    public void createSuccessDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    /**
     * Acknowledges the model has changed, and sets the changed status to false.
     *
     * @given
     */
    public void notChanged() {
        changed.setValue(false);
    }

    /**
     * Registers that the model has changed, and the view needs to be updated.
     *
     * @given
     */
    public void registerChange() {
        changed.setValue(true);
    }

    /**
     * Returns the object containing the status of if the grid should be shown
     *
     * @return grid visibility
     * @given
     */
    public BooleanProperty showGridProperty() {
        return showGrid;
    }

    /**
     * Returns the object containing the status of if the grid coordinate should be shown
     *
     * @return grid coordinate visibility
     * @given
     */
    public BooleanProperty showCoordinateProperty() {
        return showCoordinate;
    }
}
