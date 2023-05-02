package researchsim.scenario;

import researchsim.util.BadSaveException;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton class which manages all the scenarios items.
 * <p>
 * All {@link Scenario}s loaded ({@link Scenario#load(Reader)}) in a Java session
 * (each session is when you run the project) must be registered with this manager using
 * {@link #addScenario(Scenario)},
 * which will allow other classes in the project to access the scenario's information during run
 * time.
 * <p>
 * For more information on the Singleton class see: <p>
 * <a href="https://www.geeksforgeeks.org/singleton-class-java/">GeeksForGeeks</a>
 * OR
 * <a href="https://www.baeldung.com/java-singleton">Baeldung</a>
 *
 * @ass1_partial
 */
public class ScenarioManager {

    /**
     * The singleton instance.
     */
    private static ScenarioManager instance = null;

    /**
     * All scenarios that have currently been loaded.
     */
    private final Map<String, Scenario> scenarios;

    /**
     * The currently loaded scenario.
     */
    private String current;

    /**
     * Creates a new ScenarioManager with an empty map of scenarios and current selected set to
     * null.
     *
     * @ass1
     */
    private ScenarioManager() {
        this.scenarios = new LinkedHashMap<>();
        this.current = null;
        instance = this;
    }

    /**
     * Returns the singleton instance of the scenario manager.
     *
     * @return singleton instance
     * @ass1
     */
    public static ScenarioManager getInstance() {
        if (instance == null) {
            instance = new ScenarioManager();
        }
        return instance;
    }

    /**
     * Gets the current scenario from the manager.
     *
     * @return current scenario or null if none has been set.
     * @throws NullPointerException if no scenario exists yet - helpful addition
     * @ass1
     */
    public Scenario getScenario() {
        Scenario s = scenarios.get(current);
        if (s == null) {
            throw new NullPointerException("Tried to access a scenario from the manager when none"
                + " have been added");
        }
        return s;
    }

    /**
     * Sets the current scenario from the manager.
     *
     * @param scenarioName the name of the scenario to set
     * @throws BadSaveException if the scenario has not been added to the manager previously.
     * @ass1_partial
     */
    public void setScenario(String scenarioName) throws BadSaveException {
        if (this.scenarios.get(scenarioName) == null) {
            throw new BadSaveException();
        }
        current = scenarioName;
    }

    /**
     * Gets all the loaded scenarios in a map.
     * <p>
     * The order that the items should appear in the map should be the same as the order the
     * scenarios were loaded (insertion order).
     * <p>
     * Adding or removing elements from the returned map should not affect the original map.
     *
     * @return all the scenarios that have been loaded
     * @ass1
     */
    public Map<String, Scenario> getLoadedScenarios() {
        return new LinkedHashMap<>(scenarios);
    }

    /**
     * Registers a scenario with the manager.
     * <p>
     * The Scenario should be added to a Map with its name ({@link Scenario#getName()}) as the key.
     * <p>
     * If a scenario with the given name has previously been added it is replaced by the new
     * addition.
     * <p>
     * Upon adding a scenario it should be set as the "active" scenario (that is, it should be
     * returned by calling {@link #getScenario()}).
     *
     * @param scenario a scenario to register with the manager
     * @ass1_partial
     */
    public void addScenario(Scenario scenario) {
        this.scenarios.put(scenario.getName(), scenario);
        this.current = scenario.getName();
    }

    /**
     * Resets the singleton by clearing all recorded scenarios.
     * <p>
     * That is, the Map of all scenarios that have been loaded should be empty ({@code size()}
     * returns 0).
     */
    public void reset() {
        this.scenarios.clear();
    }
}
