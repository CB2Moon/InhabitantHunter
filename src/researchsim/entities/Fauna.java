package researchsim.entities;

import researchsim.logging.CollectEvent;
import researchsim.logging.MoveEvent;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.Collectable;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.util.Movable;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Fauna is all the animal life present in a particular region or time.
 * Fauna can move around the scenario and be collected by the {@link User}.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 * @ass1_test
 */
public class Fauna extends Entity implements Movable, Collectable {

    /**
     * The habitat associated with the animal.
     * That is, what tiles an animal can exist in.
     */
    private final TileType habitat;

    /**
     * Creates a fauna (Animal) with a given size, coordinate and habitat.
     *
     * @param size       size associated with the animal
     * @param coordinate coordinate associated with the animal
     * @param habitat    habitat tiles associated with the animal
     * @throws IllegalArgumentException if habitat is not {@link TileType#LAND} or
     *                                  {@link TileType#OCEAN}
     * @ass1
     */
    public Fauna(Size size, Coordinate coordinate, TileType habitat)
            throws IllegalArgumentException {
        super(size, coordinate);
        if (habitat != TileType.LAND && habitat != TileType.OCEAN) {
            throw new IllegalArgumentException("Animal was created with a bad habitat: " + habitat);
        }
        this.habitat = habitat;
    }

    /**
     * Returns the animal's habitat.
     *
     * @return animal's habitat
     * @ass1
     */
    public TileType getHabitat() {
        return habitat;
    }

    /**
     * Returns the human-readable name of this animal.
     * The name is determined by the following table.
     * <p>
     * <table border="1">
     *     <caption>Human-readable names</caption>
     *     <tr>
     *         <td rowspan="2" colspan="2" style="background-color:#808080">&nbsp;</td>
     *         <td colspan="3">Habitat</td>
     *     </tr>
     *     <tr>
     *         <td>LAND</td>
     *         <td>OCEAN</td>
     *     </tr>
     *     <tr>
     *         <td rowspan="4">Size</td>
     *         <td>SMALL</td>
     *         <td>Mouse</td>
     *         <td>Crab</td>
     *     </tr>
     *     <tr>
     *         <td>MEDIUM</td>
     *         <td>Dog</td>
     *         <td>Fish</td>
     *     </tr>
     *     <tr>
     *         <td>LARGE</td>
     *         <td>Horse</td>
     *         <td>Shark</td>
     *     </tr>
     *     <tr>
     *         <td>GIANT</td>
     *         <td>Elephant</td>
     *         <td>Whale</td>
     *     </tr>
     * </table>
     * <p>
     * e.g. if this animal is {@code MEDIUM} in size and has a habitat of {@code LAND} then its
     * name would be {@code "Dog"}
     *
     * @return human-readable name
     * @ass1
     */
    @Override
    public String getName() {
        String name;
        switch (getSize()) {
            case SMALL:
                name = habitat == TileType.LAND ? "Mouse" : "Crab";
                break;
            case MEDIUM:
                name = habitat == TileType.LAND ? "Dog" : "Fish";
                break;
            case LARGE:
                name = habitat == TileType.LAND ? "Horse" : "Shark";
                break;
            case GIANT:
            default:
                name = habitat == TileType.LAND ? "Elephant" : "Whale";
        }
        return name;
    }

    /**
     * Returns the human-readable string representation of this animal.
     * <p>
     * The format of the string to return is:
     * <pre>name [Fauna] at coordinate [habitat]</pre>
     * Where:
     * <ul>
     *   <li>{@code name} is the animal's human-readable name according to {@link #getName()}</li>
     *   <li>{@code coordinate} is the animal's associated coordinate in human-readable form</li>
     *   <li>{@code habitat} is the animal's associated habitat</li>
     *
     * </ul>
     * For example:
     *
     * <pre>Dog [Fauna] at (2,5) [LAND]</pre>
     *
     * @return human-readable string representation of this animal
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s [%s]",
                super.toString(),
                this.habitat);
    }

    /**
     * Returns the machine-readable string representation of this animal.
     * <p>
     * The format of the string to return is
     * </p><pre>Fauna-size-coordinate-habitat</pre>
     * Where:
     * <ul>
     *   <li><code>size</code> is the animal's associated size</li>
     *   <li><code>coordinate</code> is the encoding of the animal's associated coordinate</li>
     *   <li><code>habitat</code> is the animal's associated habitat</li>
     * </ul>
     * For example:
     * <pre>Fauna-SMALL-4,6-LAND</pre>
     *
     * @return encoded string representation of this animal
     */
    @Override
    public String encode() {
        return String.format(
                "%s-%s",
                super.encode(),
                this.habitat.toString());
    }

    /**
     * Returns the hash code of this animal.
     * Two animals that are equal according to the
     * equals(Object) method should have the same hash code.
     *
     * @return hash code of this animal.
     */
    @Override
    public int hashCode() {
        return String.format(
                "%d%s",
                super.hashCode(),
                this.habitat
        ).hashCode();
    }

    /**
     * Returns true if and only if this animal is equal to the other given object.
     * For two animals to be equal, they must have the same size, coordinate and habitat.
     *
     * @param other the reference object with which to compare
     * @return true if this animal is the same as the other argument; false otherwise
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && (this.habitat == ((Fauna) other).habitat);
    }

    /**
     * A <code>User</code> interacts and collects this animal. <br>
     * Upon collection the following should occur:
     * <ul>
     *     <li>A <code>CollectEvent</code> should be created with the animal and coordinate.</li>
     *     <li>The Tile that the animal was occupying should now be unoccupied (empty).</li>
     *     <li>The animal should be removed from the current scenario's animal controller.</li>
     * </ul>
     * <p>The given user will gain a number of points for collecting this animal. This value is
     * determined by the animals size.</p>
     *
     * @param user the user that collects the entity.
     * @return points earned
     */
    @Override
    public int collect(User user) {
        // Log
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        scenario.getLog().add(new CollectEvent(user, this));
        // Empty the animal tile
        scenario.getMapGrid()[this.getCoordinate().getIndex()].setContents(null);
        // remove from controller
        scenario.getController().removeAnimal(this);
        return this.getSize().points;
    }

    /**
     * Returns a <code>List</code> of all the possible coordinates that this animal can move to.
     * <p>The possible coordinates that this animal can move to are defined as: <br>
     * Any Coordinate in <code>Movable.checkRange(int, Coordinate)</code>
     * (<code>checkRange(move distance, current coordinate)</code>) that this animal can move to
     * (See <code>canMove(Coordinate)</code>).<br>
     * Any <code>CoordinateOutOfBoundsException</code>'s thrown by <code>canMove(Coordinate)</code>
     * are squashed.</p>
     * <p>The order of the returned coordinates does not matter.</p>
     *
     * @return list of possible movements
     */
    @Override
    public List<Coordinate> getPossibleMoves() {
        List<Coordinate> range = this.checkRange(this.getSize().moveDistance, this.getCoordinate());
        return range.stream().filter(coordinate -> {
            try {
                return this.canMove(coordinate);
            } catch (CoordinateOutOfBoundsException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Moves the animal to the new coordinate.
     * <p>The Tile that the animal moves to should now be occupied by this animal.</p>
     * <p>The tile that the animal moves from (the existing coordinate) should now have no occupant.
     * </p>
     * <p>A <code>MoveEvent</code> should be created and added to the current scenario logger.</p>
     *
     * @param coordinate The new coordinate to move to
     */
    @Override
    public void move(Coordinate coordinate) {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        scenario.getLog().add(new MoveEvent(this, coordinate));
        scenario.getMapGrid()[this.getCoordinate().getIndex()].setContents(null);
        scenario.getMapGrid()[coordinate.getIndex()].setContents(this);
        this.setCoordinate(coordinate);
    }

    /**
     * Determines if the animal can move to the new coordinate.
     * <p>An animal can move to the new coordinate if <b>ALL</b> of the following
     * conditions are satisfied:</p>
     * <ul>
     *     <li>The new coordinate must be different from the current coordinate.</li>
     *     <li>The coordinate given is on the current scenario map
     *     (See <code>ScenarioManager</code>).</li>
     *     <li>The distance from the given coordinate to the current coordinate is not greater
     *     than the distance this animal can move (<code>Size.moveDistance</code>)</li>
     *     <li>If the animal's habitat is <code>OCEAN</code> then the tile at the
     *     coordinate must be <code>OCEAN</code></li>
     *     <li>If the animal's habitat is <code>LAND</code> then the tile at the
     *     coordinate must <b>NOT</b> be <code>OCEAN</code></li>
     *     <li>The tile at the coordinate is not already occupied</li>
     *     <li>The animal has an unimpeded path (meaning all the above conditions are true) for
     *     each tile it must traverse to reach the destination coordinate</li>
     * </ul>
     * <p>
     * The animal can only turn once.<br> i.e. can not move diagonally but rather
     * n tiles in the horizontal plane followed by m tiles in the vertical plane (or vice
     * versa). Similar to how a knight moves in chess.
     * </p><p>
     * For example:<br>
     * If the animal wants to move from (0,0) to (2,1) on the following encoded map.
     * </p><pre> LLL
     * LSL
     * LLL
     * </pre>
     * (The above map is not possible to be created normally as the minimum dimensions are
     * <code>5 x 5</code>)<br>
     * It would have to be able to move to all the following coordinates:
     * <pre>[(1,0),(2,0),(2,1)]</pre>
     * OR
     * <pre>[(0,1),(1,1),(2,1)]</pre>
     * @param coordinate coordinate to check
     * @return true if the above conditions are satisfied else false
     * @throws CoordinateOutOfBoundsException if the coordinate given is out of bounds
     */
    @Override
    public boolean canMove(Coordinate coordinate) throws CoordinateOutOfBoundsException {
        if (!coordinate.isInBounds()) {
            throw new CoordinateOutOfBoundsException();
        }
        if (this.getCoordinate().equals(coordinate)) {
            return false;
        }
        return this.checkTraversalHorizontalFirst(coordinate)
                || this.checkTraversalVerticalFirst(coordinate);
    }

    /**
     * Determines if the animal can move to this specific coordinate assuming that
     * there exists an unimpeded path for each tile it must traverse to reach the
     * destination coordinate
     * <p>An animal can move to the new coordinate if <b>ALL</b> of the following
     * conditions are satisfied:</p>
     * <ul>
     *     <li>The new coordinate must be different from the current coordinate.</li>
     *     <li>The distance from the given coordinate to the current coordinate is not greater
     *     than the distance this animal can move (<code>Size.moveDistance</code>)</li>
     *     <li>If the animal's habitat is <code>OCEAN</code> then the tile at the
     *     coordinate must be <code>OCEAN</code></li>
     *     <li>If the animal's habitat is <code>LAND</code> then the tile at the
     *     coordinate must <b>NOT</b> be <code>OCEAN</code></li>
     *     <li>The tile at the coordinate is not already occupied</li>
     * </ul>
     *
     * @param coordinate the destination coordinate to check
     * @return true if the above conditions are satisfied else false
     * @requires the coordinate is in the current scenario
     */
    private boolean checkTile(Coordinate coordinate) {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        Tile targetTile = scenario.getMapGrid()[coordinate.getIndex()];
        Coordinate theDistance = this.getCoordinate().distance(coordinate);
        if (theDistance.getAbsX() + theDistance.getAbsY() > this.getSize().moveDistance) {
            return false;
        }
        if (this.getHabitat() == TileType.OCEAN
                && targetTile.getType() != TileType.OCEAN) {
            return false;
        }
        if (this.getHabitat() == TileType.LAND
                && targetTile.getType() == TileType.OCEAN) {
            return false;
        }
        return !targetTile.hasContents();
    }

    /**
     * Determine if the path, moving horizontally first, to the destination is unimpeded,
     * meaning for each tile in the path, the <code>checkTile()</code> is true.
     * The animal can only turn once.
     *
     * @param target the destination coordinate to move
     * @return true if the animal can move to the destination through this path
     * @requires the destination is in the current scenario
     */
    private boolean checkTraversalHorizontalFirst(Coordinate target) {
        Coordinate theDistance = this.getCoordinate().distance(target);
        Coordinate traverse;
        int incrementX = Integer.signum(theDistance.getX());
        int incrementY = Integer.signum(theDistance.getY());
        if (incrementX != 0) {
            for (int x = incrementX; Math.abs(x) <= theDistance.getAbsX(); x += incrementX) {
                traverse = this.getCoordinate().translate(x, 0);
                if (!this.checkTile(traverse)) {
                    return false;
                }
            }
        }
        if (incrementY != 0) {
            for (int y = incrementY; Math.abs(y) <= theDistance.getAbsY(); y += incrementY) {
                traverse = this.getCoordinate().translate(theDistance.getX(), y);
                if (!this.checkTile(traverse)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determine if the path, moving vertically first, to the destination is unimpeded,
     * meaning for each tile in the path, the <code>checkTile()</code> is true.
     * The animal can only turn once.
     *
     * @param target the destination coordinate to move
     * @return true if the animal can move to the destination through this path
     * @requires the destination is in the current scenario
     */
    private boolean checkTraversalVerticalFirst(Coordinate target) {
        Coordinate theDistance = this.getCoordinate().distance(target);
        Coordinate traverse;
        int incrementX = Integer.signum(theDistance.getX());
        int incrementY = Integer.signum(theDistance.getY());
        if (incrementY != 0) {
            for (int y = incrementY; Math.abs(y) <= theDistance.getAbsY(); y += incrementY) {
                traverse = this.getCoordinate().translate(0, y);
                if (!this.checkTile(traverse)) {
                    return false;
                }
            }
        }
        if (incrementX != 0) {
            for (int x = incrementX; Math.abs(x) <= theDistance.getAbsX(); x += incrementX) {
                traverse = this.getCoordinate().translate(x, theDistance.getY());
                if (!this.checkTile(traverse)) {
                    return false;
                }
            }
        }
        return true;
    }
}
