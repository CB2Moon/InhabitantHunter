package researchsim.logging;

import researchsim.map.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A detailed log that contains a record of {@link Event}s and contains some event statistics.
 *
 * @ass2
 */
public class Logger {
    /**
     * The number of entities that have been collected.
     */
    private int entitiesCollected;

    /**
     * The number of tiles that have been travelled
     */
    private int tilesTraversed;

    /**
     * The number of points that have been earned
     */
    private int pointsEarned;

    /**
     * All the events that have been logged.
     */
    private final List<Event> events;

    /**
     * Creates a new logger to maintain a list of events that occur in a scenario.
     * <p>A logger keeps track of the following statistics (starting at 0):</p>
     * <ul>
     *     <li>The number of entities that have been collected</li>
     *     <li>The number of tiles that have been travelled</li>
     *     <li>The number of points that have been earned</li>
     * </ul>
     */
    public Logger() {
        this.entitiesCollected = 0;
        this.tilesTraversed = 0;
        this.pointsEarned = 0;
        this.events = new ArrayList<>();
    }

    /**
     * Returns how many entities have been collected by a user.
     *
     * @return entities collected
     */
    public int getEntitiesCollected() {
        return entitiesCollected;
    }

    /**
     * Returns how many tiles have been traversed by entities.
     *
     * @return tiles traversed
     */
    public int getTilesTraversed() {
        return tilesTraversed;
    }

    /**
     * Returns the number of points earned in a scenario.
     *
     * @return points earned
     */
    public int getPointsEarned() {
        return pointsEarned;
    }

    /**
     * Returns all the events that have been logged.<br>
     * Adding or removing elements from the returned
     * list should not affect the original list.
     *
     * @return all events that have been logged
     */
    public List<Event> getEvents() {
        return new ArrayList<>(this.events);
    }

    /**
     * Adds an event to the log.
     *  <p>If an event is a <code>CollectEvent</code> then the number of entities collected should
     *  be incremented. Additionally, the number of points for collecting that entity should be
     *  recorded.</p>
     *  <p>If an event is a <code>MoveEvent</code> then the number of tiles traversed should be
     *  incremented by the distance travelled in this event.</p>
     * @param event the new event
     */
    public void add(Event event) {
        if (event instanceof CollectEvent) {
            this.entitiesCollected++;
            this.pointsEarned += event.getEntity().getSize().points;
        } else if (event instanceof MoveEvent) {
            Coordinate dist = event.getInitialCoordinate().distance(event.getCoordinate());
            this.tilesTraversed += dist.getAbsX() + dist.getAbsY();
        }
        this.events.add(event);
    }

    /**
     * Returns the string representation of the event log.
     * The format of the
     * string to return is:
     * <pre> logEntry
     * logEntry
     * ...</pre>
     * Where:
     * <ul>
     *   <li><code>logEntry</code> is the <code>Event.toString()</code> of an event in the log</li>
     * </ul>
     * <b>IMPORTANT:</b>
     * The log entries should appear in the order in which they were added.
     * Additionally, each entry should be separated by a system-dependent line separator.
     * <p>For example:</p>
     * <pre> Dave [User] at (13,13)
     * MOVED TO (12,12)
     * -----
     * Dave [User] at (12,12)
     * COLLECTED
     * Dog [Fauna] at (11,12) [LAND]
     * -----
     * </pre>
     * Note that there is no trailing newline.
     * @return human-readable string representation of log
     */
    @Override
    public String toString() {
        return this.events.stream()
                .map(Event::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

}
