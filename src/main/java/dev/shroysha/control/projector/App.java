package dev.shroysha.control.projector;

import dev.shroysha.control.projector.model.Projector;
import dev.shroysha.control.projector.model.ProjectorRemotePresets;
import dev.shroysha.control.projector.util.ProjectorRemoteResources;
import dev.shroysha.control.projector.view.ProjectorRemoteFrame;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App {

    private static boolean connected;
    private static Timer connectedTimer;

    public static void main(String[] args) {
        run();
    }


    private static void run() {
        try {
            if (!ProjectorRemoteResources.PROJECTOR_DIR.exists()) {
                startup();
            }

            String lockStatus = getLock();
            if (lockStatus.equals("OFF")) {
                Runtime.getRuntime().addShutdownHook(new ShutdownHook());

                System.out.println(System.getProperty("os.name"));
                if (System.getProperty("os.name").equals("Mac OS X")) {
                    System.out.println("true");
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Projector Project");

                }
                ProjectorRemotePresets.readAllPresetsFromFile();
                Projector.ProjectorSettings.readSettingsFromFile();

                connected = false;
                connectedTimer = new Timer(5000, ae -> {
                    if (!connected) {
                        System.out.println("fds");
                        int n = JOptionPane.showConfirmDialog(null, "The BAUD Rate needs to be reset", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        if (n != JOptionPane.OK_OPTION) {
                            System.exit(-1);
                        }
                    }
                    System.out.println("rfefe");
                    connectedTimer.stop();
                });
                connectedTimer.start();

                ProjectorRemoteFrame frame = new ProjectorRemoteFrame();
                frame.pack();
                frame.setVisible(true);
                connected = true;

                setLock("ON");
            }
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(null, ex);
            ex.printStackTrace(System.err);
        }
    }

    private static String getLock() throws IOException {
        Scanner lockScanner = new Scanner(ProjectorRemoteResources.LOCK_FILE);
        String line = lockScanner.nextLine().trim();
        lockScanner.close();
        return line;
    }

    private static void setLock(String lock) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(ProjectorRemoteResources.LOCK_FILE, false));
        writer.write(lock);
        writer.close();
    }

    private static void startup() throws IOException {
        ProjectorRemoteResources.PROJECTOR_DIR.mkdir();
        ProjectorRemoteResources.LOCK_FILE.createNewFile();
        ProjectorRemoteResources.PRESETS_FILE.createNewFile();
        ProjectorRemoteResources.SETTINGS_FILE.createNewFile();

        FileWriter writer = new FileWriter(ProjectorRemoteResources.LOCK_FILE);
        writer.write("OFF");
        writer.close();

        writer = new FileWriter(ProjectorRemoteResources.PRESETS_FILE);
        writer.write("Normal\n");
        writer.write("Presentation\n");
        writer.write("133\n");
        writer.write("133");
        writer.close();

        writer = new FileWriter(ProjectorRemoteResources.SETTINGS_FILE);
        writer.write("10.208.0.3\n");
        writer.write("4000");
        writer.close();
    }

    private static class ShutdownHook extends Thread {

        public void run() {
            try {
                Projector.getCurrentProjector().closeAll();
                ProjectorRemotePresets.writeAllToFile();
                Projector.ProjectorSettings.writeFinalSettings();

                setLock("OFF");
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
