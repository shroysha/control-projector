package dev.shroysha.control.projector.util;

import java.io.File;

public class ProjectorRemoteResources {

    public static final String PROJECTOR_DIRECTORY = System.getProperty("user.home") + "/ProjectorProject/";
    public static final File
            PROJECTOR_DIR = new File(PROJECTOR_DIRECTORY),
            PRESETS_FILE = new File(PROJECTOR_DIR.getPath() + "/PRESETS"),
            LOCK_FILE = new File(PROJECTOR_DIR.getPath() + "/LOCK"),
            SETTINGS_FILE = new File(PROJECTOR_DIR.getPath() + "/PROPERTIES");

}
