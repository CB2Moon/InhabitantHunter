package researchsim.logging;

import researchsim.entities.Entity;
import researchsim.map.Coordinate;

import java.util.StringJoiner;

/**
 * The movement of an entity from one coordinate to another (new) coordinate.
 *
 * @ass1
 */
public class MoveEvent extends Event {

    /**
     * Creates a new move event.
     * This is an event where an entity moves to another coordinate.
     *
     * @param entity     the entity that is moving
     * @param coordinate the coordinate that the entity is moving to
     * @ass1
     */
    public MoveEvent(Entity entity, Coordinate coordinate) {
        super(entity, coordinate);
    }

    /**
     * Returns the string representation of the move event.
     * <p>
     * The format of the string to return is:
     * <pre>
     * entity
     * MOVED TO newCoordinate
     * -----</pre>
     * Where:
     * <ul>
     *   <li>{@code entity} is the {@link #toString()} of the entity that has moved
     *   </li>
     *   <li>{@code newCoordinate} is the new coordinate of the above mentioned entity</li>
     * </ul>
     * <p>
     * <b>IMPORTANT:</b> The coordinate in the {@code entity} string should be the coordinate that
     * the entity was in when the event occurred and <b>NOT</b> its current coordinate.
     * <p>
     * Each entry should be separated by a system-dependent line separator.
     * <p>
     * For example:
     *
     * <pre>
     * Dog [Fauna] at (1,3) [LAND]
     * MOVED TO (2,4)
     * -----</pre>
     *
     * @return human-readable string representation of this move event
     * @ass1
     * @see System#lineSeparator()
     */
    @Override
    public String toString() {
        String entity = getEntity().toString();
        StringJoiner result = new StringJoiner(System.lineSeparator());
        result.add(
            entity.replace(getEntity().getCoordinate().toString(),
                getInitialCoordinate().toString()));
        result.add("MOVED TO " + getCoordinate().toString());
        result.add("-".repeat(5));

        return result.toString();
    }
}
