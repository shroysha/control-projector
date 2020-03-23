package dev.shroysha.control.projector.model;

import dev.shroysha.control.projector.util.ProjectorRemoteResources;
import dev.shroysha.control.projector.view.ProjectorRemoteFrame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProjectorRemotePresets {

    private static final ArrayList<ProjectorRemotePresets> currentPresets = new ArrayList<>();
    private static final File presetsFile = ProjectorRemoteResources.PRESETS_FILE;
    private String presetName, colorMode;
    private int contrast, brightness;

    public ProjectorRemotePresets(String name, String colorMode, int bright, int contrast) {
        this.presetName = name;
        this.colorMode = colorMode;
        this.brightness = bright;
        this.contrast = contrast;
    }

    public static ProjectorRemotePresets[] getCurrentPresets() {
        return currentPresets.toArray(new ProjectorRemotePresets[0]);
    }

    public static void readAllPresetsFromFile() {
        try {
            Scanner reader = new Scanner(presetsFile);

            // First line should be number of presets
            while (reader.hasNextLine()) {
                currentPresets.add(nextPreset(reader));
            }

        } catch (Exception ex) {
            Logger.getLogger(ProjectorRemotePresets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ProjectorRemotePresets nextPreset(Scanner reader) {
        String aPresetName = reader.nextLine().trim();
        String aColorMode = reader.nextLine().trim();
        int aContrast = Integer.parseInt(reader.nextLine().trim());
        int aBrightness = Integer.parseInt(reader.nextLine().trim());

        return new ProjectorRemotePresets(aPresetName, aColorMode, aBrightness, aContrast);
    }

    public static void writeAllToFile() throws IOException {
        BufferedWriter write = new BufferedWriter(new FileWriter(presetsFile, false));
        for (int i = 0; i < currentPresets.size(); i++) {
            writePreset(currentPresets.get(i), write, i);
        }
        System.out.println("Wrote all");
        write.close();

    }

    private static void writePreset(ProjectorRemotePresets preset, BufferedWriter write, int i) throws IOException {
        write.write(preset.getPresetName());
        write.newLine();
        write.write("" + preset.getColorMode());
        write.newLine();
        write.write("" + preset.getContrast());
        write.newLine();
        write.write("" + preset.getBrightness());
        if (i != currentPresets.size() - 1)
            write.newLine();
    }

    public static void addPreset(ProjectorRemotePresets preset) {
        currentPresets.add(preset);
        System.out.println(currentPresets.toString());
    }

    public static void removePreset(int i) {
        currentPresets.remove(i);
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int bright) {
        this.brightness = bright;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String name) {
        this.presetName = name;
    }

    public void executePreset(Projector projector) {
        ProjectorRemoteFrame.getFrame().switchToRemote();
        projector.execPreset(this);
    }
}
