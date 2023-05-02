package researchsim.display;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import researchsim.entities.*;
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

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Subclass of the JavaFX Canvas to represent the main elements of the Scenario graphically.
 * <p>
 * <b>Note:</b> The contents of this file do not necessarily follow best practice
 *
 * @given
 */
public class ScenarioCanvas extends Canvas {

    /**
     * View model containing the main model of the application
     */
    private final ViewModel viewModel;

    /**
     * Mapping of clickable regions (rectangles) to entities drawn on the canvas
     */
    private final Map<ClickableRegion, Entity> drawnEntity;

    /**
     * Mapping of clickable regions (rectangles) to users move areas on the canvas
     */
    private final Map<ClickableRegion, Coordinate> drawnMove;

    /**
     * Mapping of clickable regions (rectangles) to users collection areas on the canvas
     */
    private final Map<ClickableRegion, Coordinate> drawnCollect;

    /**
     * Sprites for the specific tile types, sprites are loaded on first use
     */
    private Map<TileType, Image> tileSprites;
    /**
     * Sprites for the specific plants, sprites are loaded on first use
     */
    private Map<Size, Image> plantSprites;
    /**
     * Sprites for the specific plants, sprites are loaded on first use
     */
    private Map<Size, Map<TileType, Image>> animalSprites;
    /**
     * Sprite for the user, sprites are loaded on first use
     */
    private Image userSprite;

    /**
     * Creates a new PortCanvas with the given dimensions.
     *
     * @param viewModel view model to use to render elements on the canvas
     * @param width     width of the canvas, in pixels
     * @param height    height of the canvas, in pixels
     * @given
     */
    public ScenarioCanvas(ViewModel viewModel, double width, double height)
        throws BadSaveException {
        super(width, height);

        this.viewModel = viewModel;

        this.drawnEntity = new HashMap<>();
        this.drawnMove = new HashMap<>();
        this.drawnCollect = new HashMap<>();
        // Sprite creation
        tileSprites = new HashMap<>();
        plantSprites = new HashMap<>();
        animalSprites = new HashMap<>();

        HashMap<TileType, Image> smallAnimalSprites = new HashMap<>();
        HashMap<TileType, Image> mediumAnimalSprites = new HashMap<>();
        HashMap<TileType, Image> largeAnimalSprites = new HashMap<>();
        HashMap<TileType, Image> giantAnimalSprites = new HashMap<>();
        try {
            userSprite = new Image(new File("assets/user.png").toURI().toURL().toString());

            tileSprites.put(TileType.LAND,
                new Image(new File("assets/land.png").toURI().toURL().toString()));
            tileSprites.put(TileType.OCEAN,
                new Image(new File("assets/ocean.png").toURI().toURL().toString()));
            tileSprites.put(TileType.SAND,
                new Image(new File("assets/sand.png").toURI().toURL().toString()));
            tileSprites.put(TileType.MOUNTAIN,
                new Image(new File("assets/mountain.png").toURI().toURL().toString()));

            smallAnimalSprites.put(TileType.OCEAN,
                new Image(new File("assets/crab.png").toURI().toURL().toString()));
            smallAnimalSprites.put(TileType.LAND,
                new Image(new File("assets/mouse.png").toURI().toURL().toString()));

            animalSprites.put(Size.SMALL, smallAnimalSprites);

            mediumAnimalSprites.put(TileType.OCEAN,
                new Image(new File("assets/fish.png").toURI().toURL().toString()));
            mediumAnimalSprites.put(TileType.LAND,
                new Image(new File("assets/dog.png").toURI().toURL().toString()));

            animalSprites.put(Size.MEDIUM, mediumAnimalSprites);

            largeAnimalSprites.put(TileType.OCEAN,
                new Image(new File("assets/shark.png").toURI().toURL().toString()));
            largeAnimalSprites.put(TileType.LAND,
                new Image(new File("assets/horse.png").toURI().toURL().toString()));

            animalSprites.put(Size.LARGE, largeAnimalSprites);

            giantAnimalSprites.put(TileType.OCEAN,
                new Image(new File("assets/whale.png").toURI().toURL().toString()));
            giantAnimalSprites.put(TileType.LAND,
                new Image(new File("assets/elephant.png").toURI().toURL().toString()));

            animalSprites.put(Size.GIANT, giantAnimalSprites);

            plantSprites.put(Size.SMALL,
                new Image(new File("assets/flower.png").toURI().toURL().toString()));
            plantSprites.put(Size.MEDIUM,
                new Image(new File("assets/shrub.png").toURI().toURL().toString()));
            plantSprites.put(Size.LARGE,
                new Image(new File("assets/sapling.png").toURI().toURL().toString()));
            plantSprites.put(Size.GIANT,
                new Image(new File("assets/tree.png").toURI().toURL().toString()));

        } catch (MalformedURLException e) {
            throw new BadSaveException("Missing the required \"assets\" folder.\nGo to Blackboard"
                + " (learn.uq.edu.au) to download this folder.");
        }

        setOnMouseClicked(event -> {
            /* Discard any click that is not a primary (left mouse button) click */
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            double x = event.getX();
            double y = event.getY();
            Entity clickedEntity = null;

            for (Map.Entry<ClickableRegion, Entity> entry : drawnEntity.entrySet()) {
                if (entry.getKey().wasClicked(x, y)) {
                    clickedEntity = entry.getValue();
                }
            }
            for (Map.Entry<ClickableRegion, Coordinate> entry : drawnMove.entrySet()) {
                if (entry.getKey().wasClicked(x, y)) {
                    Entity e = viewModel.getSelectedEntity().get();
                    if (e instanceof User) {
                        ((User) e).move(entry.getValue());
                        viewModel.setHasMoved(true);
                    }
                }
            }

            for (Map.Entry<ClickableRegion, Coordinate> entry : drawnCollect.entrySet()) {
                if (entry.getKey().wasClicked(x, y)) {
                    Entity e = viewModel.getSelectedEntity().get();
                    if (e instanceof User) {
                        try {
                            ((User) e).collect(entry.getValue());
                        } catch (NoSuchEntityException | CoordinateOutOfBoundsException ignored) {
                            // Ignored
                        }
                    }
                }
            }

            viewModel.getSelectedEntity().set(clickedEntity);
            viewModel.registerChange();
            /* Ensures the canvas gains focus when it is clicked */
            addEventFilter(MouseEvent.MOUSE_PRESSED, e -> requestFocus());
        });
    }

    /**
     * Returns the size of a grid square
     */
    private double getGridSize() {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        return Math.min(getWidth() / scenario.getWidth(), getHeight() / scenario.getHeight());
    }

    /**
     * Draws all the components of the game map
     * @given
     */
    public void draw() {
        this.drawnEntity.clear();
        this.drawnMove.clear();
        this.drawnCollect.clear();

        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.ALICEBLUE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        drawGrid();
        drawLastEvent();
    }

    /**
     * Draws an entity's possible move options.
     *
     * @param entity the entity to draw the move range for
     * @given
     */
    public void drawEntityMove(Entity entity) {
        drawEntityRange(entity, true);
    }

    /**
     * Draws a users possible collect options.
     *
     * @param user the user to draw the collect range for
     * @given
     */
    public void drawUserCollect(User user) {
        drawEntityRange(user, false);
    }

    /**
     * sets the graphics line to dashes
     * @given
     */
    private void setLineDashes() {
        getGraphicsContext2D().setLineDashes(getGridSize() / 10);
    }

    /**
     * Sets the graphics line dashes to solid
     * @given
     */
    private void setLineSolid() {
        getGraphicsContext2D().setLineDashes((double[]) null);
    }

    /**
     * Strokes an arrow using the current stroke paint.
     * <p>
     * This method will be affected by any of the global common or stroke attributes as specified in
     * the Rendering Attributes Table.
     *
     * @param x1 the X coordinate of the starting point of the arrow.
     * @param y1 the Y coordinate of the starting point of the arrow.
     * @param x2 the X coordinate of the ending point of the arrow.
     * @param y2 the Y coordinate of the ending point of the arrow.
     */
    private void drawArrow(double x1, double y1, double x2, double y2) {
        // Thanks to https://stackoverflow.com/questions/35751576/javafx-draw-line-with-arrow-canvas
        GraphicsContext gc = getGraphicsContext2D();
        // store data to reset
        Paint original = gc.getFill();
        Affine originalMatrix = gc.getTransform();
        // suppress checkstyle
        gc.setFill(original);
        gc.setTransform(originalMatrix);
        // update fill to same color as stroke
        gc.setFill(gc.getStroke());

        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        int headSize = 8;
        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[] {len, len - headSize, len - headSize, len},
            new double[] {0, -headSize, headSize, 0},
            4);

        // reset
        gc.setFill(original);
        gc.setTransform(originalMatrix);
    }

    /**
     * Draws the last event(s) of the scenario.
     */
    private void drawLastEvent() {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        List<Event> events = scenario.getLog().getEvents();
        if (events.size() == 0) {
            return;
        }
        int index = events.size() - 1;
        GraphicsContext gc = getGraphicsContext2D();
        gc.setLineWidth(2.0);
        setLineDashes();

        do {
            Event event = events.get(index);
            Color c = Color.BLACK;
            if (event instanceof MoveEvent) {
                c = Color.AQUA;
            } else if (event instanceof CollectEvent) {
                c = Color.MAROON;
            } else {
                easterEgg();
            }
            double gridSize = getGridSize();
            double offset = gridSize / 2;
            gc.setStroke(c);
            drawArrow(event.getInitialCoordinate().getX() * gridSize + offset,
                event.getInitialCoordinate().getY() * gridSize + offset,
                event.getCoordinate().getX() * gridSize + offset,
                event.getCoordinate().getY() * gridSize + offset);
            if (events.get(index).getEntity() instanceof User) {
                break;
            }
            index--;
        } while (index >= 0 && !(events.get(index).getEntity() instanceof User));
        setLineSolid();
        gc.setLineWidth(1.0);
    }

    /**
     * Draws the tile grid that the game is played on.
     */
    private void drawGrid() {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.GRAY);
        for (int i = 0; i < scenario.getWidth(); i++) {
            for (int j = 0; j < scenario.getHeight(); j++) {
                drawTile(i, j);
            }
        }
    }

    /**
     * Draws the tile at the specified grid position.
     *
     * @param x the column to draw
     * @param y the row to draw
     */
    private void drawTile(int x, int y) {
        GraphicsContext gc = getGraphicsContext2D();
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        Tile tile = scenario.getMapGrid()[Coordinate.convert(x, y)];

        double gridSize = getGridSize();

        // draw sprite
        gc.drawImage(tileSprites.get(tile.getType()),
            x * gridSize, y * gridSize, gridSize, gridSize);
        // draw grid
        if (viewModel.showGrid()) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(0.5);
            gc.strokeRect(x * gridSize, y * gridSize, gridSize, gridSize);
            gc.setLineWidth(1.0);
        }
        // draw coordinate
        if (viewModel.showCoordinate()) {
            gc.setFill(Color.BLACK);
            gc.fillText(String.format("%d,%d", x, y), x * gridSize,
                y * gridSize + gridSize / 3);

        }
        // draw sub entities
        if (tile.hasContents()) {
            Entity entity = null;
            try {
                entity = tile.getContents();
            } catch (NoSuchEntityException ignored) {
                easterEgg();
            }
            this.drawnEntity.put(
                new ClickableRegion(x * gridSize, y * gridSize, gridSize, gridSize),
                entity);
            if (entity instanceof Fauna) {
                drawAnimal((Fauna) entity, x, y);
            } else if (entity instanceof Flora) {
                drawPlant((Flora) entity, x, y);
            } else if (entity instanceof User) {
                drawUser((User) entity, x, y);
            } else {
                easterEgg();
            }
        }
    }

    /**
     * Draws a user at the specified tile.
     * The user is centred in the tile with a padding of 1 / 5 the grid size on all sides.
     * The width and height should therefore be no larger than 3 / 5 of the grid size.
     *
     * @param user the user to draw
     * @param x    the column to draw
     * @param y    the row to draw
     */
    private void drawUser(User user, int x, int y) {
        double gridSize = getGridSize();
        GraphicsContext gc = getGraphicsContext2D();
        // set buffer
        gc.drawImage(userSprite,
            x * gridSize,  y * gridSize, gridSize, gridSize);

    }

    /**
     * Draws a plant at the specified tile.
     * The plant is centred in the tile with a padding of 1 / 5 the grid size on all sides.
     * The width and height should therefore be no larger than 3 / 5 of the grid size.
     *
     * @param plant the plant to draw
     * @param x     the column to draw
     * @param y     the row to draw
     */
    private void drawPlant(Flora plant, int x, int y) {
        double gridSize = getGridSize();
        GraphicsContext gc = getGraphicsContext2D();
        // set buffer
        gc.drawImage(plantSprites.get(plant.getSize()),
            x * gridSize,  y * gridSize, gridSize, gridSize);
        gc.strokeText(String.valueOf(plant.getSize().points), (x + 1) * gridSize - gridSize / 5,
            y * gridSize + gridSize / 5);
    }

    /**
     * Draws an animal at the specified tile.
     * The animal is centred in the tile with a padding of 1 / 5 the grid size on all sides.
     * The width and height should therefore be no larger than 3 / 5 of the grid size.
     *
     * @param animal the animal to draw
     * @param x      the column to draw
     * @param y      the row to draw
     */
    private void drawAnimal(Fauna animal, int x, int y) {
        double gridSize = getGridSize();
        GraphicsContext gc = getGraphicsContext2D();
        // set buffer
        gc.drawImage(animalSprites.get(animal.getSize()).get(animal.getHabitat()),
            x * gridSize,  y * gridSize, gridSize, gridSize);
        gc.setStroke(Color.BLACK);
        gc.strokeText(String.valueOf(animal.getSize().points), (x + 1) * gridSize - gridSize / 5,
            y * gridSize + gridSize / 5);
    }

    /**
     * Just a fun method to throw an interesting exception on cases that shouldn't exist
     * I hope that you are enjoying reading all of this source code :)
     * <p>
     * -> Developer
     * =============================================
     * Please don't let this be you.
     * <p>
     * https://www.monkeyuser.com/2019/code-review/
     * <p>
     * -> A friendly programmer
     */
    private void easterEgg() {
        throw new IllegalArgumentException("Somehow, you found an easter egg..."
            + System.lineSeparator().repeat(3)
            + "...and by easter egg I mean an edge case that should have never occurred :) +"
            + System.lineSeparator()
            + "Read the stack trace to ensure your code is running as expected.");
    }

    /**
     * Draws an entity's possible options in a range.
     *
     * @param entity the entity to draw
     */
    private void drawEntityRange(Entity entity, boolean isMove) {
        if (entity instanceof Flora) {
            return;
        }
        draw();
        double gridSize = getGridSize();

        GraphicsContext gc = getGraphicsContext2D();
        Map<ClickableRegion, Coordinate> areas = isMove ? drawnMove : drawnCollect;
        List<Coordinate> locations = new ArrayList<>();
        if (entity instanceof Fauna) {
            locations = ((Fauna) entity).getPossibleMoves();
        } else if (entity instanceof User) {
            locations = isMove ? ((User) entity).getPossibleMoves() :
                ((User) entity).getPossibleCollection();
        } else {
            easterEgg();
        }
        for (Coordinate coordinate : locations) {
            int x = coordinate.getX();
            int y = coordinate.getY();

            ClickableRegion region = new ClickableRegion(x * gridSize, y * gridSize,
                gridSize, gridSize);

            areas.put(region, coordinate);
            drawnEntity.remove(region);
            boolean tileHasContents =
                ScenarioManager.getInstance().getScenario()
                    .getMapGrid()[Coordinate.convert(x, y)].hasContents();
            Color c = isMove && !tileHasContents ? Color.DARKORCHID : Color.RED;
            // DRAW
            gc.setStroke(c);
            gc.strokeRect(x * gridSize, y * gridSize,
                gridSize, gridSize);
            gc.setFill(new Color(c.getRed(), c.getGreen(),
                c.getBlue(), 0.25));
            gc.fillRect(x * gridSize, y * gridSize,
                gridSize, gridSize);
        }
    }

    /**
     * A class to represent a rectangular region on the canvas that responds to click events
     */
    private static class ClickableRegion {

        /**
         * X-coordinate of the region (top left)
         */
        private final double xcoord;
        /**
         * Y-coordinate of the region (top left)
         */
        private final double ycoord;
        /**
         * Width of the region, in pixels
         */
        private final double width;
        /**
         * Height of the region, in pixels
         */
        private final double height;

        /**
         * Creates a new clickable region with the given coordinates and dimensions
         *
         * @given
         */
        public ClickableRegion(double x, double y, double width, double height) {
            this.xcoord = x;
            this.ycoord = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Returns whether the given click event's coordinates fall within this clickable
         * region
         *
         * @given
         */
        public boolean wasClicked(double clickX, double clickY) {
            return clickX >= this.xcoord && clickX <= this.xcoord + this.width
                && clickY >= this.ycoord && clickY <= this.ycoord + this.height;
        }

        /**
         * Returns the hash code of this region.
         * <p>
         * Two regions that are equal according to equals(Object) method should have the same hash
         * code.
         *
         * @return hash code of this region
         * @given
         */
        @Override
        public int hashCode() {
            return Objects.hash(xcoord, ycoord, width, height);
        }

        /**
         * Returns true if and only if this region is equal to the other given
         * region.
         * <p>
         * For two regions to be equal, they must have the same x and y position, the same width
         * and height.
         *
         * @param other the reference object with which to compare
         * @return {@code true} if this region is the same
         * as the {@code other} argument; {@code false} otherwise
         * @given
         */
        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other == this) {
                return true;
            }
            if (!(other instanceof ClickableRegion)) {
                return false;
            }

            ClickableRegion otherRegion = (ClickableRegion) other;

            return xcoord == otherRegion.xcoord && ycoord == otherRegion.ycoord
                && width == otherRegion.width && height == otherRegion.height;
        }
    }
}
