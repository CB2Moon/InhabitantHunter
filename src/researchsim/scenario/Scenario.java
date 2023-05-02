package researchsim.scenario;

import researchsim.entities.*;
import researchsim.logging.Logger;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.util.Encodable;
import researchsim.util.NoSuchEntityException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;


/**
 * The scenario is the overriding class of the simulation.
 * It is similar to a level in a video game.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 */
public class Scenario implements Encodable {

    /**
     * The minimum dimensions of the map grid.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MIN_SIZE = 5;
    /**
     * The maximum dimensions of the map grid.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MAX_SIZE = 15;
    /**
     * Maximum number of tiles that the grid contains.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MAX_TILES = MAX_SIZE * MAX_SIZE;
    /**
     * The name of this scenario.
     */
    private final String name;
    /**
     * The width of the map in the scenario.
     */
    private final int width;
    /**
     * The height of the map in the scenario.
     */
    private final int height;
    /**
     * The scenarios enemy manager.
     */
    private final AnimalController controller;
    /**
     * The the scenario's activity log .
     */
    private final Logger log;
    /**
     * The Random seed for this scenario.
     */
    private final Random random;
    /**
     * The seed for this scenario.
     */
    private final int seed;
    /**
     * The tile grid for this scenario.
     */
    private Tile[] mapGrid;

    /**
     * Creates a new Scenario with a given name, width, height and random seed. <br>
     * A one dimensional (1D) array of tiles is created as the board with the given width and
     * height. <br>
     * An empty Animal Controller and logger is also initialised. <br>
     * An instance of the {@link Random} class in initialised with the given seed.
     *
     * @param name   scenario name
     * @param width  width of the board
     * @param height height of the board
     * @param seed   the random seed for this scenario
     * @throws IllegalArgumentException if width &lt; {@value Scenario#MIN_SIZE} or width &gt;
     *                                  {@value Scenario#MAX_SIZE} or height
     *                                  &lt; {@value Scenario#MIN_SIZE} or height &gt;
     *                                  {@value Scenario#MAX_SIZE} or seed &lt; 0 or name is {@code
     *                                  null}
     * @ass1_partial
     * @see Random see docs
     */
    public Scenario(String name, int width, int height, int seed)
            throws IllegalArgumentException {
        if (width > MAX_SIZE || width < MIN_SIZE) {
            throw new IllegalArgumentException("The given width does not conform to the "
                    + "requirement: " + MIN_SIZE + " <= width <= " + MAX_SIZE + ".");
        }
        if (height > MAX_SIZE || height < MIN_SIZE) {
            throw new IllegalArgumentException("The given height does not conform to the "
                    + "requirement: " + MIN_SIZE + " <= height <= " + MAX_SIZE + ".");
        }
        if (seed < 0) {
            throw new IllegalArgumentException("The given seed does not conform to the "
                    + "requirement: 0 < seed.");
        }
        if (name == null) {
            throw new IllegalArgumentException("The given name does not conform to the "
                    + "requirement: name != null.");
        }
        this.name = name;
        this.width = width;
        this.height = height;
        this.mapGrid = new Tile[width * height];
        this.controller = new AnimalController();
        this.log = new Logger();
        this.seed = seed;
        this.random = new Random(seed);
    }

    /**
     * Creates a Scenario instance by reading information from the given reader.
     * <p>
     * The provided reader should contain data  in the format:
     * </p><p>
     * </p><pre> {ScenarioName}
     *  Width:{Width}
     *  Height:{Height}
     *  Seed:{Seed}
     *  {Separator}
     *  {map}
     *  {Separator}
     *  {entity}
     *  {entity...}
     *  </pre>
     * (As specified by <a href="#encode()"><code>encode()</code></a>)
     * <p>
     * The reader is invalid if any of the following conditions are true:
     * </p><ul>
     * <li>The given reader is empty</li>
     * <li>The reader hits <code>EOF</code> (end of file) before all of the required information
     * is present. The required information is:
     *     <ol>
     *         <li><code>ScenarioName</code></li>
     *         <li><code>Width</code></li>
     *         <li><code>Height</code></li>
     *         <li><code>Seed</code></li>
     *         <li><code>map</code></li>
     *     </ol>
     *     It is not required to have an <code>entity</code>. But a <code>Separator</code> must
     *     exist after
     *     the <code>map</code>.
     *     </li>
     *     <li>The required information does NOT appear in the order specified above.</li>
     *     <li>If the <code>Width</code>, <code>Height</code> or <code>Seed</code> lines do not
     *     contain exactly one (<code>1</code>) colon (<code>":"</code>)</li>
     *     <li>If any of the <code>Width</code>, <code>Height</code> and <code>Seed</code> keys
     *     (before colon)
     *     are not <code>"Width"</code>, <code>"Height"</code> and <code>"Seed"</code> respectively.
     *     </li>
     *     <li>If any of the <code>Width</code>, <code>Height</code> and <code>Seed</code> values
     *     (after colon)
     *     are not a valid integer (i.e. cannot be parsed by <code>Integer.parseInt(String)</code>)
     *     </li>
     *     <li>If any of the <code>ScenarioName</code>, <code>Width</code>, <code>Height</code> and
     *     <code>Seed</code>
     *     values cause an <code>IllegalArgumentException</code> when used to create a Scenario with
     *     the constructor <a href="#%3Cinit%3E(java.lang.String,int,int,int)"><code>Scenario(String
     *     , int, int, int)</code></a>.
     *     <br><code>Scenario(ScenarioName, Width, Height, Seed)</code></li>
     *     <li>A separator does not have exactly the <code>Width</code> value (or its default, see
     *     below) number of equals characters (<code>"="</code>).<br> i.e. <code>Width</code> == 5
     *     -&gt;
     *     separator == "=====". <br> A separator must appear on the line immediately after the
     *     <code>Seed</code> and the last line of the <code>map</code>.</li>
     *     <li>If any of the following hold true for a <code>map</code> line:
     *          <ul>
     *              <li>The number of characters on that line is not exactly the <code>Width</code>
     *              value (or its default, see below).
     *              <br>NOTE: This includes trailing whitespace such as tabs and spaces (<code>
     *              "\t"</code> and <code>" "</code>).</li>
     *              <li>If the number of characters provided is <b>not</b> equal to the size of
     *              the scenario created with the <code>Width</code> and <code>Height</code> values
     *              </li>
     *              <li>If ANY character provided can <b>not</b> be parsed by
     *              <a href="../map/TileType.html#decode(java.lang.String)">
     *                  <code>TileType.decode(String)</code></a></li>
     *          </ul>
     *     </li>
     *     <li>If any of the following hold true for an <code>entity</code> line:
     *          <ul>
     *              <li>The line does not contain the correct number of hyphen (<code>"-"</code>)
     *              characters for its respective encoding
     *              (<a href="../entities/Fauna.html#encode()"><code>Fauna.encode()</code></a>,
     *              <a href="../entities/Entity.html#encode()"><code>Entity.encode()</code></a>,
     *              <a href="../entities/User.html#encode()"><code>User.encode()</code></a>)</li>
     *              <li>The line does not start with <code>"Fauna"</code> or <code>"Flora"</code> or
     *              <code>"User"</code></li>
     *              <li>If the Coordinate component can not be decoded by
     *              <a href="../map/Coordinate.html#decode(java.lang.String)">
     *                  <code>Coordinate.decode(String)</code></a></li>
     *              <li>If the Coordinate specified already has an Entity assigned.<br>
     *              i.e. You can not have multiple entities request the same Coordiante in the
     *              reader</li>
     *              <li>If line starts with <code>"Fauna"</code> or <code>"Flora"</code> AND the
     *              Size component can not be decoded by <a href="../entities/Size.html#valueOf
     *              (java.lang.String)"><code>Size.valueOf(String)</code></a></li>
     *              <li>If line starts with <code>"Fauna"</code> AND the Habitat
     *              component can not be decoded by
     *              <a href="../map/TileType.html#valueOf(java.lang.String)">
     *                  <code>TileType.valueOf(String)</code></a></li>
     *              <li>If line starts with <code>"Fauna"</code> AND the Habitat
     *              value causes an <code>IllegalArgumentException</code> to be thrown</li>
     *              <li>If line starts with <code>"Fauna"</code> AND the Habitat
     *              at the Tile specified by the Coordinate is not suitable<br>
     *              That is, if Habitat is <a href="../map/TileType.html#OCEAN"><code>TileType.OCEAN
     *              </code></a> the Tile's type must be
     *              <a href="../map/TileType.html#OCEAN"><code>TileType.OCEAN</code></a>.
     *              If the Habitat is <a href="../map/TileType.html#LAND"><code>TileType.LAND</code>
     *              </a> the
     *              Tile's type must <b>NOT</b> be <a href="../map/TileType.html#OCEAN">
     *                  <code>TileType.OCEAN</code></a>.
     *              </li>
     *              <li>If line starts with <code>"Flora"</code> AND the Tile specified by the
     *              Coordinate
     *              is not suitable<br>
     *              That is, if Tile's type is <a href="../map/TileType.html#OCEAN">
     *                  <code>TileType.OCEAN</code></a> then it is INVALID.
     *              </li>
     *              <li>If line starts with <code>"User"</code> AND the Tile specified by the
     *              Coordinate
     *              is not suitable<br>
     *              That is, if Tile's type is <a href="../map/TileType.html#OCEAN">
     *                  <code>TileType.OCEAN</code></a> or
     *              <a href="../map/TileType.html#MOUNTAIN"><code>TileType.MOUNTAIN</code></a>
     *              then it is INVALID.
     *              </li>
     *          </ul>
     *     </li>
     * </ul>
     * <p>
     * If the <code>Width</code>, <code>Height</code> and <code>Seed</code> values are
     * <code>-1</code> they
     * should be assigned a default value of <a href="#MIN_SIZE"><code>MIN_SIZE</code></a>
     * (<a href="#MIN_SIZE">5</a>).
     * </p><p>
     * The created Scenario should be added to the
     * <a href="ScenarioManager.html" title="class in researchsim.scenario"><code>ScenarioManager
     * </code></a> class by calling
     * <a href="ScenarioManager.html#addScenario(researchsim.scenario.Scenario)">
     *     <code>ScenarioManager.addScenario(Scenario)</code></a>.
     * </p><p>
     * The created Scenario map should be set to the map as descriped in the Reader.
     * </p><p>
     * The created entities should be inhabiting the Tiles at the Coordinate specified.
     * <br> (HINT: Make sure that you add the Scenario before this step so that you can utilise the
     * index of the Coordinate by
     * <a href="../map/Coordinate.html#getIndex()"><code>Coordinate.getIndex()</code></a> or
     * <a href="../map/Coordinate.html#convert(int,int)"><code>Coordinate.convert(int, int)
     * </code></a>.)
     * </p><p>
     * For example, the reader could contain:
     * </p><pre> Example File
     * Width:-1
     * Height:6
     * Seed:20
     * =====
     * LLLLS
     * LLSSO
     * LLSOO
     * LLSSS
     * LLLLL
     * LLLLL
     * =====
     * Fauna-SMALL-1,1-LAND
     * Flora-LARGE-2,5-LAND
     * </pre>
     * Noting that the Width is set to a default value of
     * <a href="#MIN_SIZE"><code>MIN_SIZE</code></a> (<a href="#MIN_SIZE">5</a>)
     * and as such the Map is valid
     * <p>
     * The simplest file would be:
     * </p><pre> Example File
     * Width:5
     * Height:5
     * Seed:5
     * =====
     * LLLLL
     * LLLLL
     * LLLLL
     * LLLLL
     * LLLLL
     * =====
     * </pre>
     *
     * @param reader reader from which to load all info (will not be null)
     * @return scenario created by reading from the given reader
     * @throws IOException      if an IOException is encountered when reading from the reader
     * @throws BadSaveException if the reader contains a line that does not adhere to the
     *                          rules above (thus indicating that the contents of the reader
     *                          are invalid)
     */
    public static Scenario load(Reader reader) throws IOException, BadSaveException {
        // a function used to decode width, height, seed
        ToIntBiFunction<String, String> decodeVar = (line, targetVar) -> {
            if (line.indexOf(":") != line.lastIndexOf(":")) {
                return Integer.MIN_VALUE; // invalid, not exactly one :
            }
            String[] components = line.split(":");
            if (components.length != 2) {
                return Integer.MIN_VALUE; // invalid format
            }
            if (!components[0].equals(targetVar)) {
                return Integer.MIN_VALUE; // invalid, not the right variable
            }
            int ans;
            try {
                ans = Integer.parseInt(components[1]);
            } catch (NumberFormatException e) {
                return Integer.MIN_VALUE; // invalid, number not a number
            }
            if (ans < -1) {
                return Integer.MIN_VALUE; // invalid value
            }
            return ans;
        };
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            // reading name, width, height, seed
            String loadName = null;
            int loadWidth = Integer.MIN_VALUE;
            int loadHeight = Integer.MIN_VALUE;
            int loadSeed = Integer.MIN_VALUE;
            for (int i = 0; i < 4; i++) {
                line = bufferedReader.readLine();
                if (line == null || line.isBlank()) {
                    throw new BadSaveException();
                }
                switch (i) {
                    case 0:
                        loadName = line;
                        break;
                    case 1:
                        loadWidth = decodeVar.applyAsInt(line, "Width");
                        if (loadWidth == -1) {
                            loadWidth = MIN_SIZE;
                        }
                        break;
                    case 2:
                        loadHeight = decodeVar.applyAsInt(line, "Height");
                        if (loadHeight == -1) {
                            loadHeight = MIN_SIZE;
                        }
                        break;
                    case 3:
                        loadSeed = decodeVar.applyAsInt(line, "Seed");
                        if (loadSeed == -1) {
                            loadSeed = MIN_SIZE;
                        }
                        break;
                }
            }
            Scenario retScenario;
            try {
                retScenario = new Scenario(loadName, loadWidth, loadHeight, loadSeed);
            } catch (IllegalArgumentException e) {
                throw new BadSaveException();
            }
            // reading first separator
            line = bufferedReader.readLine();
            if (line == null || !line.equals("=".repeat(loadWidth))) {
                throw new BadSaveException();
            }
            ScenarioManager.getInstance().addScenario(retScenario);
            // reading map grid
            Tile[] loadMapGrid = retScenario.getMapGrid();
            for (int i = 0; i < loadHeight; i++) {
                line = bufferedReader.readLine();
                if (line == null || line.length() != loadWidth) {
                    throw new BadSaveException();
                }
                for (int tileNum = 0; tileNum < loadWidth; tileNum++) {
                    loadMapGrid[i * loadWidth + tileNum] = new Tile(
                            TileType.decode(String.valueOf(line.charAt(tileNum)))
                    );
                }
            }
            retScenario.setMapGrid(loadMapGrid);
            // reading last separator
            line = bufferedReader.readLine();
            if (line == null || !line.equals("=".repeat(loadWidth))) {
                throw new BadSaveException();
            }
            // reading entities
            while ((line = bufferedReader.readLine()) != null) {
                Entity loadEntity = decodeLoadEntity(line);
                retScenario.addEntity(loadEntity);
            }
            return retScenario;
        } catch (CoordinateOutOfBoundsException e) {
            throw new BadSaveException();
        }
    }

    /**
     * Decode the line read from the reader<br>
     * <ul>
     *      <li>The line does not contain the correct number of hyphen ("-")
     *      characters for its respective encoding</li>
     *      <li>The line does not start with "Fauna" or "Flora" or "User"</li>
     *      <li>If the Coordinate component can not be decoded by</li>
     *      <li>If line starts with "Fauna" or "Flora" AND the Size component
     *      can not be decoded by Size.valueOf(String)</li>
     *      <li>If line starts with "Fauna" AND the Habitat component can not be
     *      decoded by TileType.valueOf(String)</li>
     *      <li>If line starts with "Fauna" AND the Habitat value causes an
     *      IllegalArgumentException to be thrown</li>
     * </ul>
     *
     * @param line the line read from teh reader
     * @return the corresponding entity
     * @throws BadSaveException if the rules above are not satisfied
     */
    private static Entity decodeLoadEntity(String line) throws BadSaveException {
        String[] components = line.split("-");
        Entity ans;
        int dashNum = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '-') {
                dashNum++;
            }
        }
        Coordinate coordinate;
        Size size;
        switch (components[0]) {
            case "User":
                if (dashNum != 2 || components.length != 3) {
                    throw new BadSaveException();
                }
                coordinate = Coordinate.decode(components[1]);
                String name = components[2];
                if (name == null || name.isBlank()) {
                    throw new BadSaveException();
                }
                ans = new User(coordinate, name);
                break;
            case "Fauna":
                if (dashNum != 3 || components.length != 4) {
                    throw new BadSaveException();
                }
                TileType habitat;
                try {
                    size = Size.valueOf(components[1]);
                    habitat = TileType.valueOf(components[3]);
                    coordinate = Coordinate.decode(components[2]);
                    ans = new Fauna(size, coordinate, habitat);
                } catch (IllegalArgumentException e) {
                    throw new BadSaveException();
                }
                break;
            case "Flora":
                if (dashNum != 2 || components.length != 3) {
                    throw new BadSaveException();
                }
                coordinate = Coordinate.decode(components[2]);
                try {
                    size = Size.valueOf(components[1]);
                } catch (IllegalArgumentException e) {
                    throw new BadSaveException();
                }
                ans = new Flora(size, coordinate);
                break;
            default:
                throw new BadSaveException();
        }
        return ans;
    }

    /**
     * Load and add the entity to the loading scenario<br>
     * Rules below must hold true:
     * <ul>
     *      <li>The Coordinate specified does not have an Entity assigned.</li>
     *      <li>If entity is "Fauna" AND the Habitat at the Tile specified by the Coordinate is
     *      not suitable
     *      That is, if Habitat is TileType.OCEAN the Tile's type must be TileType.OCEAN. If the
     *      Habitat is
     *      TileType.LAND the Tile's type must NOT be TileType.OCEAN.</li>
     *      <li>If entity is "Flora" AND the Tile specified by the Coordinate is not suitable.
     *      That is, if Tile's type is TileType.OCEAN then it is INVALID.</li>
     *      <li>If entity is "User" AND the Tile specified by the Coordinate is not suitable
     *      That is, if Tile's type is TileType.OCEAN or TileType.MOUNTAIN then it is INVALID.</li>
     * </ul>
     *
     * @param entity the entity to add
     * @throws BadSaveException if the entity does not adhere to the rules above
     */
    private void addEntity(Entity entity) throws BadSaveException {
        Tile tile = this.getMapGrid()[entity.getCoordinate().getIndex()];
        TileType tileType = tile.getType();
        if (tile.hasContents()) {
            throw new BadSaveException();
        }
        if (entity instanceof User) {
            User user = (User) entity;
            if (tileType == TileType.OCEAN || tileType == TileType.MOUNTAIN) {
                throw new BadSaveException();
            }
            tile.setContents(user);
        } else if (entity instanceof Fauna) {
            Fauna animal = (Fauna) entity;
            if (animal.getHabitat() == TileType.OCEAN) {
                if (tileType != TileType.OCEAN) {
                    throw new BadSaveException();
                }
            } else {
                if (tileType == TileType.OCEAN) {
                    throw new BadSaveException();
                }
            }
            this.getController().addAnimal(animal);
            tile.setContents(animal);
        } else {
            Flora plant = (Flora) entity;
            if (tileType == TileType.OCEAN) {
                throw new BadSaveException();
            }
            tile.setContents(plant);
        }
    }

    /**
     * Returns the scenarios enemy manager.
     *
     * @return enemy manager
     */
    public AnimalController getController() {
        return controller;
    }

    /**
     * Returns the scenario's activity log.
     *
     * @return game log
     */
    public Logger getLog() {
        return log;
    }

    /**
     * Returns the scenarios random instance.
     *
     * @return random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Returns the name of the scenario.
     *
     * @return scenario name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the map grid for this scenario.
     * <p>
     * Adding or removing elements from the returned array should not affect the original array.
     *
     * @return map grid
     * @ass1
     */
    public Tile[] getMapGrid() {
        return Arrays.copyOf(mapGrid, getSize());
    }

    /**
     * Updates the map grid for this scenario.
     * <p>
     * Adding or removing elements from the array that was passed should not affect the class
     * instance array.
     *
     * @param map the new map
     * @throws CoordinateOutOfBoundsException (param) map length != size of current scenario map
     * @ass1_partial
     */
    public void setMapGrid(Tile[] map) throws CoordinateOutOfBoundsException {
        if (map.length != this.height * this.width) {
            throw new CoordinateOutOfBoundsException();
        }
        mapGrid = Arrays.copyOf(map, getSize());
    }

    /**
     * Returns the width of the map for this scenario.
     *
     * @return map width
     * @ass1
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map for this scenario.
     *
     * @return map height
     * @ass1
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the map in the scenario.<br>
     * The size of a map is the total number of tiles in the Tile array.
     *
     * @return map size
     * @ass1
     */
    public int getSize() {
        return width * height;
    }

    /**
     * Returns the hash code of this scenario.<br>
     * Two scenarios that are equal according to the equals(Object)
     * method should have the same hash code.
     *
     * @return hash code of this scenario.
     */
    @Override
    public int hashCode() {
        return String.format(
                "%s%d%d%d",
                this.name,
                this.width,
                this.height,
                Arrays.hashCode(this.mapGrid)).hashCode();
    }

    /**
     * Returns true if and only if this scenario is equal to the other given object.
     * <p>
     * For two scenarios to be equal, they must have the same:
     * </p><ul>
     * <li>name</li>
     * <li>width</li>
     * <li>height</li>
     * <li>map contents (The tile array)</li>
     * </ul>
     *
     * @param other the reference object with which to compare
     * @return true if this scenario is the same as the other argument; false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        if (!this.name.equals(((Scenario) other).name)) {
            return false;
        }
        if (this.width != ((Scenario) other).width) {
            return false;
        }
        if (this.height != ((Scenario) other).height) {
            return false;
        }
        return Arrays.equals(this.mapGrid, ((Scenario) other).mapGrid);
    }

    /**
     * Returns the human-readable string representation of this scenario.
     * <p>
     * The format of the string to return is:
     * <pre>
     *     (name)
     *     Width: (width), Height: (height)
     *     Entities: (entities)
     * </pre>
     * Where:
     * <ul>
     *   <li>{@code (name)} is the scenario's name</li>
     *   <li>{@code (width)} is the scenario's width</li>
     *   <li>{@code (height)} is the scenario's height</li>
     *   <li>{@code (entities)} is the number of entities currently on the map in the scenario</li>
     * </ul>
     * For example:
     *
     * <pre>
     *     Beach retreat
     *     Width: 6, Height: 5
     *     Entities: 4
     * </pre>
     * <p>
     * Each line should be separated by a system-dependent line separator.
     *
     * @return human-readable string representation of this scenario
     * @ass1
     */
    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(System.lineSeparator());
        result.add(name);
        result.add(String.format("Width: %d, Height: %d", width, height));
        result.add(String.format("Entities: %d",
                Arrays.stream(mapGrid).filter(Objects::nonNull).filter(Tile::hasContents).count()));
        return result.toString();
    }

    /**
     * Returns the machine-readable string representation of this Scenario.
     * <p>The format of the string to return is</p>
     * <pre>
     *  {ScenarioName}
     *  Width:{Width}
     *  Height:{Height}
     *  Seed:{Seed}
     *  {Separator}
     *  {map}
     *  {Separator}
     *  {entity}
     *  {entity...}
     *  </pre>
     * Where:
     * <ul>
     * <li><code>{ScenarioName}</code> is the name of the scenario</li>
     * <li><code>{Width}</code> is the width of the scenario</li>
     * <li><code>{Height}</code> is the Height of the scenario</li>
     * <li><code>{Seed}</code> is the seed of the scenario</li>
     * <li>NOTE: There is no whitespace between the ':' and value for the above conditions</li>
     * <li><code>{Separator}</code> is a string of repeated equals <code>"="</code> characters where
     * the number of characters is equal to the width of the scenario <br>i.e. width == 5 -&gt;
     * separator == "====="</li>
     * <li><code>{map}</code> is the tile map grid where:
     *      <ul>
     *          <li>Each tile is represented by its <code>TileType.encode()</code>)</li>
     *          <li>A system-dependent line separator is added after <code>Width</code> characters
     *          are written <br>(See example below)</li>
     *      </ul>
     * </li>
     * <li><code>{entity}</code> is the <code>Entity.encode()</code> of each entity found in the map
     * where:
     *      <ul>
     *          <li>Each entity is added in the order it appears in the array by index (i.e. an
     *          entity inhabiting a tile with index 1 appears before an entity inhabiting a tile
     *          with index 4</li>
     *          <li>A system-dependent line separator is added after <code>entity</code> EXCEPT the
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
     *     <br> See <a href="../entities/Fauna.html#getName()"><code>Fauna.getName()</code></a></li>
     *     <li>The map is as shown in the save
     *     <br>Each Tile is represented by its <a href="../map/TileType.html#encode()">
     *         <code>TileType.encode()</code></a> value of its type</li>
     * </ul>
     * would be return the following string
     * <pre>
     *     Scenario X
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
     * @return encoded string representation of this Scenario
     */
    @Override
    public String encode() {
        StringJoiner ans = new StringJoiner(System.lineSeparator());
        ans.add(this.name);
        ans.add(String.format("Width:%d", this.width));
        ans.add(String.format("Height:%d", this.height));
        ans.add(String.format("Seed:%d", this.seed));
        ans.add("=".repeat(this.width));
        for (int h = 0; h < this.height; h++) {
            ans.add(Arrays.stream(
                            Arrays.copyOfRange(
                                    this.mapGrid,
                                    h * this.width,
                                    (h + 1) * this.width))
                    .map(tile -> tile.getType().encode())
                    .collect(Collectors.joining()));
        }
        ans.add("=".repeat(this.width));
        for (Tile tile : this.mapGrid) {
            try {
                Entity entity = tile.getContents();
                ans.add(entity.encode());
            } catch (NoSuchEntityException ignored) {
                // ignored
            }
        }
        return ans.toString();
    }
}
