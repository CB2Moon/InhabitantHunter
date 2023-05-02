package researchsim.map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import researchsim.entities.Fauna;
import researchsim.entities.Size;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;

import java.util.Arrays;

import static org.junit.Assert.*;


public class CoordinateTest {

    private Coordinate coordinate11;
    private Coordinate coordinate11Dup;
    private Coordinate coordinate21;
    private Coordinate coordinateneg21;
    private Scenario scenario1;

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a 5x5 map of LAND. A Seed of 0.
     *
     * @param name of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[])
     */
    public static Scenario createSafeTestScenario(String name) {
        return createSafeTestScenario(name, new TileType[]{
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND
        });
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a 5x5 map with the array of tiles based on the array provided. A
     * Seed of 0.
     *
     * @param name  of the scenario
     * @param tiles the map of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[], int, int)
     */
    public static Scenario createSafeTestScenario(String name, TileType[] tiles) {
        return createSafeTestScenario(name, tiles, 5, 5);
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has an n x m map with the array of LAND tiles. A
     * Seed of 0.
     *
     * @param name   of the scenario
     * @param width  the width of the scenario
     * @param height the height of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[], int, int)
     */
    public static Scenario createSafeTestScenario(String name, int width, int height) {
        int size = width * height;
        TileType[] tiles = new TileType[size];
        Arrays.fill(tiles, 0, size, TileType.LAND);
        return createSafeTestScenario(name, tiles, width, height);
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a n x m map with the array of tiles based on the array provided. A
     * Seed of 0.
     *
     * @param name   of the scenario
     * @param tiles  the map of the scenario
     * @param width  the width of the scenario
     * @param height the height of the scenario
     * @return generated scenario
     */
    public static Scenario createSafeTestScenario(String name, TileType[] tiles,
                                                  int width, int height) {
        Scenario s = new Scenario(name, width, height, 0);
        Tile[] map = Arrays.stream(tiles).map(Tile::new).toArray(Tile[]::new);
        try {
            s.setMapGrid(map);
        } catch (CoordinateOutOfBoundsException error) {
            fail("Failed to update a scenario map for test: " + name + "\n "
                    + error.getMessage());
        }
        ScenarioManager.getInstance().addScenario(s);
        try {
            ScenarioManager.getInstance().setScenario(name);
        } catch (BadSaveException error) {
            fail("Failed to update a scenario map for test: " + name + "\n "
                    + error.getMessage());
        }
        return s;
    }

    @Before
    public void setUp() throws Exception {
        scenario1 = createSafeTestScenario("scenario1", 12, 6);
        coordinate11 = new Coordinate(1, 1);
        coordinate11Dup = new Coordinate(1, 1);
        coordinate21 = new Coordinate(2, 1);
        coordinateneg21 = new Coordinate(-2, -1);
    }

    @After
    public void tearDown() throws Exception {
        ScenarioManager.getInstance().reset();
    }

    @Test
    public void testDefaultConstructor() {
        Coordinate origin = new Coordinate();
        assertEquals("Incorrect value was returned.", 0, origin.getX());
        assertEquals("Incorrect value was returned.", 0, origin.getY());
    }

    @Test
    public void testIndexConstructor() {
        createSafeTestScenario("testIndexConstructor", 5, 5);
        Coordinate origin = new Coordinate(12);
        assertEquals("Incorrect value was returned.", 2, origin.getX());
        assertEquals("Incorrect value was returned.", 2, origin.getY());
    }

    @Test
    public void testGetX() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getX());
        assertEquals("Incorrect value was returned.", -2, coordinateneg21.getX());

    }

    @Test
    public void testGetAbsX() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getAbsX());
        assertEquals("Incorrect value was returned.",
                2, coordinateneg21.getAbsX());
    }

    @Test
    public void testGetY() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getY());
        assertEquals("Incorrect value was returned.", -1, coordinateneg21.getY());
    }

    @Test
    public void testGetAbsY() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getAbsY());
        assertEquals("Incorrect value was returned.",
                1, coordinateneg21.getAbsY());
    }

    @Test
    public void testGetIndex() {
        createSafeTestScenario("testGetIndex", 5, 5);
        assertEquals("Incorrect value was returned.",
                6, coordinate11.getIndex());
        assertEquals("Incorrect value was returned.",
                7, coordinate21.getIndex());
        assertEquals("Incorrect value was returned.",
                -7, coordinateneg21.getIndex());
    }

    @Test
    public void testIsInBounds() {
        createSafeTestScenario("testIsInBounds", 10, 10);
        assertTrue("Incorrect value was returned.", coordinate11.isInBounds());
        assertFalse("Incorrect value was returned.", coordinateneg21.isInBounds());
    }

    @Test
    public void testConvert() {
        createSafeTestScenario("testConvert", 10, 10);
        assertEquals("Incorrect value was returned.",
                55, Coordinate.convert(5, 5));
        createSafeTestScenario("testConvert2", 5, 7);
        assertEquals("Incorrect value was returned.",
                30, Coordinate.convert(5, 5));
    }

    @Test
    public void testEquals() {
        assertEquals("Should be equal.", coordinate11, coordinate11Dup);
        Coordinate coordinate11Copy = coordinate11;
        assertEquals("Should be equal.", coordinate11, coordinate11Copy);
        assertNotEquals("Should not be equal.", coordinate21, coordinateneg21);
        assertTrue("Should be equal",
                (new Coordinate()).equals(new Coordinate(0)));
        assertNotEquals("Should not be equal",
                new Coordinate(1, 0),
                new Coordinate(0, 1));
        assertNotEquals("Should not be equal",
                new Coordinate(0),
                new Coordinate(0, 1));
        try {
            assertNotEquals("Coordinate not equal to null",
                    coordinate21, null);
        } catch (NullPointerException e) {
            fail("Should handle the exception inside the method.");
        }
        try {
            assertNotEquals("Coordinate not equal to null",
                    coordinate21, new Fauna(Size.LARGE, coordinate11, TileType.LAND));
        } catch (Exception e) {
            fail("Should handle the exception inside the method.");
        }
    }

    @Test
    public void testHashCode() {
        assertEquals("Should be equal.",
                coordinate11.hashCode(), coordinate11Dup.hashCode());
        assertNotEquals("Should not be equal.",
                coordinate21.hashCode(), coordinateneg21.hashCode());
    }

    @Test
    public void testTranslate() {
        assertEquals("Should be equal.", coordinate11, coordinate11.translate(0, 0));
        assertEquals("Should be equal.",
                coordinateneg21, coordinate21.translate(-4, -2));
        assertEquals("Should be equal.", coordinate21, coordinate11.translate(1, 0));
    }

    @Test
    public void testEncode() {
        assertEquals("Incorrect value was returned.",
                "1,1", coordinate11.encode());
        assertEquals("Incorrect value was returned.",
                "0,0", new Coordinate().encode());
        assertEquals("Incorrect value was returned.",
                "-2,-1", coordinateneg21.encode());
    }

    @Test
    public void testDecode() {
        try {
            assertEquals("Should be equal.",
                    coordinate11, Coordinate.decode(coordinate11.encode()));
            assertEquals("Should be equal.", coordinate11, Coordinate.decode("1,1"));
            assertEquals("Should be equal.",
                    coordinate21, Coordinate.decode(coordinate21.encode()));
            assertEquals("Should be equal.", coordinate21, Coordinate.decode("2,1"));
            assertEquals("Should be equal.",
                    coordinateneg21, Coordinate.decode(coordinateneg21.encode()));
            assertEquals("Should be equal.", coordinateneg21, Coordinate.decode("-2,-1"));
        } catch (BadSaveException e) {
            fail();
        }
        try {
            Coordinate.decode("-1,-1,");
            fail("BadSaveException");
        } catch (BadSaveException e) {
            assertTrue(true);
        }
        try {
            Coordinate.decode("1,4 ");
            fail("BadSaveException");
        } catch (BadSaveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDistance() {
        assertEquals("Should be equal.",
                new Coordinate(), coordinate11.distance(coordinate11));
        assertEquals("Should be equal.",
                new Coordinate(-4, -2), coordinate21.distance(coordinateneg21));
        assertEquals("Should be equal.",
                new Coordinate(1, 0), coordinate11.distance(coordinate21));
    }

    @Test
    public void testToString() {
        assertEquals("Incorrect value was returned.",
                "(1,1)", coordinate11.toString());
        assertEquals("Incorrect value was returned.",
                "(0,0)", new Coordinate().toString());
        assertEquals("Incorrect value was returned.",
                "(-2,-1)", coordinateneg21.toString());
    }
}