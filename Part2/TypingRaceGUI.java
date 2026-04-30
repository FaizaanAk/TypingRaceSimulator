import javax.swing.*;
import java.awt.*;

public class TypingRaceGUI
{
    //Onitiates the GUI application.
    public static void startRaceGUI()
    {
        // Run on the Event Dispatch Thread as required by Swing
        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("Typing Race Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 650);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // Start on the config screen
            SetupScreen setupScreen = new SetupScreen(frame);
            frame.setContentPane(setupScreen);
            frame.setVisible(true);
        });
    }

    //Calls startRaceGUI() to start the program
    public static void main(String[] args)
    {
        startRaceGUI();
    }
}