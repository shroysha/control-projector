package dev.shroysha.control.projector.view;

import dev.shroysha.control.projector.model.Projector;
import dev.shroysha.control.projector.model.ProjectorRemotePresets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class ProjectorRemotePresetsPanel extends JPanel {

    private final Projector currentProj;
    private final JPanel buttonPanel = new JPanel(new FlowLayout());
    private final JComboBox<ProjectorRemotePresets> presetsBox = new JComboBox<>();
    private final JLabel presetNameLabel = new JLabel("Preset Name");
    private final JLabel colorModeLabel = new JLabel("Color Mode");
    private final JLabel brightnessLabel = new JLabel("Brightness");
    private final JLabel contrastLabel = new JLabel("Contrast");
    private final JButton executePresetButton = new JButton("Execute");
    private final JButton newPresetButton = new JButton("New");
    private final JButton editPresetButton = new JButton("Edit");
    private final JButton removePresetButton = new JButton("Remove");
    private ProjectorRemotePresets current;
    private int cursor;
    private boolean showingPreset = false;

    public ProjectorRemotePresetsPanel(Projector currentProj) {
        super();
        this.currentProj = currentProj;
        init();
    }

    private void init() {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        cursor = 0;

        presetsBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        presetNameLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        colorModeLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        brightnessLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        contrastLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

        executePresetButton.addActionListener(ae -> current.executePreset(currentProj));

        newPresetButton.addActionListener(ae -> {
            NewPresetDialog npd = new NewPresetDialog();
            npd.setVisible(true);
            ProjectorRemotePresets newPreset = npd.getPreset();
            if (newPreset != null) {
                ProjectorRemotePresets.addPreset(newPreset);
                cursor = ProjectorRemotePresets.getCurrentPresets().length - 1;
                updateGUI();
            } else System.out.println("null");
        });

        editPresetButton.addActionListener(ae -> {
            NewPresetDialog npd = new NewPresetDialog(current);
            npd.setVisible(true);
            updateGUI();
        });

        removePresetButton.addActionListener(ae -> {
            ProjectorRemotePresets.removePreset(cursor);
            cursor--;
            updateGUI();
        });

        presetsBox.addActionListener(ae -> {
            cursor = presetsBox.getSelectedIndex();
            updateGUI();
        });

        buttonPanel.add(executePresetButton);
        buttonPanel.add(newPresetButton);
        buttonPanel.add(editPresetButton);
        buttonPanel.add(removePresetButton);

        updateGUI();
    }

    private void updateGUI() {
        ProjectorRemotePresets[] presets = ProjectorRemotePresets.getCurrentPresets();

        boolean ableToShow = presets.length != 0;

        Object[] presetNames = new Object[presets.length];
        for (int i = 0; i < presetNames.length; i++) {
            presetNames[i] = presets[i].getPresetName();
        }

        System.out.println("AbleToShow = " + ableToShow);
        System.out.println("Showing preset = " + showingPreset);

        if (!ableToShow) {
            if (showingPreset) {
                this.removeAll();

                this.setLayout(new BorderLayout());
                JLabel labe = new JLabel("No Presets");
                labe.setHorizontalTextPosition(JLabel.CENTER);

                this.add(labe, BorderLayout.CENTER);
                this.add(newPresetButton, BorderLayout.SOUTH);

                this.revalidate();
            }
            showingPreset = false;

        } else {
            if (presetsBox.getSelectedIndex() != -1)
                current = presets[presetsBox.getSelectedIndex()];
            else
                current = presets[cursor];

            if (showingPreset) {
                presetsBox.setModel(new DefaultComboBoxModel<>(presets));
                presetNameLabel.setText("Name: " + current.getPresetName());
                colorModeLabel.setText("Color Mode: " + current.getColorMode());
                brightnessLabel.setText("Brightness: " + current.getBrightness());
                contrastLabel.setText("Contrast: " + current.getContrast());
            } else {
                removeAll();
                this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

                presetsBox.setModel(new DefaultComboBoxModel<>(presets));
                presetNameLabel.setText("Name: " + current.getPresetName());
                colorModeLabel.setText("Color Mode: " + current.getColorMode());
                brightnessLabel.setText("Brightness: " + current.getBrightness());
                contrastLabel.setText("Contrast: " + current.getContrast());

                this.add(presetsBox);
                this.add(Box.createVerticalStrut(10));
                this.add(presetNameLabel);
                this.add(Box.createVerticalStrut(10));
                this.add(colorModeLabel);
                this.add(Box.createVerticalStrut(10));
                this.add(brightnessLabel);
                this.add(Box.createVerticalStrut(10));
                this.add(contrastLabel);
                this.add(Box.createVerticalStrut(10));
                this.add(buttonPanel);

                this.revalidate();
            }

            showingPreset = true;
        }


    }

    public static class NewPresetDialog extends JDialog {

        public static final String[] COLOR_MODES = {"sRGB", "Presentation", "Theatre", "Game", "Sports", "Black Board", "Photo"};


        private final String initName, initColorMode;
        private final int initBright, initContrast;
        private final ProjectorRemotePresets oldPreset;
        private JTextField nameField;
        private JSlider brightnessSlider, contrastSlider;
        private JComboBox colorModeBox;
        private boolean confirmed, edit = true;

        public NewPresetDialog() {
            this(new ProjectorRemotePresets("", "", 0, 0));
            edit = false;
        }

        public NewPresetDialog(ProjectorRemotePresets preset) {
            super(ProjectorRemoteFrame.getFrame(), true);

            this.oldPreset = preset;
            this.initName = preset.getPresetName();
            this.initColorMode = preset.getColorMode();
            this.initBright = preset.getBrightness();
            this.initContrast = preset.getContrast();

            init();
        }

        private void init() {
            this.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel("Name:");

            nameField = new JTextField();
            nameField.setText(initName);

            JLabel colorLabel = new JLabel("Color mode:");
            colorModeBox = new JComboBox<>(COLOR_MODES);
            colorModeBox.setSelectedItem(initColorMode);

            JLabel brightnessLabel = new JLabel("Brightness:");
            int MAX_VALUE = 255;
            brightnessSlider = new JSlider(0, MAX_VALUE, initBright);

            JLabel contrastLabel = new JLabel("Contrast:");
            contrastSlider = new JSlider(0, MAX_VALUE, initContrast);

            JButton confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(ae -> {
                confirmed = true;
                NewPresetDialog.this.dispose();
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(ae -> {
                confirmed = false;
                NewPresetDialog.this.dispose();
            });

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            nameLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
            nameField.setAlignmentX(Box.LEFT_ALIGNMENT);
            colorLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
            colorModeBox.setAlignmentX(Box.LEFT_ALIGNMENT);
            brightnessLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
            brightnessSlider.setAlignmentX(Box.LEFT_ALIGNMENT);
            contrastLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
            contrastSlider.setAlignmentX(Box.LEFT_ALIGNMENT);
            buttonPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

            panel.add(nameLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));

            panel.add(colorLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(colorModeBox);
            panel.add(Box.createVerticalStrut(10));

            panel.add(brightnessLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(brightnessSlider);
            panel.add(Box.createVerticalStrut(10));

            panel.add(contrastLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(contrastSlider);
            panel.add(Box.createVerticalStrut(10));

            panel.add(buttonPanel);


            this.add(panel, BorderLayout.CENTER);
            this.pack();
        }

        public ProjectorRemotePresets getPreset() {
            if (confirmed) {
                String name = nameField.getText().trim();
                String colorMode = (String) colorModeBox.getSelectedItem();
                int brightness = brightnessSlider.getValue();
                int contrast = contrastSlider.getValue();
                if (edit) {
                    oldPreset.setPresetName(name);
                    oldPreset.setColorMode(colorMode);
                    oldPreset.setBrightness(brightness);
                    oldPreset.setContrast(contrast);

                    return null;
                } else {
                    return new ProjectorRemotePresets(name, colorMode, brightness, contrast);
                }
            } else {
                return null;
            }


        }
    }
}
