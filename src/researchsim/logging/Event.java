package researchsim.logging;

import researchsim.entities.Entity;
import researchsim.map.Coordinate;

/**
 * An event that occurs in the {@link researchsim.scenario.Scenario}.
 * An event occurs between an {@link Entity} and a {@link Coordinate}.
 *
 * @ass1
 */
public abstract class Event {

    /**
     * The entity actioning the event
     */
    private final Entity entity;
    /**
     * The initial coordinate when the event occurred
     */
    private final Coordinate initialCoordinate;
    /**
     * The coordinate being actioned upon
     */
    private final Coordinate coordinate;

    /**
     * Creates a new Event of an entity completing some action on a coordinate.
     * The coordinate that the event was initiated on (where the entity is) is also recorded.
     *
     * @param entity     the entity completing some action
     * @param coordinate the coordinate that is being actioned (the target of the event)
     * @ass1
     */
    public Event(Entity entity, Coordinate coordinate) {
        this.entity = entity;
        this.initialCoordinate = entity.getCoordinate();
        this.coordinate = coordinate;
    }

    /**
     * Returns the target coordinate of the event.
     *
     * @return target coordinate
     * @ass1
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the coordinate that the event was initiated from (the coordinate the entity was in
     * when this event was created).
     *
     * @return initiating coordinate
     * @ass1
     */
    public Coordinate getInitialCoordinate() {
        return initialCoordinate;
    }

    /**
     * Returns the entity that initiated the event.
     *
     * @return initiating entity
     * @ass1
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Returns the string representation of the Event.
     *
     * @return string form of the event
     * @ass1
     */
    public abstract String toString();

}
