package dev.shroysha.control.projector.controller;


public abstract class ProjectorUtilities {

    private ProjectorUtilities() {
    }

    /*
     * 01: sRGB
     * 04: Presentation
     * 05: Theatre
     * 06: Game
     * 08: Sports
     * 11: Black Board
     * 14: Photo
     */

    public static String colorModeToInt(String colorMode) {
        if (colorMode != null) {
            switch (colorMode) {
                case "sRGB":
                    return "01";
                case "Presentation":
                    return "04";
                case "Theatre":
                    return "05";
                case "Game":
                    return "06";
                case "Black Board":
                    return "11";
                case "Photo":
                    return "14";
            }
        }
        return "UNK";
    }

    public static String intToColorMode(String integer) {
        switch (integer) {
            case "01":
                return "sRGB";
            case "04":
                return "Presentation";
            case "05":
                return "Theatre";
            case "06":
                return "Game";
            case "11":
                return "Black Board";
            case "14":
                return "Photo";
            default:
                return "UNK";
        }
    }

    /*
     * COMP 1 = 14
     * COMP 2 = 21
     * VIDEO = 41
     * SVIDEO = 42
     */

    public static String intToSource(String integer) {
        switch (integer) {
            case "14":
                return "COMP1";
            case ("21"):
                return "COMP2";
            case "41":
                return "Video";
            case "42":
                return "sVideo";
            default:
                return "UNK";
        }
    }

    public static String sourceToInt(String source) {
        switch (source) {
            case "COMP1":
                return "14";
            case "COMP2":
                return "21";
            case "Video":
                return "41";
            case "sVideo":
                return "42";
            default:
                return "UNK";
        }
    }
}
