package dev.shroysha.control.projector.model;

import dev.shroysha.control.projector.controller.ProjectorUtilities;
import dev.shroysha.control.projector.util.ProjectorRemoteResources;
import dev.shroysha.control.projector.view.ProjectorRemoteFrame;

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Projector {

    private static Projector projector;
    private String PROJECTORIP;
    private Socket socket;
    private OutputStream os;
    private Scanner in;
    private String pwr = "", mute = "", freeze = "", colorMode, src;
    private int contrast, brightness;
    private boolean polling = false;
    private boolean ableToSend = true;
    private Queue<ProjectorCommand> toSend;

    public Projector() {
        super();
        startProjectorSocket();
    }

    public static int errorDialog(Exception ex) {
        return JOptionPane.showConfirmDialog(
                ProjectorRemoteFrame.getFrame(), //Parent
                ex.getMessage(), //Message
                "Error", //Title
                JOptionPane.OK_CANCEL_OPTION, //Type
                JOptionPane.ERROR_MESSAGE); //Default
    }

    public static Projector getCurrentProjector() {
        return projector;
    }

    /**
     * This method gets the IP address and the port number from the Settings class and uses them to
     * create a socket to the projector. It also gets the projector ready for commands and does an initial scan.
     */
    private void startProjectorSocket() {
        try {
            PROJECTORIP = ProjectorSettings.getProjectorIP();
            int PORT = ProjectorSettings.getPort();

            System.out.println(PROJECTORIP);
            System.out.println(PORT);

            socket = new Socket(PROJECTORIP, PORT);
            os = socket.getOutputStream();
            in = new Scanner(socket.getInputStream());
            os.write("\r".getBytes()); //Send initial \r
            hold(100);
            scan();
        } catch (Exception ex) {
            Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);

            int n = errorDialog(ex);

            if (n == JOptionPane.YES_OPTION)
                startProjectorSocket();
            else {
                System.exit(2);
            }
        }

        toSend = new LinkedList<>();
        projector = this;

        Scan scanner = new Scan(projector);
        scanner.start();

        Sender send = new Sender();
        send.start();
    }


    public final void execCommand(String command, String option) {
        ProjectorCommand toAdd = new ProjectorCommand(command, option);

        toSend.add(toAdd);
    }


    public final boolean execCommand(ProjectorCommand command) {
        while (!ableToSend) {
            hold(50);
        }

        if (polling) {
            while (polling) {
                hold(50);
            }
        }

        polling = true;
        try {
            String message = command.toString();
            os.write(message.getBytes());
            //System.out.println(in.next(":"));
            System.out.println("Executed command: " + message);

            AbleToSendThread ats = new AbleToSendThread(message);
            ats.start();
        } catch (IOException ex) {
            errorDialog(ex);
        }
        polling = false;

        return true;
    }

    /**
     * This method adds all the commands in the preset to the queue
     */
    public final void execPreset(ProjectorRemotePresets preset) {
        execCommand("CMODE", ProjectorUtilities.colorModeToInt(preset.getColorMode()));
        execCommand("CONTRAST", "" + preset.getContrast());
        execCommand("BRIGHT", "" + preset.getBrightness());
    }

    /**
     * This method is used by the menu bar and shows a JOptionPane asking for a command
     */
    public final void execCommand() {
        String input = JOptionPane.showInputDialog(ProjectorRemoteFrame.getFrame(), "Enter the command").trim().toUpperCase();
        StringTokenizer tokener = new StringTokenizer(input);
        String command = tokener.nextToken();
        String option = tokener.nextToken();
        execCommand(command, option);
    }

    private String getStatus(final String command) throws IOException {
        if (ableToSend) {
            if (polling) {
                while (polling) {
                    hold(50);
                }
            }

            polling = true;

            os.write((command + "?" + "\r").getBytes()); // Asks the projector what the status is

            System.out.println("Waiting on response for " + command);


            String response = in.nextLine().replaceAll(":", "").trim().replace(command + "=", "");

            polling = false;

            System.out.println("Got response: " + response);

            if (response.equals("ERR"))
                throw new ProjectorException("There was an error :/");

            return response;
        } else {
            System.out.println("Cannot poll right now.");
        }

        return null;
    }

    public void getStatus() {
        String input = JOptionPane.showInputDialog("Enter the option you want the status of").trim();
        try {
            String message = getStatus(input);
            JOptionPane.showConfirmDialog(ProjectorRemoteFrame.getFrame(), "The response was:\n" + message);
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(ProjectorRemoteFrame.getFrame(), "There was an error");
        }
    }


    public void scan() {
        if (ableToSend) {
            try {
                System.out.println("Started scan");
                pwr = this.getPowerStatus();
                System.out.println("Scan: Got power status");
                if (isPowerOn()) { //If the power is off, all the other commands will return a null command

                    mute = this.getMuteStatus();
                    System.out.println("Scan: Got mute status");

                    if (!isMuteOn()) {
                        freeze = this.getFreezeStatus();
                        System.out.println("Scan: Got freeze status");

                        contrast = this.getContrastStatus();
                        System.out.println("Scan: Got contrast status");

                        brightness = this.getBrightnessStatus();
                        System.out.println("Scan: Got brightness status");

                        colorMode = this.getColorModeStatus();
                        System.out.println("Scan: got color mode status");

                        src = this.getSourceStatus();
                    }
                } else {
                    mute = "OFF";
                    freeze = "OFF";
                    contrast = 0;
                    brightness = 0;
                }

                if (ProjectorRemoteFrame.getFrame() != null) {
                    ProjectorRemoteFrame.getFrame().getRemotePanel().updateGUI();
                    ProjectorRemoteFrame.getFrame().setThereWasAnErrorLabelText("");
                }
            } catch (ProjectorException ex) {
                if (ProjectorRemoteFrame.getFrame() != null)
                    ProjectorRemoteFrame.getFrame().setThereWasAnErrorLabelText(ex.getMessage());
                int n = errorDialog(ex);
                if (n == JOptionPane.YES_OPTION)
                    scan();

                ProjectorRemoteFrame.getFrame().getRemotePanel().updateGUI();
            } catch (Exception ignored) {
            }

        } else {
            System.out.println("Cannot scan right now.");
        }

        System.out.println("Main polling finished");
    }

    public void editPort() {
        String input = JOptionPane.showInputDialog(ProjectorRemoteFrame.getFrame(), "Enter the new port");
        int newPort = Integer.parseInt(input);
        ProjectorSettings.setPort(newPort);
    }

    private String getPowerStatus() throws IOException {
        final String PWR_OFF = "00";
        final String PWR_ON = "01";

        String powerStatus = getStatus("PWR");
        if (Objects.equals(powerStatus, PWR_ON)) {
            return "ON";
        } else if (powerStatus.equals(PWR_OFF)) {
            return "OFF";
        } else {
            return "UNK";
        }
    }

    private String getMuteStatus() throws IOException {

        return getStatus("MUTE");
    }

    private String getSourceStatus() throws IOException {
        String sourceInt = getStatus("SOURCE");

        assert sourceInt != null;
        return ProjectorUtilities.intToSource(sourceInt);
    }

    private String getFreezeStatus() throws IOException {

        return getStatus("FREEZE");
    }

    private int getBrightnessStatus() throws IOException {
        String brightStatus = getStatus("BRIGHT");

        assert brightStatus != null;
        return Integer.parseInt(brightStatus);
    }

    private int getContrastStatus() throws IOException {
        String contrastStatus = getStatus("CONTRAST");

        assert contrastStatus != null;
        return Integer.parseInt(contrastStatus);
    }

    private String getColorModeStatus() throws IOException {
        String colorModeStatus = getStatus("CMODE");
        /*
         * 01: sRGB
         * 04: Presentation
         * 05: Theatre
         * 06: Game
         * 08: Sports
         * 11: Black Board
         * 14: Photo
         */

        assert colorModeStatus != null;
        return ProjectorUtilities.intToColorMode(colorModeStatus);
    }

    public int getBrightnessValue() {
        return brightness;
    }

    public void setBrightnessValue(int bright) {
        if (isAbleToSend())
            this.brightness = bright;
    }

    public int getContrastValue() {
        return contrast;
    }

    public void setContrastValue(int contrast) {
        if (isAbleToSend())
            this.contrast = contrast;
    }

    public boolean isPowerOn() {
        return pwr.equals("ON");
    }

    public boolean isMuteOn() {
        return mute.equals("ON");
    }

    public boolean isFreezeOn() {
        return freeze.equals("ON");
    }

    public String getColorModeValue() {
        return colorMode;
    }

    public String getSourceValue() {
        return src;
    }

    public void setSourceValue(String source) {
        if (isAbleToSend())
            this.src = source;
    }

    public void setMuteValue(String mute) {
        if (isAbleToSend())
            this.mute = mute;
    }

    public void setFreezeValue(String freeze) {
        if (isAbleToSend())
            this.freeze = freeze;
    }

    public void setPowerValue(String power) {
        if (isAbleToSend())
            this.pwr = power;
    }

    public boolean isAbleToSend() {
        return ableToSend;
    }

    public boolean isPowerWaiting() {
        return pwr.equals("Waiting");
    }

    private void hold(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ex) {
            Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editIP() {
        String newIP = JOptionPane.showInputDialog(ProjectorRemoteFrame.getFrame(), "Old IP: " + PROJECTORIP + "Enter the new IP");

        boolean valid = validateIP(newIP);

        if (valid)
            ProjectorSettings.setProjectorIP(newIP);
        else
            JOptionPane.showConfirmDialog(ProjectorRemoteFrame.getFrame(), "Invalid IP Address.");
    }

    public boolean validateIP(String newIP) {
        try {
            InetAddress temo = InetAddress.getByName(newIP);
            temo.isReachable(ProjectorSettings.getPort());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void closeAll() throws IOException {
        os.write("\r".getBytes());
        os.close();
        in.close();
        socket.close();

    }

    public static class ProjectorCommand {

        private final String first, second;

        public ProjectorCommand(String first, String second) {
            this.first = first;
            this.second = second;
        }


        public String toString() {
            return first.trim() + " " + second.trim() + "\r";
        }

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }
    }

    public static class ProjectorSettings {

        private static String projectorIP;
        private static int port;

        public static void readSettingsFromFile() throws IOException {
            FileReader fr = new FileReader(ProjectorRemoteResources.SETTINGS_FILE);
            Scanner scanner = new Scanner(fr);
            if (scanner.hasNextLine())
                projectorIP = scanner.nextLine().trim();

            if (scanner.hasNextLine())
                port = Integer.parseInt(scanner.nextLine().trim());

            fr.close();
            scanner.close();
        }

        public static int getPort() {
            return port;
        }

        public static void setPort(int port) {
            ProjectorSettings.port = port;
        }

        public static String getProjectorIP() {
            return projectorIP;
        }

        public static void setProjectorIP(String projectorIP) {
            ProjectorSettings.projectorIP = projectorIP;
        }

        public static void writeFinalSettings() throws IOException {
            FileWriter writer = new FileWriter(ProjectorRemoteResources.SETTINGS_FILE);
            writer.write(projectorIP + "\n");
            writer.write("" + port);
            writer.close();
        }

    }

    private static class ProjectorException extends RuntimeException {
        public ProjectorException() {
            super();
        }

        private ProjectorException(String message) {
            super(message);
        }
    }

    private class Scan extends Thread {
        private final Projector projector;

        public Scan(Projector projector) {
            this.projector = projector;
        }


        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                hold(3000);

                projector.scan();
            }
        }
    }

    private class AbleToSendThread extends Thread {

        private String waitingFor;

        public AbleToSendThread(String waitingFor) {
            this.waitingFor = waitingFor;
            ableToSend = false;
        }


        public void run() {
            ableToSend = false;
            ProjectorRemoteFrame.getFrame().setAbleToSendText("Not able to send commands due to command: " + waitingFor);

            int howLong;
            waitingFor = waitingFor.replaceAll("\r", "").trim();
            System.out.println("waitingFor = " + waitingFor);

            if (waitingFor.equals("PWR ON")) {
                howLong = 25 * 1000; // 25 second wait for power on
                System.out.println("Waiting on power on");
            } else if (waitingFor.equals("PWR OFF")) {
                howLong = 25 * 1000; // 10 second wait for power off
                System.out.println("Waiting on power off");
            } else if (waitingFor.contains("SOURCE")) {
                howLong = 5 * 1000; // wait 5 seconds for the source to change
                System.out.println("Waiting on source");
            } else if (waitingFor.contains("?")) {
                howLong = 0;
            } else {
                howLong = 3 * 1000; // 3 second wait for anything else
                System.out.println("Waiting on random command");
            }

            hold(howLong);

            System.out.println("Able to poll again");
            ableToSend = true;
            ProjectorRemoteFrame.getFrame().setAbleToSendText("Able to send commands");

            scan();
            ProjectorRemoteFrame.getFrame().getRemotePanel().updateGUI();

        }
    }

    private class Sender extends Thread {

        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                hold(50);

                if (!toSend.isEmpty() && !polling) {
                    ProjectorCommand next = toSend.poll();
                    boolean successful;
                    do {
                        successful = execCommand(next);
                        if (!successful) {
                            int n = errorDialog(new ProjectorException("There was an error with sending the command: " + next));
                            if (n != JOptionPane.YES_OPTION) {
                                successful = true;
                            }
                        }
                    } while (!successful);
                    scan();
                }
            }
        }
    }
}
