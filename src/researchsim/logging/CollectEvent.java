package researchsim.logging;

import researchsim.entities.Entity;
import researchsim.entities.User;

import java.util.Arrays;

/**
 * The collection of an entity that implemented {@link researchsim.util.Collectable} by a
 * {@link User}.
 *
 * @ass2
 */
public class CollectEvent extends Event {
    /**
     * The entity the user is going to collect
     */
    private final Entity target;

    /**
     * Creates a new collect event, this is an event where a user collects research on an entity.
     *
     * @param user the user collecting a target
     * @param target the target entity that is being collected
     */
    public CollectEvent(User user, Entity target) {
        super(user, target.getCoordinate());
        this.target = target;
    }

    /**
     * Returns the target that was collected.
     *
     * @return event target
     */
    public Entity getTarget() {
        return this.target;
    }

    /**
     * Returns the string representation of the collect event.
     * <p>The format of the string to return is: </p>
     * <pre> user
     * COLLECTED
     * entity
     * -----</pre>
     * Where:
     * <ul>
     *   <li><code>user</code> is the <code>toString()</code> of the user that collected the
     *   <code>entity</code></li>
     *   <li><code>entity</code> is the <code>toString()</code> of the collected entity</li>
     * </ul>
     * <b>IMPORTANT:</b> The coordinate in the user string should be the coordinate that the
     * user was in when the event occurred and <b>NOT</b> its current coordinate
     * <p><b>ADDITIONALLY:</b> The coordinate in the entity string should be the coordinate that the
     * entity was in when the event occurred and <b>NOT</b> its current coordinate</p>
     * <p>Each entry should be separated by a system-dependent line separator.</p>
     * <p>For example:</p>
     * <pre> Dave [User] at (12,12)
     * COLLECTED
     * Dog [Fauna] at (11,12) [LAND]
     * -----</pre>
     * Note that there is no trailing newline.
     *
     * @return human-readable string representation of this collect event
     */
    @Override
    public String toString() {
        String coordinateRegex = "\\(\\d*,\\d*\\)";
        String userOriToString = this.getEntity().toString().split(coordinateRegex)[0]
                                + this.getInitialCoordinate().toString();
        String targetOriToString = this.target.toString().split(coordinateRegex)[0]
                                + this.getCoordinate().toString();

        return String.join(
                System.lineSeparator(),
                Arrays.asList(
                        userOriToString,
                        "COLLECTED",
                        targetOriToString,
                        "-".repeat(5)
                )
        );
    }
}
