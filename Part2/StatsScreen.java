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
        setLayout(new BorderLayout(0, 20));

        // Find winner (first place)
        int winnerIdx = -1;
        for (int i = 0; i < finishPositions.length; i++) {
            if (finishPositions[i] == 1) {
                winnerIdx = i;
                break;
            }
        }

        // --- Title ---
        String winnerText = (winnerIdx != -1)
            ? ("Winner: " + typists[winnerIdx].getSymbol() + "  " + typists[winnerIdx].getName() +
               "  |  Accuracy: " + String.format("%.2f", typists[winnerIdx].getAccuracy()))
            : "No winner";
        JLabel title = new JLabel(winnerText, SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(new Color(72, 199, 170));
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Table of stats ---
        String[] columns = {"Place", "Typist", "WPM", "Accuracy %", "Burnouts", "Accuracy Δ"};
        Object[][] data = new Object[typists.length][columns.length];

        double timeMinutes = totalTimeMs / 1000.0 / 60.0;
        for (int i = 0; i < typists.length; i++) {
            // Place
            String place = finishPositions[i] > 0 ? ("#" + finishPositions[i]) : "-";
            // Typist
            String typistStr = typists[i].getSymbol() + "  " + typists[i].getName();
            // WPM
            double wpm = (passageLength / 5.0) / (timeMinutes > 0 ? timeMinutes : 1);
            // Accuracy %
            int correct = typists[i].getProgress();
            int total = totalKeystrokes[i];
            double accPct = (total > 0) ? (100.0 * correct / total) : 0.0;
            // Burnouts
            int burnouts = burnoutCounts[i];
            // Accuracy Δ (change)
            // Assume accuracy change is: +0.02 for winner, -0.01 per burnout (as in RaceDisplay)
            double accChange = 0.0;
            if (finishPositions[i] == 1) accChange += 0.02;
            if (burnoutCounts[i] > 0) accChange -= 0.01 * burnoutCounts[i];
            String accDelta = (accChange == 0.0) ? "0.00" : String.format("%+.2f", accChange);

            data[i][0] = place;
            data[i][1] = typistStr;
            data[i][2] = String.format("%.1f", wpm);
            data[i][3] = String.format("%.1f", accPct);
            data[i][4] = burnouts;
            data[i][5] = accDelta;
        }

        javax.swing.JTable table = new javax.swing.JTable(data, columns);
        table.setFont(new Font("Monospaced", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(28, 30, 45));
        table.setGridColor(new Color(72, 199, 170));
        table.setShowGrid(true);
        table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(35, 38, 55));
        table.getTableHeader().setForeground(new Color(72, 199, 170));
        table.setEnabled(false);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        add(scroll, BorderLayout.CENTER);

        // --- Back to Setup button ---
        JButton backBtn = new JButton("Back to Setup");
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        backBtn.setBackground(new Color(72, 199, 170));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        backBtn.addActionListener(e -> {
            parentFrame.setContentPane(new SetupScreen(parentFrame));
            parentFrame.revalidate();
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(18, 18, 28));
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}