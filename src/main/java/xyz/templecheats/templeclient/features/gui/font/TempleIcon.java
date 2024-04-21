package xyz.templecheats.templeclient.features.gui.font;

public enum TempleIcon {
    POTION("0"),
    SCREEN("1"),
    WORLD("2"),
    LOCATE("3"),
    CURSE("4"), // Ehe
    WIFI1("5"),
    WIFI2("6"),
    WARNING("7"),
    TRI_WARNING("8"),
    ERROR("9"),
    INFO(":"),
    MAGIC(";"),
    SUCCESS("<"),
    LIST("="),
    USER("B"),
    MISC("C"),
    MOVEMENT("D"),
    COMBAT("E"),
    VISUAL("F"),
    COLOR("G"),
    GEAR("H"),
    KEYBOARD("M"),
    BOX("L"),
    SHIELD("I"),
    DISCONNECT("J"),
    TEMPLE("T"),
    SEARCH("s"),
    FOLDER("t"),
    CONFIG("u"),
    PEOPLE("v"),
    TRASH("w"),
    DOWNLOAD("x");

    private final String icon;

    TempleIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
