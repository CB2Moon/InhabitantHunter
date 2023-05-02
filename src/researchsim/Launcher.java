package researchsim;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import researchsim.display.View;
import researchsim.display.ViewModel;
import researchsim.util.BadSaveException;

import java.io.IOException;


/**
 * Entry point for the GUI of the Research Simulation.
 * @given
 */
public class Launcher extends Application {
    /**
     * <b>Note</b>: you do not need to write this constructor, it is generated automatically and
     * cannot be removed from the Javadoc.
     * @given
     */
    public Launcher() {}

    /**
     * Launches the GUI.
     * <p>
     * Usage: {@code scenario_file | save_file}
     * <p>
     * Where
     * <ul>
     * <li>{@code scenario_file} or {@code save_file} is the path to the file containing the
     * scenario</li>
     * </ul>
     * @param args command line arguments
     * @given
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: scenario_file [scenario_file] \n");
            System.err.println("You did not specify the names of the required save files"
                + " from which to load.");
            System.err.println("To do this, you need to add the command line "
                + "argument to your "
                + "program in IntelliJ.");

            System.err.println("Go to \"Run > Edit Configurations > Launcher > Program Arguments\" "
                + "and add the paths to your file to the text box.\n");
            System.err.println("Example: saves/scenario1.txt");
            System.exit(1);
        }
        Application.launch(Launcher.class, args);
    }

    /**
     * {@inheritDoc}
     * @given
     */
    @Override
    public void start(Stage stage) {
        View view;
        try {
            view = new View(stage, new ViewModel(getParameters().getRaw()));
        } catch (BadSaveException | IOException e) {
            System.err.println("Error loading files. Stack trace below:");
            e.printStackTrace();
            Platform.exit();
            System.exit(1);
            return;
        }

        view.run();
    }
}
