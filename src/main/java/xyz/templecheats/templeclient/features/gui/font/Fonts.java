package xyz.templecheats.templeclient.features.gui.font;

import xyz.templecheats.templeclient.features.module.modules.client.FontSettings;

public class Fonts {

    private static CFont getFont(int size) {
        return FontSettings.getFont(size);
    }

    private static CFont getIcon(int size) {
        return FontSettings.getIcon(size);
    }

    //Font
    public static CFont font12 = getFont(12);
    public static CFont font14 = getFont(14);
    public static CFont font16 = getFont(16);
    public static CFont font18 = getFont(18);
    public static CFont font20 = getFont(20);
    public static CFont font22 = getFont(22);
    public static CFont font24 = getFont(24);

    //Icon
    public static CFont icon12 = getIcon(12);
    public static CFont icon14 = getIcon(14);
    public static CFont icon16 = getIcon(16);
    public static CFont icon18 = getIcon(18);
    public static CFont icon20 = getIcon(20);
    public static CFont icon22 = getIcon(22);
    public static CFont icon24 = getIcon(24);
    public static CFont icon26 = getIcon(26);
    public static CFont icon28 = getIcon(28);
    public static CFont icon30 = getIcon(30);
    public static CFont icon32 = getIcon(32);
    public static CFont icon34 = getIcon(34);
    public static CFont icon40 = getIcon(40);
    public static CFont icon46 = getIcon(46);
}
