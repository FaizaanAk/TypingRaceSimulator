import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StatsScreen extends JPanel
{
    public StatsScreen(JFrame parentFrame, Typist[] typists, String passage,
                       int passageLength, long totalTimeMs, int turnCount,
                       int[] burnoutCounts, int[] mistypeCounts,
                       int[] totalKeystrokes, int[] finishPositions)
    {
        setBackground(new Color(18, 18, 28));
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Temporary for testing", SwingConstants.CENTER);
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("Monospaced", Font.BOLD, 20));
        add(placeholder, BorderLayout.CENTER);
    }
}