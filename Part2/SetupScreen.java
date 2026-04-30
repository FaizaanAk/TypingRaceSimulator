import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SetupScreen extends JPanel
{

    // Pre-defined passages
    private static final String[] passageLabels = {"Short", "Medium", "Long", "Custom" };
    private static final String[] passages = { "I practice typing every day.", "His dog barks loudly. She loves to play fetch in the park.", "She enjoys reading books in the library. It is nice to have a quiet place to study and learn new things.", "Sports are fun to play and watch. They can be a great way to stay active and socialise with friends.Some examples include football, basketball, and tennis."};

    // Typing style options and their accuracy modifiers
    private static final String[] typingStyles = { "Touch Typist", "Hunt & Peck", "Phone Thumbs", "Voice-to-Text" };
    private static final double[] styleModifiers = {  0.10,           -0.10,          -0.05,           -0.15 };

    // Keyboard type options and their speed modifiers
    private static final String[] keyboardTypes = { "Mechanical", "Membrane", "Touchscreen", "Stenography" };
    private static final double[] keyboardModifiers = { 0.05,         0.00,       -0.10,          0.15 };

    // Accessory options
    private static final String[] accessories = { "Wrist Support", "Energy Drink", "Noise-Cancelling Headphones" };

    // UI references for collecting settings
    private JFrame parentFrame;
    private JComboBox<String> passageCombo;
    private JTextArea customPassageArea;
    private JSpinner seatCountSpinner;
    private JCheckBox autocorrectBox;
    private JCheckBox caffeineModeBox;
    private JCheckBox nightShiftBox;

    // Per-typist panels
    private ArrayList<TypistConfigPanel> typistPanels;
    private JPanel typistPanelContainer;
    private JTabbedPane tabs;

    // Constructor
    public SetupScreen(JFrame parentFrame)
    {
        this.parentFrame = parentFrame;
        typistPanels = new ArrayList<>();

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(30, 30, 40));

        // Title
        JLabel title = new JLabel("TYPING RACE SIMULATOR", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 26));
        title.setForeground(new Color(0, 220, 180));

        title.setOpaque(true);
        title.setBackground(new Color(20, 20, 30));
        title.setBorder(BorderFactory.createEmptyBorder(14, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Main content panels
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Monospaced", Font.BOLD, 13));
        tabs.setBackground(new Color(30, 30, 40));
        tabs.setForeground(new Color(0, 220, 180));

        tabs.addTab("  Race Configuration  ", buildRaceSetupScreen());
         tabs.addTab("  Typist Customisation  ", buildTypistSetupScreen());

        add(tabs, BorderLayout.CENTER);

        // Start button
        JButton startButton = new JButton("START RACE");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        startButton.setBackground(new Color(0, 180, 140));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startRace());
        add(startButton, BorderLayout.SOUTH);

        // Initialise with 2 typist panels
        updateTypistPanels(2);
    }

    // Build left panel — race configuration
    private JPanel buildRaceSetupScreen()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 140)),
            "Race Configuration",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 13),
            new Color(0, 180, 140)
        ));

        //Passage selection
        panel.add(makeLabel("Passage Length:"));
        passageCombo = new JComboBox<>(passageLabels);
        passageCombo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        passageCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passageCombo.setBackground(new Color(55, 55, 70));
        passageCombo.setForeground(Color.WHITE);
        panel.add(passageCombo);
        panel.add(Box.createVerticalStrut(8));

        panel.add(makeLabel("Custom Passage (if selected above):"));
        customPassageArea = new JTextArea(3, 20);
        customPassageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);
        customPassageArea.setBackground(new Color(55, 55, 70));
        customPassageArea.setForeground(Color.WHITE);
        customPassageArea.setCaretColor(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(customPassageArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(12));

        // Seat count
        panel.add(makeLabel("Number of Typists (2-6):"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, 6, 1);
        seatCountSpinner = new JSpinner(spinnerModel);
        seatCountSpinner.setFont(new Font("Monospaced", Font.PLAIN, 13));
        seatCountSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        seatCountSpinner.addChangeListener(e ->
            updateTypistPanels((int) seatCountSpinner.getValue()));
        panel.add(seatCountSpinner);
        panel.add(Box.createVerticalStrut(12));

        // Difficulty modifiers
        panel.add(makeLabel("Difficulty Modifiers:"));

        autocorrectBox = makeCheckBox("Autocorrect On (slideBack halved)");
        caffeineModeBox = makeCheckBox("Caffeine Mode (speed boost then burnout)");
        nightShiftBox   = makeCheckBox("Night Shift (accuracy reduced)");

        panel.add(autocorrectBox);
        panel.add(caffeineModeBox);
        panel.add(nightShiftBox);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Build right panel — typist configuration
    private JPanel buildTypistSetupScreen()
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(40, 40, 55));
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 140)),
            "Typist Configuration",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 13),
            new Color(0, 180, 140)
        ));

        typistPanelContainer = new JPanel();
        typistPanelContainer.setLayout(new BoxLayout(typistPanelContainer, BoxLayout.Y_AXIS));
        typistPanelContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(typistPanelContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    // Add/remove typist panels when seat count changes
    private void updateTypistPanels(int count)
    {
        typistPanels.clear();
        typistPanelContainer.removeAll();

        String[] defaultNames = {"TURBOFINGERS", "QWERTY_QUEEN", "HUNT_N_PECK", "TYPEMASTER", "SPEEDSTER", "TYPEZILLA"};
        String[] defaultSymbols = {"①", "②", "③", "④", "⑤", "⑥" };
        double[] defaultAccuracy = {0.85, 0.60, 0.30, 0.70, 0.50, 0.40};

        for (int i = 0; i < count; i++)
        {
            TypistConfigPanel panel = new TypistConfigPanel(i + 1, defaultNames[i], defaultSymbols[i], defaultAccuracy[i]);
            typistPanels.add(panel);
            typistPanelContainer.add(panel);
            typistPanelContainer.add(Box.createVerticalStrut(8));
        }

        typistPanelContainer.revalidate();
        typistPanelContainer.repaint();
    }

    // Collect settings and launch the race screen
    private void startRace()
    {
        // Determine passage
        String passage;
        int selectedPassage = passageCombo.getSelectedIndex();
        if (selectedPassage == 3)
        {
            passage = customPassageArea.getText().trim();
            if (passage.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Please enter a custom passage or select a pre-defined one", "No Passage", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            passage = passages[selectedPassage];
        }

        // Collect difficulty modifiers
        boolean autocorrect = autocorrectBox.isSelected();
        boolean caffeineMode = caffeineModeBox.isSelected();
        boolean nightShift = nightShiftBox.isSelected();

        // Build typist list
        Typist[] typists = new Typist[typistPanels.size()];
        for (int i = 0; i < typistPanels.size(); i++)
        {
            typists[i] = typistPanels.get(i).buildTypist(nightShift);
        }

        // Launch race screen
        RaceDisplay raceScreen = new RaceDisplay(parentFrame, typists, passage, autocorrect, caffeineMode, nightShift);
        parentFrame.setContentPane(raceScreen);
        parentFrame.revalidate();
    }

    // Helper methods for building UI components
    private JLabel makeLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.BOLD, 12));
        label.setForeground(new Color(180, 180, 200));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JCheckBox makeCheckBox(String text)
    {
        JCheckBox box = new JCheckBox(text);
        box.setFont(new Font("Monospaced", Font.PLAIN, 12));
        box.setForeground(new Color(180, 180, 200));
        box.setBackground(new Color(40, 40, 55));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    // Inner class — per-typist configuration panel
    class TypistConfigPanel extends JPanel
    {
        private JTextField nameField;
        private JTextField symbolField;
        private JComboBox<String> styleCombo;
        private JComboBox<String> keyboardCombo;
        private JCheckBox[] accessoryBoxes;
        private double baseAccuracy;

        public TypistConfigPanel(int seatNumber, String defaultName, String defaultSymbol, double baseAccuracy)
        {
            this.baseAccuracy = baseAccuracy;

            setLayout(new GridLayout(0, 2, 5, 3));
            setBackground(new Color(50, 50, 65));
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80, 80, 100)), "Typist " + seatNumber, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 11), new Color(150, 150, 200)));

            setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

            add(smallLabel("Name:"));
            nameField = new JTextField(defaultName);
            styleTextField(nameField);
            add(nameField);

            add(smallLabel("Symbol:"));
            symbolField = new JTextField(defaultSymbol);
            styleTextField(symbolField);
            add(symbolField);

            add(smallLabel("Typing Style:"));
            styleCombo = new JComboBox<>(typingStyles);
            styleCombo(styleCombo);
            add(styleCombo);

            add(smallLabel("Keyboard:"));
            keyboardCombo = new JComboBox<>(keyboardTypes);
            styleCombo(keyboardCombo);
            add(keyboardCombo);

            add(smallLabel("Accessories:"));
            JPanel accPanel = new JPanel(new GridLayout(0, 1));
            accPanel.setOpaque(false);
            accessoryBoxes = new JCheckBox[accessories.length];
            for (int i = 0; i < accessories.length; i++)
            {
                accessoryBoxes[i] = new JCheckBox(accessories[i]);
                accessoryBoxes[i].setFont(new Font("Monospaced", Font.PLAIN, 10));
                accessoryBoxes[i].setForeground(new Color(180, 180, 200));
                accessoryBoxes[i].setBackground(new Color(50, 50, 65));
                accPanel.add(accessoryBoxes[i]);
            }
            add(accPanel);
        }

        //Builds a Typist object from the current panel settings and applies all modifiers from typing style, keyboard, accessories and night shift.
        public Typist buildTypist(boolean nightShift)
        {
            String name = nameField.getText().trim().toUpperCase();
            String symStr = symbolField.getText().trim();
            char symbol = symStr.isEmpty() ? '?' : symStr.charAt(0);

            // Start from base accuracy and apply modifiers
            double accuracy = baseAccuracy;
            accuracy += styleModifiers[styleCombo.getSelectedIndex()];
            accuracy += keyboardModifiers[keyboardCombo.getSelectedIndex()];

            // Night shift reduces everyone's accuracy
            if (nightShift)
            {
                accuracy = accuracy - 0.05;
            }
            Typist t = new Typist(symbol, name, accuracy);
            return t;
        }

        public boolean hasWristSupport()   
        { 
            return accessoryBoxes[0].isSelected(); 
        }

        public boolean hasEnergyDrink()    
        { 
            return accessoryBoxes[1].isSelected(); 
        }

        public boolean hasHeadphones()     
        {
             return accessoryBoxes[2].isSelected(); 
        }

        public String getTypistName()      
        { 
            return nameField.getText().trim().toUpperCase(); 
        }

        private JLabel smallLabel(String text)
        {
            JLabel l = new JLabel(text);
            l.setFont(new Font("Monospaced", Font.BOLD, 11));
            l.setForeground(new Color(160, 160, 190));
            return l;
        }

        private void styleTextField(JTextField field)
        {
            field.setFont(new Font("Monospaced", Font.PLAIN, 12));
            field.setBackground(new Color(65, 65, 80));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 100)));
        }

        private void styleCombo(JComboBox<String> combo)
        {
            combo.setFont(new Font("Monospaced", Font.PLAIN, 11));
            combo.setBackground(new Color(65, 65, 80));
            combo.setForeground(Color.WHITE);
        }
    }

    // Static accessors used by RaceScreen to read accessory settings
    public ArrayList<TypistConfigPanel> getTypistPanels()
    {
        return typistPanels;
    }
}