package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.friend.Friend;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.List;

public class Friends extends HUD.HudElement {
    /*
     * Settings
     */
    private final BooleanSetting leftOfCenter = new BooleanSetting("LeftOfCenter", this, true);

    public Friends() {
        super("Friends", "Shows your friends in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        List < Friend > friends = TempleClient.friendManager.getFriends();

        friends.sort((Friend friend1, Friend friend2) -> {
            String name1 = friend1.getName();
            String name2 = friend2.getName();

            return Double.compare(font.getStringWidth(this.isTopOfCenter() ? name2 : name1), font.getStringWidth(this.isTopOfCenter() ? name1 : name2));
        });

        double y = 0;
        double maxWidth = 0;
        double totalHeight = 0;

        for (Friend friend: friends) {
            String name = friend.getName();

            double width = font.getStringWidth(name) + 10;
            double height = font.getFontHeight() + 2;
            maxWidth = Math.max(maxWidth, width);
            totalHeight += height;

            font.drawString(name, this.getX() + (!this.isLeftOfCenter() ? this.getWidth() - font.getStringWidth(name) : 0), this.getY() + y, ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);

            y += height;
        }

        this.setWidth(!friends.isEmpty() ? font.getStringWidth(this.isTopOfCenter() ? friends.get(0).getName() : friends.get(friends.size() - 1).getName()) : 0);
        this.setHeight(totalHeight);
    }
}