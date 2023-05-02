package researchsim.entities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import researchsim.logging.CollectEvent;
import researchsim.logging.Event;
import researchsim.logging.MoveEvent;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.util.NoSuchEntityException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest {

    private Scenario scenario1;
    private User user1, user2, user3;
    private Fauna fauna1, fauna2;

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

        user1 = new User(new Coordinate(11, 3), "user1");
        scenario1.getMapGrid()[(new Coordinate(11, 3)).getIndex()].setContents(user1);

        fauna1 = new Fauna(Size.LARGE, new Coordinate(11, 4), TileType.LAND);
        scenario1.getMapGrid()[
                (new Coordinate(11, 4)).getIndex()].setContents(fauna1);
        scenario1.getController().addAnimal(fauna1);

        user2 = new User(new Coordinate(4, 2), "user2");
        scenario1.getMapGrid()[(new Coordinate(4, 2)).getIndex()].setContents(user2);

        fauna2 = new Fauna(Size.SMALL, new Coordinate(2, 1), TileType.LAND);
        scenario1.getMapGrid()[(new Coordinate(2, 1)).getIndex()].setContents(fauna2);
        scenario1.getController().addAnimal(fauna2);

        user3 = new User(new Coordinate(5, 2), "user2");
        scenario1.getMapGrid()[(new Coordinate(5, 2)).getIndex()].setContents(user3);
    }

    @After
    public void tearDown() throws Exception {
        ScenarioManager.getInstance().reset();
        user1 = null;
        user2 = null;
        user3 = null;
        fauna1 = null;
        fauna2 = null;
    }

    @Test
    public void testGetName() {
        assertEquals("Incorrect value was returned.", "user1", user1.getName());
        assertEquals("Incorrect value was returned.", "user2", user2.getName());
        assertEquals("Incorrect value was returned.", user3.getName(), user2.getName());
    }

    @Test
    public void testEncode() {
        assertEquals("Incorrect value was returned.",
                "User-11,3-user1", user1.encode());
        assertEquals("Incorrect value was returned.",
                "User-4,2-user2", user2.encode());
    }

    @Test
    public void testHashCode() {
        assertEquals(
                "Incorrect value was returned.",
                (new User(new Coordinate(11, 3), "user1")).hashCode(),
                user1.hashCode());
        assertEquals(
                "Incorrect value was returned.",
                (new User(new Coordinate(4, 2), "user2")).hashCode(),
                user2.hashCode());
        assertNotEquals("Incorrect value was returned.",
                user1.hashCode(), user2.hashCode());
        assertNotEquals("Incorrect value was returned.",
                user3.hashCode(), user2.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(
                "Incorrect value was returned.",
                new User(new Coordinate(11, 3), "user1"),
                user1);
        assertEquals(
                "Incorrect value was returned.",
                new User(new Coordinate(4, 2), "user2"),
                user2);
        assertNotEquals("Incorrect value was returned.", user1, user2);
    }

    @Test
    public void testCheckRange() {
        List<Coordinate> user1RangeAns = Arrays.asList(
                new Coordinate(11, 0), new Coordinate(10, 1),
                new Coordinate(10, 4), new Coordinate(10, 5),
                new Coordinate(11, 1), new Coordinate(10, 2),
                new Coordinate(11, 2), new Coordinate(10, 3),
                new Coordinate(14, 3), new Coordinate(9, 2),
                new Coordinate(11, 3), new Coordinate(11, 6),
                new Coordinate(11, 4), new Coordinate(11, 5),
                new Coordinate(9, 3), new Coordinate(8, 3),
                new Coordinate(9, 4), new Coordinate(12, 1),
                new Coordinate(12, 2), new Coordinate(12, 5),
                new Coordinate(13, 2), new Coordinate(13, 4),
                new Coordinate(12, 3), new Coordinate(13, 3),
                new Coordinate(12, 4)
        );
        List<Coordinate> user1Range = user1.checkRange(
                user1.getSize().moveDistance, user1.getCoordinate());
        List<Coordinate> user2RangeAns = Arrays.asList(
                new Coordinate(3, 0), new Coordinate(4, 1),
                new Coordinate(1, 2), new Coordinate(2, 1),
                new Coordinate(3, 1), new Coordinate(4, 0),
                new Coordinate(3, 2), new Coordinate(4, 3),
                new Coordinate(2, 2), new Coordinate(2, 3),
                new Coordinate(3, 4), new Coordinate(4, 5),
                new Coordinate(3, 3), new Coordinate(4, 4),
                new Coordinate(5, 0), new Coordinate(5, 4),
                new Coordinate(5, 1), new Coordinate(5, 3),
                new Coordinate(6, 1), new Coordinate(6, 2),
                new Coordinate(6, 3), new Coordinate(5, 2),
                new Coordinate(7, 2), new Coordinate(4, -1),
                new Coordinate(4, 2)
        );
        List<Coordinate> user2Range = user2.checkRange(
                user2.getSize().moveDistance, user2.getCoordinate());

        assertTrue(
                user1Range.size() == user1RangeAns.size() &&
                        user1Range.containsAll(user1RangeAns) &&
                        user1RangeAns.containsAll(user1Range)
        );
        assertTrue(
                user2Range.size() == user2RangeAns.size() &&
                        user2Range.containsAll(user2RangeAns) &&
                        user2RangeAns.containsAll(user2Range)
        );
    }

    @Test
    public void testCanMove() {
        try {
            assertTrue("Incorrect value was returned.",
                    user1.canMove(new Coordinate(8, 3)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }
        try {
            // does not move (cannot move 0 distance)
            assertFalse("Incorrect value was returned. Cannot move 0 distance",
                    user1.canMove(new Coordinate(11, 3)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }
        try {
            // more than 1 turn
            assertFalse("Incorrect value was returned. Cannot turn twice.",
                    user1.canMove(new Coordinate(11, 5)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }
        try {
            assertFalse("Incorrect value was returned. Too far away.",
                    user1.canMove(new Coordinate(7, 2)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }
        try {
            // cannot move to a tile whose content is a User
            assertFalse("Incorrect value was returned. Cannot move to another user.",
                    user2.canMove(new Coordinate(5, 2)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }
        try {
            // can move and collect
            assertTrue("Incorrect value was returned. Should move and collect.",
                    user2.canMove(new Coordinate(2, 1)));
        } catch (CoordinateOutOfBoundsException e) {
            fail();
        }

        try {
            user1.canMove(new Coordinate(14, 3));
            fail("Move out of bounds.");
        } catch (CoordinateOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetPossibleMoves() {
        List<Coordinate> user1PossibleMovesAns = Arrays.asList(
                new Coordinate(11, 0), new Coordinate(10, 1),
                new Coordinate(10, 4), new Coordinate(9, 2),
                new Coordinate(11, 1), new Coordinate(10, 2),
                new Coordinate(10, 5), new Coordinate(8, 3),
                new Coordinate(11, 2), new Coordinate(10, 3),
                new Coordinate(11, 4), new Coordinate(9, 3),
                new Coordinate(9, 4)
        );
        List<Coordinate> user1PossibleMoves = user1.getPossibleMoves();
        List<Coordinate> user2PossibleMovesAns = Arrays.asList(
                new Coordinate(3, 0), new Coordinate(4, 1),
                new Coordinate(1, 2), new Coordinate(3, 1),
                new Coordinate(4, 0), new Coordinate(2, 1),
                new Coordinate(3, 2), new Coordinate(4, 3),
                new Coordinate(2, 2), new Coordinate(2, 3),
                new Coordinate(3, 4), new Coordinate(4, 5),
                new Coordinate(3, 3), new Coordinate(4, 4),
                new Coordinate(5, 0), new Coordinate(5, 4),
                new Coordinate(5, 1), new Coordinate(5, 3),
                new Coordinate(6, 1), new Coordinate(6, 3)
        );
        List<Coordinate> user2PossibleMoves = user2.getPossibleMoves();

        assertTrue("Fail to move in a fauna.",
                user1PossibleMoves.size() == user1PossibleMovesAns.size()
                && user1PossibleMoves.containsAll(user1PossibleMovesAns)
                && user1PossibleMovesAns.containsAll(user1PossibleMoves)
        );
        assertTrue("Should not move to a user.",
                user2PossibleMoves.size() == user2PossibleMovesAns.size()
                        && user2PossibleMoves.containsAll(user2PossibleMovesAns)
                        && user2PossibleMovesAns.containsAll(user2PossibleMoves)
        );
    }

    @Test
    public void testCollectOutOfBounds() {
        try {
            user1.collect(new Coordinate(12, 3));
            fail("Out of bounds");
        } catch (NoSuchEntityException e) {
            fail("Out of bounds.");
        } catch (CoordinateOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCollectEmptyTile() {
        try {
            user2.collect(new Coordinate(4, 4));
            fail("Tile does not have content.");
        } catch (NoSuchEntityException e) {
            assertTrue(true);
        } catch (CoordinateOutOfBoundsException e) {
            fail("Not out of bounds.");
        }
    }

    @Test
    public void testCollectNotCollectable() {
        try {
            user2.collect(new Coordinate(5, 2));
            assertEquals("No CollectEvents should be logged",
                    0,
                    scenario1.getLog().getEvents().size());
            assertEquals("Target tile content should still be there",
                    user3,
                    scenario1.getMapGrid()[
                            (new Coordinate(5, 2)).getIndex()].getContents());
            assertEquals("The User should still be there",
                    user2,
                    scenario1.getMapGrid()[
                            (new Coordinate(4, 2)).getIndex()].getContents());
        } catch (NoSuchEntityException | CoordinateOutOfBoundsException e) {
            fail("Nothing should happen. No Exceptions should be thrown.");
        }
    }

    @Test
    public void testCollectOneTileAway() {
        try {
            user1.collect(new Coordinate(11, 4));
            assertEquals("One CollectEvent should be logged",
                    1,
                    scenario1.getLog().getEvents().size());
            assertFalse("Target tile content should have no content.",
                    scenario1.getMapGrid()[
                            (new Coordinate(11, 4)).getIndex()].hasContents());
            assertEquals("The User should still be there",
                    user1,
                    scenario1.getMapGrid()[
                            (new Coordinate(11, 3)).getIndex()].getContents());
            assertFalse("The AnimalController should not have the collected animal.",
                    scenario1.getController().getAnimals().contains(fauna1));
        } catch (NoSuchEntityException | CoordinateOutOfBoundsException e) {
            fail("Nothing should happen. No Exceptions should be thrown.");
        }
    }

    @Test
    public void testCollectMoreThanOneTilesAway() {
        try {
            user2.collect(new Coordinate(2, 1));
            assertEquals("One CollectEvent should be logged",
                    1,
                    scenario1.getLog().getEvents().size());
            assertFalse("Target tile content should have no content.",
                    scenario1.getMapGrid()[
                            (new Coordinate(2, 1)).getIndex()].hasContents());
            assertEquals("The User should still be there",
                    user2,
                    scenario1.getMapGrid()[
                            (new Coordinate(4, 2)).getIndex()].getContents());
            assertFalse("The AnimalController should not have the collected animal.",
                    scenario1.getController().getAnimals().contains(fauna2));
        } catch (NoSuchEntityException | CoordinateOutOfBoundsException e) {
            fail("Nothing should happen. No Exceptions should be thrown. " +
                    "The User is allowed to collect more than 1 tile away.");
        }
    }

    @Test
    public void testGetPossibleCollection() {
        List<Coordinate> user1PossibleCollectionAns = List.of(new Coordinate(11, 4));
        List<Coordinate> user1PossibleCollection = user1.getPossibleCollection();
        assertTrue(String.format("Expected list:%s%nReturn list:%s",
                        user1PossibleCollectionAns,
                        user1PossibleCollection.toString()),
                user1PossibleCollection.size() == user1PossibleCollectionAns.size() &&
                        user1PossibleCollection.containsAll(user1PossibleCollectionAns) &&
                        user1PossibleCollectionAns.containsAll(user1PossibleCollection));

        List<Coordinate> user2PossibleCollection = user2.getPossibleCollection();
        assertTrue(user2PossibleCollection.isEmpty());
    }

    @Test
    public void testMoveOnly() {
        user1.move(new Coordinate(8, 3));
        assertEquals("The logger should have 1 event logged",
                1,
                scenario1.getLog().getEvents().size());
        assertTrue("The user has not moved to the target coordinate",
                scenario1.getMapGrid()[
                        (new Coordinate(8, 3)).getIndex()].hasContents());
        assertFalse("The original coordinate is not empty",
                scenario1.getMapGrid()[
                        (new Coordinate(11, 3)).getIndex()].hasContents());
    }

    @Test
    public void testMoveAndCollect() {
        user2.move(new Coordinate(2, 1));
        // log the move first, then the collect, target pos has user2, ori null
        List<Event> events = scenario1.getLog().getEvents();
        assertEquals("The logger should have 2 events logged",
                2,
                events.size());
        assertTrue("The MoveEvent should be logged first",
                events.get(0) instanceof MoveEvent);
        assertTrue("The MoveEvent should be logged first",
                events.get(1) instanceof CollectEvent);
        try {
            assertTrue("The user should be at the target coordinate",
                    scenario1.getMapGrid()[(new Coordinate(2, 1)).getIndex()]
                            .getContents() instanceof User);
        } catch (NoSuchEntityException e) {
            fail("The user should be at the target coordinate");
        }
        assertFalse("The original coordinate should be empty",
                scenario1.getMapGrid()[
                        (new Coordinate(4, 2)).getIndex()].hasContents());
    }
}