package researchsim.entities;

/**
 * This enum governs the base attributes of each entity.
 * An {@link Entity} will always have an associated size.
 * <p>
 * The characteristics of a Size include its Score (Points) earned for collecting and how far it
 * can move on the scenario map.
 * <table border="1">
 *      <caption>Enum definitions</caption>
 *     <tr>
 *         <th>Size</td>
 *         <th>Points</td>
 *         <th>Move Distance</td>
 *     </tr>
 *     <tr>
 *         <td>SMALL</td>
 *         <td>1</td>
 *         <td>4</td>
 *     </tr>
 *     <tr>
 *         <td>MEDIUM</td>
 *         <td>2</td>
 *         <td>3</td>
 *     </tr>
 *     <tr>
 *         <td>LARGE</td>
 *         <td>3</td>
 *         <td>2</td>
 *     </tr>
 *     <tr>
 *         <td>GIANT</td>
 *         <td>4</td>
 *         <td>1</td>
 *     </tr>
 * </table>
 * <p>
 * For this assignment you will need to add some values to the Java enum class, see the following
 * links.<br>
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html">
 * Java documentation - Recommended</a>
 * <br> OR <br>
 * <a href="https://www.baeldung.com/java-enum-values">Baeldung - Java enum values (section 1-4)</a>
 * <p>
 * <span style="font-size: 1.2em;">
 * <b>NOTE:</b> You do <b>not</b> need to implement the {@code values()} or
 * {@code valueOf(String)} methods as part of the assignment.
 * Their implementations are generated automatically by the compiler.
 * Also, you do <b>not</b> need to implement the {@code Serializable} or {@code Comparable}
 * interfaces, or extend {@code Enum}.
 * </span>
 *
 * @ass1_partial
 */
public enum Size {
    /**
     * An entity that is small in physical size.
     * <p>
     * Smaller than a human.</p>
     *
     * @ass1_partial
     */
    SMALL(1, 4),
    /**
     * An entity that is medium in physical size.
     * <p>
     * Larger than a desktop PC but smaller than a human.</p>
     *
     * @ass1_partial
     */
    MEDIUM(2, 3),
    /**
     * An entity that is large in physical size.
     * <p>
     * Larger than a human but smaller than a car.</p>
     *
     * @ass1_partial
     */
    LARGE(3, 2),
    /**
     * An entity that is ginormous in physical size.
     * <p>
     * Anything that is larger than a car.</p>
     * <p>
     * Ginormous (slang: meaning extremely large or huge)</p>
     *
     * @ass1_partial
     */
    GIANT(4, 1);
    /**
     * The number of points an User will gain after collecting this entity.
     * If it implements Collectable.
     */
    public final int points;

    /**
     * The <b>MAXIMUM</b> number of tiles an entity can traverse.
     * If it implements <code>Movable</code>.
     */
    public final int moveDistance;

    Size(int points, int moveDistance) {
        this.points = points;
        this.moveDistance = moveDistance;
    }
}
