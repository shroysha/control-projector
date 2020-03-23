// Author: Shawn Shroyer
// Date Mar 1, 2012
package dev.shroysha.control.projector.view;

import dev.shroysha.control.projector.controller.ProjectorUtilities;
import dev.shroysha.control.projector.model.Projector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ProjectorRemoteFrame extends JFrame {

    private static ProjectorRemoteFrame frame;
    private ProjectorRemotePanel remotePanel;
    private JLabel ableToSendLabel;
    private JTabbedPane tabbedPane;
    private Projector projector;
    private JLabel thereWasAnErrorLabel;

    public ProjectorRemoteFrame() {
        super("Remote");
        init();
    }

    public static ProjectorRemoteFrame getFrame() {
        return frame;
    }

    private void init() {
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        JMenuBar mb = new MyMenuBar();
        this.setJMenuBar(mb);

        JPanel panel = new JPanel(new BorderLayout());

        projector = new Projector();
        remotePanel = new ProjectorRemotePanel(projector);

        ProjectorRemotePresetsPanel presetsPanel = new ProjectorRemotePresetsPanel(projector);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Remote", remotePanel);
        tabbedPane.addTab("Presets", presetsPanel);

        ableToSendLabel = new JLabel("Able to send commands");
        thereWasAnErrorLabel = new JLabel("");

        JPanel temp = new JPanel(new BorderLayout());

        temp.add(ableToSendLabel, BorderLayout.WEST);
        temp.add(thereWasAnErrorLabel, BorderLayout.EAST);

        panel.add(temp, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        this.add(panel, BorderLayout.CENTER);
        this.pack();

        frame = this;
    }

    public ProjectorRemotePanel getRemotePanel() {
        return remotePanel;
    }

    public void setAbleToSendText(String message) {
        ableToSendLabel.setText(message);
    }

    public void setThereWasAnErrorLabelText(String message) {
        thereWasAnErrorLabel.setText(message);
    }

    public void switchToRemote() {
        tabbedPane.setSelectedIndex(0);
    }

    public static class ProjectorRemotePanel extends JPanel {

        private final Projector projector;
        private final int CIRCLE_DIAMETER = 20;
        private final String[] colorModes = {"sRGB", "Presentation", "Theatre", "Game", "Black Board", "Photo"};
        private final String[] sources = {"COMP1", "COMP2", "sVideo", "Video"};
        private boolean init;
        private String lastCMODE = "";
        private boolean updating;
        private String lastSource = "";
        private JToggleButton comp1Button, comp2Button, svideoButton, videoButton;
        private JButton powerButton;
        private JButton muteButton;
        private JButton freezeButton;
        private JComboBox colorModeBox;
        private JSlider brightnessSlider;
        private JSlider contrastSlider;
        private JSpinner brightnessSpinner;
        private JSpinner contrastSpinner;

        /**
         * Creates new form ProjectorRemotePanel
         */
        public ProjectorRemotePanel(Projector projector) {
            this.projector = projector;
            initComponents();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            ArrayList<JComponent> components = new ArrayList<>();
            components.add(powerButton);
            components.add(muteButton);
            components.add(freezeButton);

            for (JComponent comp : components) {
                if (comp instanceof JButton) {
                    int x = comp.getParent().getX() + comp.getX() + comp.getWidth() + 5;
                    int y = comp.getParent().getY() + (comp.getY() + comp.getHeight()) / 2 - CIRCLE_DIAMETER / 2;

                    if (comp.equals(powerButton)) {
                        if (projector.isPowerOn())
                            g.setColor(Color.green);
                        else if (projector.isPowerWaiting())
                            g.setColor(Color.orange);
                        else
                            g.setColor(Color.red);

                    } else if (comp.equals(muteButton)) {
                        if (projector.isMuteOn())
                            g.setColor(Color.green);
                        else
                            g.setColor(Color.red);
                    } else if (comp.equals(freezeButton)) {
                        if (projector.isFreezeOn())
                            g.setColor(Color.green);
                        else
                            g.setColor(Color.red);
                    }

                    g.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
                }
            }

        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {
            init = true;

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(new EmptyBorder(10, 10, 10, 10 + CIRCLE_DIAMETER + 5)); // the 20 is for the circles OC wants which will be 10 pizels big.

            comp1Button = new JToggleButton("COMP1");
            comp1Button.addActionListener(ae -> {
                if (!updating) {
                    projector.setSourceValue("COMP1");
                    updateGUI();
                    projector.execCommand("SOURCE", ProjectorUtilities.sourceToInt("COMP1"));
                }
            });

            comp2Button = new JToggleButton("COMP2");
            comp2Button.addActionListener(ae -> {
                if (!updating) {
                    projector.setSourceValue("COMP2");
                    updateGUI();
                    projector.execCommand("SOURCE", ProjectorUtilities.sourceToInt("COMP2"));
                }
            });

            svideoButton = new JToggleButton("sVideo");
            svideoButton.addActionListener(ae -> {
                if (!updating) {
                    projector.setSourceValue("sVideo");
                    updateGUI();
                    projector.execCommand("SOURCE", ProjectorUtilities.sourceToInt("sVideo"));
                }
            });

            videoButton = new JToggleButton("Video");
            videoButton.addActionListener(ae -> {
                if (!updating) {
                    projector.setSourceValue("Video");
                    updateGUI();
                    projector.execCommand("SOURCE", ProjectorUtilities.sourceToInt("Video"));
                }

            });

            ButtonGroup bGroup = new ButtonGroup();
            bGroup.add(comp1Button);
            bGroup.add(comp2Button);
            bGroup.add(svideoButton);
            bGroup.add(videoButton);

            powerButton = new JButton("Power");//get this
            powerButton.addActionListener(ae -> {
                if (projector.isPowerOn()) {
                    projector.execCommand("PWR", "OFF");
                } else {
                    projector.execCommand("PWR", "ON");
                }
                projector.setPowerValue("Waiting");
                updateGUI();
            });

            muteButton = new JButton("Mute"); //get this
            muteButton.addActionListener(ae -> {
                if (projector.isMuteOn()) {
                    updateGUI();
                    projector.execCommand("MUTE", "OFF");
                } else {
                    updateGUI();
                    projector.execCommand("MUTE", "ON");
                }
            });

            freezeButton = new JButton("Freeze");
            freezeButton.addActionListener(ae -> {
                if (projector.isFreezeOn()) {
                    updateGUI();
                    projector.execCommand("FREEZE", "OFF");
                } else {
                    updateGUI();
                    projector.execCommand("FREEZE", "ON");
                }
            });

            JLabel sourceLabel = new JLabel("Source");
            JLabel powerLabel = new JLabel("Power");
            JLabel muteLabel = new JLabel("Mute");
            JLabel freezeLabel = new JLabel("Freeze");
            // Variables declaration - do not modify
            JLabel colorModeLabel = new JLabel("Color Mode");
            JLabel brightnessLabel = new JLabel("Brightness");
            JLabel contrastLabel = new JLabel("Contrast");

            colorModeBox = new JComboBox(colorModes);
            colorModeBox.setSelectedIndex(0);
            colorModeBox.addActionListener(ae -> {
                String selected = (String) colorModeBox.getSelectedItem();
                if (!init)
                    projector.execCommand("CMODE", ProjectorUtilities.colorModeToInt(selected));
            });

            // int min, int max, int value
            int MAX_VALUE = 255;
            brightnessSlider = new JSlider(0, MAX_VALUE, projector.getBrightnessValue());
            brightnessSlider.addMouseListener(new MouseAdapter() {

                public void mouseReleased(MouseEvent me) {
                    super.mouseReleased(me);
                    int changedTo = brightnessSlider.getValue();
                    projector.setBrightnessValue(changedTo);
                    updateGUI();
                    projector.execCommand("BRIGHT", "" + changedTo);
                }

            });


            contrastSlider = new JSlider(0, MAX_VALUE, projector.getContrastValue());
            contrastSlider.addMouseListener(new MouseAdapter() {

                public void mouseReleased(MouseEvent me) {
                    super.mouseReleased(me);
                    int changedTo = contrastSlider.getValue();
                    projector.setContrastValue(changedTo);
                    updateGUI();
                    projector.execCommand("CONTRAST", "" + changedTo);
                }

            });

            SpinnerModel brightnessModel = new SpinnerNumberModel(projector.getBrightnessValue(), //initial value
                    0, //min
                    MAX_VALUE, //max
                    5); //step
            brightnessSpinner = new JSpinner(brightnessModel);
            brightnessSpinner.addChangeListener(ce -> {
                if (!updating) {
                    int changedTo = Integer.parseInt(brightnessSpinner.getValue().toString());
                    projector.setBrightnessValue(changedTo);
                    updateGUI();
                    projector.execCommand("BRIGHT", "" + changedTo);
                }
            });

            SpinnerModel contrastModel = new SpinnerNumberModel(projector.getContrastValue(), //initial value
                    0, //min
                    MAX_VALUE, //max
                    5); //step
            contrastSpinner = new JSpinner(contrastModel);
            contrastSpinner.addChangeListener(ce -> {
                if (!updating) {
                    int changedTo = Integer.parseInt(contrastSpinner.getValue().toString());
                    projector.setContrastValue(changedTo);
                    updateGUI();
                    projector.execCommand("CONTRAST", "" + changedTo);
                }
            });

            JPanel sourcePanel = new JPanel(new BorderLayout());
            JToolBar buttonPanel = new JToolBar();
            buttonPanel.setRollover(false);
            buttonPanel.setFloatable(false);
            buttonPanel.setLayout(new GridLayout(1, 4));
            buttonPanel.add(comp1Button);
            buttonPanel.add(comp2Button);
            buttonPanel.add(svideoButton);
            buttonPanel.add(videoButton);
            sourcePanel.add(sourceLabel, BorderLayout.WEST);
            sourcePanel.add(buttonPanel, BorderLayout.CENTER);

            JPanel powerPanel = new JPanel(new BorderLayout());
            powerPanel.add(powerLabel, BorderLayout.WEST);
            powerPanel.add(powerButton, BorderLayout.EAST);

            JPanel mutePanel = new JPanel(new BorderLayout());
            mutePanel.add(muteLabel, BorderLayout.WEST);
            mutePanel.add(muteButton, BorderLayout.EAST);

            JPanel freezePanel = new JPanel(new BorderLayout());
            freezePanel.add(freezeLabel, BorderLayout.WEST);
            freezePanel.add(freezeButton, BorderLayout.EAST);

            JPanel colorModePanel = new JPanel(new BorderLayout());
            colorModePanel.add(colorModeLabel, BorderLayout.WEST);
            colorModePanel.add(colorModeBox, BorderLayout.EAST);

            JPanel brightnessPanel = new JPanel(new BorderLayout());
            brightnessPanel.add(brightnessSlider, BorderLayout.CENTER);
            brightnessPanel.add(brightnessLabel, BorderLayout.NORTH);
            brightnessPanel.add(brightnessSpinner, BorderLayout.EAST);

            JPanel contrastPanel = new JPanel(new BorderLayout());
            contrastPanel.add(contrastSlider, BorderLayout.CENTER);
            contrastPanel.add(contrastLabel, BorderLayout.NORTH);
            contrastPanel.add(contrastSpinner, BorderLayout.EAST);

            sourcePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            powerPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            mutePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            freezePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            colorModePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            brightnessPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
            contrastPanel.setAlignmentX(Box.LEFT_ALIGNMENT);


            this.add(powerPanel);
            this.add(Box.createVerticalStrut(5));
            this.add(mutePanel);
            this.add(Box.createVerticalStrut(5));
            this.add(sourcePanel);
            this.add(Box.createVerticalStrut(5));
            this.add(freezePanel);
            this.add(Box.createVerticalStrut(5));
            this.add(colorModePanel);
            this.add(Box.createVerticalStrut(5));
            this.add(brightnessPanel);
            this.add(Box.createVerticalStrut(5));
            this.add(contrastPanel);

            updateGUI();

            init = false;
        }// </editor-fold>

        public void updateGUI() {
            System.out.println("Updating GUI");
            updating = true;

            if (projector.isPowerOn()) { // if power is on
                muteButton.setEnabled(true);
                if (!projector.isMuteOn()) { // and the mute is off
                    isMutedUpdateGUI(true);

                    int index = -1;
                    String mode = projector.getColorModeValue();
                    for (int i = 0; i < colorModes.length && index == -1; i++) {
                        if (colorModes[i].equals(mode)) {
                            index = i;
                            if (!lastCMODE.equals(colorModes[i])) {
                                colorModeBox.setSelectedIndex(index);
                            }

                            lastCMODE = colorModes[i];
                        }
                    }

                    index = -1;
                    String source = projector.getSourceValue();
                    for (int i = 0; i < sources.length && index == -1; i++) {
                        if (sources[i].equals(source)) {
                            index = i;
                            if (!lastSource.equals(sources[i])) {
                                if (sources[i].equals("COMP1")) {
                                    comp1Button.doClick();
                                }
                                if (sources[i].equals("COMP2")) {
                                    comp2Button.doClick();
                                }
                                if (sources[i].equals("Video")) {
                                    videoButton.doClick();
                                }
                                if (sources[i].equals("sVideo")) {
                                    svideoButton.doClick();
                                }
                            }

                            lastSource = sources[i];
                        }
                    }

                    brightnessSlider.setValue(projector.getBrightnessValue());
                    contrastSlider.setValue(projector.getContrastValue());
                    brightnessSpinner.setValue(projector.getBrightnessValue());
                    contrastSpinner.setValue(projector.getContrastValue());
                } else {
                    isMutedUpdateGUI(false);
                }
            } else {
                muteButton.setEnabled(false);
                isMutedUpdateGUI(false);
            }

            repaint();

            updating = false;
            System.out.println("Done updating GUI");
        }

        private void isMutedUpdateGUI(boolean activated) {
            freezeButton.setEnabled(activated);
            brightnessSlider.setEnabled(activated);
            brightnessSpinner.setEnabled(activated);
            contrastSlider.setEnabled(activated);
            contrastSpinner.setEnabled(activated);
            colorModeBox.setEnabled(activated);

            comp1Button.setEnabled(activated);
            comp2Button.setEnabled(activated);
            svideoButton.setEnabled(activated);
            videoButton.setEnabled(activated);
        }

        public void setEnabledToAll(boolean activated) {
            powerButton.setEnabled(activated);
            muteButton.setEnabled(activated);

            isMutedUpdateGUI(activated);
        }
        // End of variables declaration
    }

    private class MyMenuBar extends JMenuBar {

        public MyMenuBar() {
            super();
            init();
        }

        private void init() {

            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu commandMenu = new JMenu("Commands");
            JMenuItem closeItem = new JMenuItem("Close");
            JMenu serverEditMenu = new JMenu("Server Properties");

            JMenuItem editIPItem = new JMenuItem("Edit IP");
            JMenuItem editPortItem = new JMenuItem("Edit Port");
            JMenuItem sendCommandItem = new JMenuItem("Send Command");
            JMenuItem getStatusItem = new JMenuItem("Get Status");
            JMenuItem scanItem = new JMenuItem("Scan");

            closeItem.addActionListener(ae -> System.exit(54));

            editIPItem.addActionListener(ae -> projector.editIP());

            editPortItem.addActionListener(ae -> projector.editPort());

            sendCommandItem.addActionListener(ae -> projector.execCommand());

            getStatusItem.addActionListener(ae -> projector.getStatus());

            scanItem.addActionListener(ae -> {
                projector.scan();
            });

            fileMenu.add(closeItem);

            editMenu.add(serverEditMenu);
            serverEditMenu.add(editIPItem);
            serverEditMenu.add(editPortItem);

            commandMenu.add(sendCommandItem);
            commandMenu.add(getStatusItem);
            commandMenu.add(scanItem);

            this.add(fileMenu);
            this.add(editMenu);
            this.add(commandMenu);
        }
    }
}
