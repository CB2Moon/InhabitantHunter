package researchsim.entities;

import researchsim.logging.CollectEvent;
import researchsim.map.Coordinate;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.Collectable;

/**
 * Flora is all the plant life present in a particular region or time, generally the naturally
 * occurring (indigenous) native plants.
 * Flora can be collected by the {@link User}.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 */
public class Flora extends Entity implements Collectable {
    /**
     * Creates a flora (plant) with a given size and coordinate.
     *
     * @param size       size associated with the plant
     * @param coordinate coordinate associated with the plant
     * @ass1
     */
    public Flora(Size size, Coordinate coordinate) {
        super(size, coordinate);
    }

    /**
     * Returns the human-readable name of this plant.
     * The name is determined by the following table.
     * <table border="1">
     *     <tr>
     *          <td colspan=2>Human-readable names</td>
     *     </tr>
     *     <tr>
     *         <td>SMALL</td>
     *         <td>Flower</td>
     *     </tr>
     *     <tr>
     *         <td>MEDIUM</td>
     *         <td>Shrub</td>
     *     </tr>
     *     <tr>
     *         <td>LARGE</td>
     *         <td>Sapling</td>
     *     </tr>
     *     <tr>
     *         <td>GIANT</td>
     *         <td>Tree</td>
     *     </tr>
     * </table>
     *
     * @return human-readable name
     * @ass1
     */
    @Override
    public String getName() {
        String name;
        switch (getSize()) {
            case SMALL:
                name = "Flower";
                break;
            case MEDIUM:
                name = "Shrub";
                break;
            case LARGE:
                name = "Sapling";
                break;
            case GIANT:
            default:
                name = "Tree";
        }
        return name;
    }

    /**
     * A <code>User</code> interacts and collects this plant.
     * Upon collection the following should occur:
     * <ul>
     *     <li>A <code>CollectEvent</code> should be created with the plant and coordinate.</li>
     *     <li>The Tile that the plant was occupying should now be unoccupied (empty).</li>
     * </ul>
     * <p>The user will gain a number of points for collecting this plant determined on its size.
     * </p>
     *
     * @param user the user that collects the entity.
     * @return points earned
     */
    @Override
    public int collect(User user) {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        scenario.getLog().add(new CollectEvent(user, this));
        scenario.getMapGrid()[this.getCoordinate().getIndex()].setContents(null);
        return this.getSize().points;
    }
}
