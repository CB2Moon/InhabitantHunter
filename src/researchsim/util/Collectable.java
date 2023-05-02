package researchsim.util;

import researchsim.entities.User;

/**
 * Denotes an entity that can be collected by the {@link researchsim.entities.User} in the
 * simulation.
 *
 * @ass2
 */
public interface Collectable {
    /**
     * A User interacts and collects this instance.
     *
     * @param user the user that collects the entity.
     * @return the amount of points gained by that user
     */
    int collect(User user);
}
