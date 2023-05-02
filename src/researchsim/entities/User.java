package researchsim.entities;

import researchsim.logging.MoveEvent;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.Collectable;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.util.Movable;
import researchsim.util.NoSuchEntityException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User is the player controlled character in the simulation.
 * A user can {@code collect} any class that implements the {@link researchsim.util.Collectable}
 * interface.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass2
 * @ass2_test
 */
public class User extends Entity implements Movable {
    /**
     * The name of this user
     */
    private final String name;

    /**
     * Creates a user with a given coordinate and name.
     * A user is a MEDIUM sized entity.
     *
     * @param coordinate coordinate associated with the user
     * @param name       the name of this user
     */
    public User(Coordinate coordinate, String name) {
        super(Size.MEDIUM, coordinate);
        this.name = name;
    }

    /**
     * Returns the name of this user.
     *
     * @return human-readable name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the machine-readable string representation of this user.
     * <p>The format of the string to return is</p>
     * <pre>User-coordinate-name</pre>
     * Where:
     * <ul>
     *   <li><code>coordinate</code> is the encoding of the user's associated coordinate</li>
     *   <li><code>name</code> is the user's name</li>
     * </ul>
     * For example:
     * <pre>User-3,5-Bob</pre>
     *
     * @return encoded string representation of this user
     */
    @Override
    public String encode() {
        return String.format("%s-%s-%s",
                this.getClass().getSimpleName(),
                this.getCoordinate().encode(),
                this.name);
    }

    /**
     * Returns the hash code of this user.<br>
     * Two users that are equal according to the equals(Object)
     * method should have the same hash code.
     *
     * @return hash code of this user.
     */
    @Override
    public int hashCode() {
        return String.format("%s%s",
                this.name,
                this.getCoordinate().toString()).hashCode();
    }

    /**
     * Returns true if and only if this user is equal to the other given object.<br>
     * For two users to be equal, they must have the same coordinate and name.
     *
     * @param other the reference object with which to compare
     * @return true if this user is the same as the other argument; false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        return this.name.equals(((User) other).name);
    }

    /**
     * Returns a <code>List</code> of all the possible coordinates that this user can move to.
     * <p>
     * The possible coordinates that this User can move to are defined as: <br>
     * Any Coordinate in <code>Movable.checkRange(int, Coordinate)</code>
     * (<code>checkRange(move distance, current coordinate)</code>) that this user can move to (See
     * <code>canMove(Coordinate)</code>).<br>
     * Any <code>CoordinateOutOfBoundsException</code>'s thrown by <code>canMove(Coordinate)</code>
     * are squashed.
     * </p><p>
     * The order of the returned coordinates does not matter.</p>
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
     * Returns a <code>List</code> of all the possible coordinates that this user can collect from.
     * A user can only collect from its immediate neighbouring tiles (only 1 tile away) that:
     * <ul>
     * <li>Are in the bounds of the current scenario</li>
     * <li>Have contents (<code>Tile.hasContents()</code>)</li>
     * <li>If the contents of that tile implement the <code>Collectable</code> interface</li>
     * </ul>
     * <p>
     * The User can collect from ANY <code>TileType</code>.
     * </p><p>
     * The order of the returned coordinates does not matter.</p>
     *
     * @return list of possible collections
     */
    public List<Coordinate> getPossibleCollection() {
        List<Coordinate> range = new ArrayList<>();
        range.add(this.getCoordinate().translate(-1, 0));
        range.add(this.getCoordinate().translate(0, -1));
        range.add(this.getCoordinate().translate(0, 1));
        range.add(this.getCoordinate().translate(1, 0));

        Tile[] mapGrid = ScenarioManager.getInstance().getScenario().getMapGrid();
        return range.stream().filter(coordinate -> {
            if (!coordinate.isInBounds()) {
                return false;
            }
            try {
                return mapGrid[coordinate.getIndex()].getContents() instanceof Collectable;
            } catch (NoSuchEntityException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Collects an entity from the specified coordinate.
     * <p>If the entity at the given coordinate does not implement the <code>Collectable</code>
     * interface then no action is taken.</p>
     * <p>This method should collect the entity even if the Coordinate is more than 1 tile away
     * (<code>getPossibleMoves()</code>).</p>
     *
     * @param coordinate the coordinate we are collecting from
     * @throws NoSuchEntityException          if the given coordinate is empty
     * @throws CoordinateOutOfBoundsException if the given coordinate is not in the map bounds.
     */
    public void collect(Coordinate coordinate)
            throws NoSuchEntityException, CoordinateOutOfBoundsException {
        Tile[] mapGrid = ScenarioManager.getInstance().getScenario().getMapGrid();
        if (!coordinate.isInBounds()) {
            throw new CoordinateOutOfBoundsException();
        }
        Entity targetTileContent = mapGrid[coordinate.getIndex()].getContents();
        if (targetTileContent instanceof Collectable) {
            ((Collectable) targetTileContent).collect(this);
        }
    }

    /**
     * Moves the user to the new coordinate. <br>
     * The Tile that the user moves to should now be occupied by this user. <br>
     * The tile that the user moves from (the existing coordinate) should now have no occupant. <br>
     * A <code>MoveEvent</code> should be created with the animal and new coordinate.
     * <p>
     * If the new coordinate has an entity that implements <code>Collectable</code> then this entity
     * should be collected with its implementation of <code>collect(Coordinate)</code>. <br>The move
     * event should be
     * added to the log BEFORE calling <code>collect(Coordinate)</code>.<br>Any exceptions that
     * might be raised from <code>collect(Coordinate)</code> should be suppressed.</p>
     *
     * @param coordinate The new coordinate to move to
     * @requires canMove(Coordinate) == true
     * @ensures the state of the tile that the user is inhabiting, the tile that the user
     * is going to are both updated, the event is logged
     */
    @Override
    public void move(Coordinate coordinate) {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        scenario.getLog().add(new MoveEvent(this, coordinate));
        try {
            this.collect(coordinate);
        } catch (NoSuchEntityException | CoordinateOutOfBoundsException ignored) {
            // since canMove() is true, ignore empty tile can only perform movement
        } finally {
            scenario.getMapGrid()[this.getCoordinate().getIndex()].setContents(null);
            scenario.getMapGrid()[coordinate.getIndex()].setContents(this);
            this.setCoordinate(coordinate);
        }
    }

    /**
     * Determines if the user can move to the specified coordinate. <p>
     * A User can move to the new coordinate if <b>ALL</b> of the following
     * conditions are satisfied:
     * </p><ul>
     * <li>The new coordinate must be different from the current coordinate.</li>
     * <li>The coordinate given is on the scenario map.</li>
     * <li>The distance from the given coordinate to the current coordinate is not greater
     * than four (4)</li>
     * <li>The tile at the coordinate is <b>NOT</b> <code>OCEAN</code> or <code>MOUNTAIN</code></li>
     * <li>The entity has an unimpeded path (meaning all the above conditions are true) for
     * each tile it must traverse to reach the destination coordinate</li>
     * </ul>
     * <p>
     * A user can only turn once, i.e. can not move diagonally but rather
     * n tiles in the horizontal plane followed by m tiles in the vertical plane (or vice
     * versa). Similar to how a knight moves in chess.
     * </p><p>
     * For example:<br>
     * If the user wants to move from (0,0) to (2,1) on the following encoded map.
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
     *
     * @param coordinate coordinate to check
     * @return true if the instance can move to the specified coordinate else false
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
        if (!this.checkTile(coordinate, true)) {
            return false;
        }
        return this.checkTraversalHorizontalFirst(coordinate)
                || this.checkTraversalVerticalFirst(coordinate);
    }

    /**
     * Determines if the user can move to this specific coordinate assuming that
     * there exists an unimpeded path for each tile it must traverse to reach the
     * destination coordinate
     * <p>A user can move to the new coordinate if <b>ALL</b> of the following
     * conditions are satisfied:</p>
     * <ul>
     *     <li>The new coordinate must be different from the current coordinate.</li>
     *     <li>The distance from the given coordinate to the current coordinate is not greater
     *     than 4</li>
     *     <li>The tile at the coordinate is <b>NOT</b> <code>OCEAN</code> or <code>MOUNTAIN</code>
     *     </li>
     * </ul>
     *
     * @param coordinate     the destination coordinate to check
     * @param canHaveContent true if the target coordinate is allowed to have entity that implements
     *                       <code>Collectable</code>
     * @return true if the above conditions are satisfied else false
     * @requires the coordinate is in the current scenario
     */
    private boolean checkTile(Coordinate coordinate, boolean canHaveContent) {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        Tile targetTile = scenario.getMapGrid()[coordinate.getIndex()];
        Coordinate theDistance = this.getCoordinate().distance(coordinate);
        if (theDistance.getAbsX() + theDistance.getAbsY() > 4) {
            return false;
        }
        if (targetTile.getType() == TileType.OCEAN
                || targetTile.getType() == TileType.MOUNTAIN) {
            return false;
        }
        try {
            Entity content = targetTile.getContents();
            if (!canHaveContent) {
                return false;
            }
            return content instanceof Collectable;
        } catch (NoSuchEntityException e) {
            return true; // empty tile
        }
    }

    /**
     * Determine if the path, moving horizontally first, to the destination is unimpeded,
     * meaning for each tile in the path, the <code>checkTile()</code> is true.
     * The user can only turn once.
     *
     * @param target the destination coordinate to move
     * @return true if the user can move to the destination through this path
     * @requires the destination is in the current scenario
     */
    private boolean checkTraversalHorizontalFirst(Coordinate target) {
        Coordinate theDistance = this.getCoordinate().distance(target);
        int incrementX = Integer.signum(theDistance.getX());
        int incrementY = Integer.signum(theDistance.getY());
        if (incrementX != 0) {
            for (int x = incrementX; Math.abs(x) <= theDistance.getAbsX(); x += incrementX) {
                Coordinate traverse = this.getCoordinate().translate(x, 0);
                if (!this.checkTile(traverse, false)) {
                    return false;
                }
            }
        }
        if (incrementY != 0) {
            for (int y = incrementY; Math.abs(y) < theDistance.getAbsY(); y += incrementY) {
                Coordinate traverse = this.getCoordinate().translate(theDistance.getX(), y);
                if (!this.checkTile(traverse, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determine if the path, moving vertically first, to the destination is unimpeded,
     * meaning for each tile in the path, the <code>checkTile()</code> is true.
     * The user can only turn once.
     *
     * @param target the destination coordinate to move
     * @return true if the user can move to the destination through this path
     * @requires the destination is in the current scenario
     */
    private boolean checkTraversalVerticalFirst(Coordinate target) {
        Coordinate theDistance = this.getCoordinate().distance(target);
        int incrementX = Integer.signum(theDistance.getX());
        int incrementY = Integer.signum(theDistance.getY());
        if (incrementY != 0) {
            for (int y = incrementY; Math.abs(y) <= theDistance.getAbsY(); y += incrementY) {
                Coordinate traverse = this.getCoordinate().translate(0, y);
                if (!this.checkTile(traverse, false)) {
                    return false;
                }
            }
        }
        if (incrementX != 0) {
            for (int x = incrementX; Math.abs(x) < theDistance.getAbsX(); x += incrementX) {
                Coordinate traverse = this.getCoordinate().translate(x, theDistance.getY());
                if (!this.checkTile(traverse, false)) {
                    return false;
                }
            }
        }
        return true;
    }
}
