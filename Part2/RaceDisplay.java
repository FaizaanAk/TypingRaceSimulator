import javax.swing.*;
import java.awt.*;

public class RaceDisplay extends JPanel
{
    public RaceDisplay(JFrame parentFrame, Typist[] typists, String passage, boolean autocorrect, boolean caffeineMode, boolean nightShift)
    {
        setBackground(new Color(30, 30, 40));
        setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("Race screen coming soon...", SwingConstants.CENTER);
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("Monospaced", Font.BOLD, 20));
        add(placeholder, BorderLayout.CENTER);
    }
}