import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class RaceDisplay extends JPanel
{
    // Race mechanics constants
    private static final double mistypechance = 0.3;
    private static final int slideBackAmount = 2;
    private static final int burnoutDuration = 3;
    private static final int ticks = 200; // milliseconds per turn

    // Colour scheme
    private static final Color mainBackgroundColour = new Color(18, 18, 28);
    private static final Color panelBackgroundColour = new Color(28, 30, 45);
    private static final Color laneColour = new Color(35, 38, 55);
    private static final Color accentColour = new Color(72, 199, 170);
    private static final Color textPrimaryColour = new Color(230, 230, 240);
    private static final Color textDim = new Color(90, 92, 110);
    private static final Color charDone = new Color(80, 200, 120);   // completed chars — green
    private static final Color charCurrent = new Color(255, 220, 60);   // current char — yellow
    private static final Color charBurnout = new Color(220, 80, 80);    // burnt out cursor — red
    private static final Color charMistyped = new Color(220, 100, 60);   // mistyped cursor — orange

    // Race state
    private JFrame parentFrame;
    private Typist[] typists;
    private String passage;
    private int passageLength;
    private boolean autocorrect;
    private boolean caffeineMode;
    private boolean nightShift;
    private ArrayList<SetupScreen.TypistConfigPanel> typistPanels;

    private int turnCount;
    private long startTimeMs;
    private boolean raceOver;

    // Per-typist stat tracking
    private int[] burnoutCounts;
    private int[] mistypeCounts;
    private int[] totalKeystrokes;
    private int[] finishPositions;
    private int finishedCount;

    // UI components — one lane panel per typist
    private LanePanel[] lanePanels;
    private JLabel statusLabel;
    private javax.swing.Timer raceTimer;

    // Constructor
    public RaceDisplay(JFrame parentFrame, Typist[] typists, String passage,
                       boolean autocorrect, boolean caffeineMode, boolean nightShift,
                       ArrayList<SetupScreen.TypistConfigPanel> typistPanels)
    {
        this.parentFrame   = parentFrame;
        this.typists       = typists;
        this.passage       = passage;
        this.passageLength = passage.length();
        this.autocorrect   = autocorrect;
        this.caffeineMode  = caffeineMode;
        this.nightShift    = nightShift;
        this.typistPanels  = typistPanels;

        turnCount      = 0;
        raceOver       = false;
        finishedCount  = 0;

        burnoutCounts   = new int[typists.length];
        mistypeCounts   = new int[typists.length];
        totalKeystrokes = new int[typists.length];
        finishPositions = new int[typists.length];

        // Reset all typists to start
        for (Typist t : typists) t.resetToStart();

        buildUI();

        startTimeMs = System.currentTimeMillis();

        // Start the race timer
        raceTimer = new javax.swing.Timer(ticks, e -> tick());
        raceTimer.start();
    }

    // Build the UI layout
    private void buildUI()
    {
        setLayout(new BorderLayout(0, 10));
        setBackground(mainBackgroundColour);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //Title bar
        JLabel title = new JLabel("RACE IN PROGRESS", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(accentColour);
        title.setOpaque(true);
        title.setBackground(new Color(10, 10, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Lane container
        JPanel lanesPanel = new JPanel();
        lanesPanel.setLayout(new GridLayout(typists.length, 1, 0, 8));
        lanesPanel.setBackground(mainBackgroundColour);

        lanePanels = new LanePanel[typists.length];
        for (int i = 0; i < typists.length; i++)
        {
            lanePanels[i] = new LanePanel(typists[i], passage);
            lanesPanel.add(lanePanels[i]);
        }

        JScrollPane scroll = new JScrollPane(lanesPanel);
        scroll.setBackground(mainBackgroundColour);
        scroll.getViewport().setBackground(mainBackgroundColour);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("  Turn 0  |  Race underway...", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statusLabel.setForeground(textPrimaryColour);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(10, 10, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    // One tick of the race
    private void tick()
    {
        if (raceOver) return;

        turnCount++;

        for (int i = 0; i < typists.length; i++)
        {
            if (finishPositions[i] > 0) continue; // already finished

            advanceTypist(i);

            // Check if this typist just finished
            if (typists[i].getProgress() >= passageLength && finishPositions[i] == 0)
            {
                finishedCount++;
                finishPositions[i] = finishedCount;
            }
        }

        // Refresh all lane displays
        for (LanePanel lp : lanePanels) lp.refresh();

        // Update status bar
        long elapsed = (System.currentTimeMillis() - startTimeMs) / 1000;
        statusLabel.setText("  Turn " + turnCount + "  |  Time: " + elapsed + "s  |  Race underway...");

        // Check if race is over
        if (finishedCount >= 1 && allTypistsFinishedOrBehind())
        {
            endRace();
        }
    }

    // Race ends when the leader has finished
    private boolean allTypistsFinishedOrBehind()
    {
        // End as soon as first place is decided
        return finishedCount >= 1;
    }

    // Advance one typist by one turn
    private void advanceTypist(int i)
    {
        Typist t = typists[i];

        if (t.isBurntOut())
        {
            t.recoverFromBurnout();
            t.setJustMistyped(false);
            return;
        }

        //Accuracy modifiers
        double accuracy = t.getAccuracy();

        // Caffeine Mode: +0.15 accuracy for first 10 turns, then +0.05 burnout risk
        if (caffeineMode && turnCount <= 10) accuracy = Math.min(1.0, accuracy + 0.15);

        // Energy Drink: boost first half, reduce second half
        if (typistPanels != null && typistPanels.get(i).hasEnergyDrink())
        {
            if (t.getProgress() < passageLength / 2.0)
                accuracy = Math.min(1.0, accuracy + 0.10);
            else
                accuracy = Math.max(0.0, accuracy - 0.05);
        }

        // Headphones: reduce mistype chance by reducing effective inaccuracy
        double mistypeReduction = 0.0;
        if (typistPanels != null && typistPanels.get(i).hasHeadphones())
            mistypeReduction = 0.10;

        // Type a character
        if (Math.random() < accuracy)
        {
            t.typeCharacter();
            totalKeystrokes[i]++;
        }

        // Mistype check
        double mistypeChance = (1.0 - accuracy) * mistypechance - mistypeReduction;
        if (mistypeChance > 0 && Math.random() < mistypeChance)
        {
            int slideAmount = autocorrect ? Math.max(1, slideBackAmount / 2) : slideBackAmount;
            t.slideBack(slideAmount);
            t.setJustMistyped(true);
            mistypeCounts[i]++;
            totalKeystrokes[i]++;
        }
        else
        {
            t.setJustMistyped(false);
        }

        // Burnout check
        double burnoutRisk = 0.05 * accuracy * accuracy;
        if (caffeineMode && turnCount > 10) burnoutRisk += 0.05;
        if (Math.random() < burnoutRisk)
        {
            // Wrist Support: reduces burnout duration by 1
            int duration = burnoutDuration;
            if (typistPanels != null && typistPanels.get(i).hasWristSupport())
                duration = Math.max(1, duration - 1);

            t.burnOut(duration);
            burnoutCounts[i]++;
        }
    }

    // End the race — stop timer, apply accuracy changes, go to stats
    private void endRace()
    {
        raceOver = true;
        raceTimer.stop();

        long totalTimeMs = System.currentTimeMillis() - startTimeMs;

        // Apply accuracy changes: winner +0.02, burnt out typists -0.01 per burnout
        for (int i = 0; i < typists.length; i++)
        {
            if (finishPositions[i] == 1)
            {
                typists[i].setAccuracy(typists[i].getAccuracy() + 0.02);
            }
            if (burnoutCounts[i] > 0)
            {
                typists[i].setAccuracy(typists[i].getAccuracy() - 0.01);
            }
        }

        // Switch to stats screen
        StatsScreen statsScreen = new StatsScreen(
            parentFrame, typists, passage, passageLength,
            totalTimeMs, turnCount, burnoutCounts, mistypeCounts,
            totalKeystrokes, finishPositions
        );
        parentFrame.setContentPane(statsScreen);
        parentFrame.revalidate();
    }

    // Inner class — one lane for one typist
    class LanePanel extends JPanel
    {
        private Typist typist;
        private String passage;
        private JLabel nameLabel;
        private JTextPane passagePane;
        private JLabel statusLabel;

        public LanePanel(Typist typist, String passage)
        {
            this.typist  = typist;
            this.passage = passage;

            setLayout(new BorderLayout(8, 0));
            setBackground(laneColour);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 58, 82), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            // Left — typist name and symbol
            nameLabel = new JLabel(typist.getSymbol() + "  " + typist.getName());
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
            nameLabel.setForeground(accentColour);
            nameLabel.setPreferredSize(new Dimension(200, 40));
            add(nameLabel, BorderLayout.WEST);

            // Centre — passage text with coloured progress
            passagePane = new JTextPane();
            passagePane.setEditable(false);
            passagePane.setBackground(laneColour);
            passagePane.setFont(new Font("Monospaced", Font.PLAIN, 14));
            passagePane.setOpaque(true);
            add(passagePane, BorderLayout.CENTER);

            // Right — status (burnt out / mistyped / accuracy)
            statusLabel = new JLabel("Accuracy: " + String.format("%.2f", typist.getAccuracy()));
            statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
            statusLabel.setForeground(textPrimaryColour);
            statusLabel.setPreferredSize(new Dimension(220, 40));
            statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(statusLabel, BorderLayout.EAST);

            refresh();
        }

        /**
         * Redraws the lane based on the typist's current progress.
         * Completed characters shown in green, current position in yellow,
         * remaining characters shown dimly.
         */
        public void refresh()
        {
            int progress = Math.min(typist.getProgress(), passage.length());

            // Build styled text in the passage pane
            javax.swing.text.StyledDocument doc = passagePane.getStyledDocument();

            // Clear existing text
            try { doc.remove(0, doc.getLength()); } catch (Exception e) {}

            // Define styles
            javax.swing.text.Style base = passagePane.getStyle("base") != null ? passagePane.getStyle("base") : passagePane.addStyle("base", null);
            javax.swing.text.StyleConstants.setFontFamily(base, "Monospaced");
            javax.swing.text.StyleConstants.setFontSize(base, 14);

            javax.swing.text.Style doneStyle = passagePane.getStyle("done") != null ? passagePane.getStyle("done") : passagePane.addStyle("done", base);
            javax.swing.text.StyleConstants.setForeground(doneStyle, charDone);
            javax.swing.text.StyleConstants.setBold(doneStyle, true);

            javax.swing.text.Style curStyle = passagePane.getStyle("cur") != null ? passagePane.getStyle("cur") : passagePane.addStyle("cur", base);
            Color cursorColour = typist.isBurntOut() ? charBurnout
                   : typist.getJustMistyped() ? charMistyped
                   : charCurrent;
            javax.swing.text.StyleConstants.setBackground(curStyle, cursorColour);
            javax.swing.text.StyleConstants.setForeground(curStyle, Color.BLACK);
            javax.swing.text.StyleConstants.setBold(curStyle, true);

            javax.swing.text.Style remainStyle = passagePane.getStyle("remain") != null ? passagePane.getStyle("remain") : passagePane.addStyle("remain", base);
            javax.swing.text.StyleConstants.setForeground(remainStyle, textDim);

            try
            {
                // Completed portion
                if (progress > 0)
                    doc.insertString(doc.getLength(), passage.substring(0, progress), doneStyle);

                // Current character cursor
                if (progress < passage.length())
                    doc.insertString(doc.getLength(), String.valueOf(passage.charAt(progress)), curStyle);

                // Remaining portion
                if (progress + 1 < passage.length())
                    doc.insertString(doc.getLength(), passage.substring(progress + 1), remainStyle);
            }
            catch (Exception e) {}

            // Update status label on the right
            String status = "Accuracy: " + String.format("%.2f", typist.getAccuracy());
            if (typist.isBurntOut())
                status = "BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)  |  " + status;
            else if (typist.getJustMistyped())
                status = "\u2190 mistyped  |  " + status;

            statusLabel.setText(status);
            statusLabel.setForeground(typist.isBurntOut() ? charBurnout
                                    : typist.getJustMistyped() ? charMistyped
                                    : textPrimaryColour);
        }
    }
}