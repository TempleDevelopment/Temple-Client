package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import xyz.templecheats.templeclient.features.gui.clickgui.hud.HudEditorScreen;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.features.module.modules.combat.Aura;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.animation.Animation;
import xyz.templecheats.templeclient.util.render.animation.Easing;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;

import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.minecraft.util.math.MathHelper.clamp;
import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.font12;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;
import static xyz.templecheats.templeclient.util.math.MathUtil.round;
import static xyz.templecheats.templeclient.util.render.RenderUtil.drawHead;

public class TargetHUD extends HUD.HudElement {
    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final Animation animation = new Animation(Easing.InOutCircle, 300);
    private TargetInfo info = TargetInfo.BLANK;
    private double healthProgress = 0.0;

    public TargetHUD() {
        super("TargetHUD", "Draws a HUD featuring target information");
        registerSettings(outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        this.update();

        Vec2d pos1 = new Vec2d(-getWidth() * 0.5, -getHeight() * 0.5);
        Vec2d pos2 = new Vec2d(getWidth() * 0.5, getHeight() * 0.5);

        glPushMatrix();
        this.animation.progress(1);

        glTranslated(getX() + getWidth() * animation.getProgress() / 2f, getY() + getHeight() * 0.5, 0.0);
        glScaled(animation.getProgress(), animation.getProgress(), animation.getProgress());
        if (info != TargetInfo.BLANK) {
            drawTarget(pos1, pos2);
        }
        glPopMatrix();

        this.setWidth(120 + (font18.getStringWidth(info.name) / 2));
        this.setHeight(38);
    }

    public void drawTarget(Vec2d pos1, Vec2d pos2) {
        // Colors
        Color highlightColor1 = setAlpha(ClickGUI.INSTANCE.getClientColor(0), 0.06);
        Color highlightColor2 = setAlpha(highlightColor1, 0.0);
        Color headBgColor = new Color(26, 26, 26);
        Color bgColor = color.getColor();
        Color bgOutline = outlineColor.getColor();
        Color healthBarBgColor = new Color(12, 12, 12);
        Color healthBarColor1 = setAlpha(ClickGUI.INSTANCE.getClientColor(0), 0.75);
        Color healthBarColor2 = setAlpha(ClickGUI.INSTANCE.getClientColor(3), 0.75);
        Color fontColor = new Color(-1);

        double h = pos2.y - pos1.y;

        // Background
        new RectBuilder(pos1, pos2).outlineColor(bgOutline).width(outline.booleanValue() ? outlineWidth.doubleValue() : 0).color(bgColor).radius(5.0).blur(blur.booleanValue() ? blurRadius.doubleValue() : 0).drawBlur().draw();
        // Highlight
        new RectBuilder(pos1.plus(1.0), pos2.minus(1.0)).colorV(highlightColor1, highlightColor2).radius(3.9).draw();
        // Head background
        new RectBuilder(pos1, pos1.plus(h)).color(headBgColor).radius(5.0).draw();
        new RectBuilder(pos1.plus(h * 0.5, 0.0), pos1.plus(h)).color(headBgColor).draw();
        // Shadow
        new RectBuilder(pos1.plus(h, 0.0), pos1.plus(h + 5.0, h)).colorH(new Color(0, 0, 0, 90), new Color(0, 0, 0, 0)).draw();

        Vec2d headPos1 = pos1.plus(3.0);
        Vec2d headPos2 = pos1.plus(h).minus(3.0);
        drawHead(info.entity, headPos1, headPos2, 6f);

        // Healthbar background
        double healthBarCenter = lerp(pos1.y, pos2.y, 0.75);
        Vec2d healthBgPos1 = new Vec2d(pos1.x + h + 4.0, healthBarCenter - 2.0);
        Vec2d healthBgPos2 = new Vec2d(pos2.x - 4.0, healthBarCenter + 2.0);
        new RectBuilder(healthBgPos1, healthBgPos2).color(healthBarBgColor).radius(100.0).blur(8).drawBlur().draw();

        // Healthbar
        double sliderX = lerp(healthBgPos1.x, healthBgPos2.x, healthProgress);
        double previousSliderX = sliderX;
        if (previousSliderX <= healthBgPos2.x) {
            sliderX = lerp(healthBgPos1.x, healthBgPos2.x, healthProgress);
        }

        Vec2d healthSliderPos = new Vec2d(sliderX, healthBgPos2.y);
        Vec2d textPos = new Vec2d(healthBgPos1.x, healthBgPos1.y);
        new RectBuilder(healthBgPos1, healthSliderPos).colorH(healthBarColor1, healthBarColor2).radius(100.0).draw();

        // Health and Name
        font12.drawString("HP: " + info.displayHealth(), (float) textPos.x, (float) (textPos.y - font12.getFontHeight() - 3), fontColor, false);
        font18.drawString(info.name, (float) textPos.x, (float) textPos.y - font18.getFontHeight() - 10, fontColor, false);

        // Armor bar
        double barSize = 7.8;
        double barPadding = 2.0;
        Vec2d armorBarStartPos = new Vec2d(pos2.x - 5 - barSize * 2 - barPadding * 3, pos1.y + 5.0);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Vec2d armorBarPos1 = armorBarStartPos.plus((barSize + barPadding) * i, (barSize + barPadding) * j);
                Vec2d armorBarPos2 = armorBarPos1.plus(barSize);
                new RectBuilder(armorBarPos1, armorBarPos2).color(headBgColor).radius(1.0).draw();
                int index = (1 - i) * 2 + (1 - j);
                if (info.entity instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer) info.entity;
                    if (index < player.inventory.armorInventory.size()) {
                        ItemStack stack = player.inventory.armorInventory.get(index);
                        if (!stack.isEmpty()) {
                            GlStateManager.pushMatrix();
                            RenderHelper.enableGUIStandardItemLighting();

                            glTranslatef((float) armorBarPos1.x, (float) armorBarPos1.y, 0.0f);
                            glScaled(0.5, 0.5, 0.5);
                            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
                            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);

                            RenderHelper.disableStandardItemLighting();
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
        }
    }

    public void update() {
        EntityLivingBase entity = (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) ? (EntityLivingBase) mc.objectMouseOver.entityHit : null;
        EntityLivingBase ka = Aura.INSTANCE.isEnabled() ? Aura.INSTANCE.renderTarget : null;
        EntityLivingBase ca = AutoCrystal.INSTANCE.isEnabled() ? AutoCrystal.INSTANCE.getTarget() : null;

        // Java but write like kotlin syntax <3 - Kuro
        // I hate this - ZANE
        info = Stream.of(
                        () -> (ca != null) ? new TargetInfo(ca.getName(), ca.getHealth() + ca.getAbsorptionAmount(), ca.getMaxHealth(), ca) : null,
                        () -> (ka != null) ? new TargetInfo(ka.getName(), ka.getHealth() + ka.getAbsorptionAmount(), ka.getMaxHealth(), ka) : null,
                        () -> (entity != null) ? new TargetInfo(entity.getName(), entity.getHealth() + entity.getAbsorptionAmount(), entity.getMaxHealth(), entity) : null,
                        (Supplier<TargetInfo>) () -> (mc.currentScreen instanceof HudEditorScreen) ? TargetInfo.SELF : null
                )
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(TargetInfo.BLANK);

        if (info == TargetInfo.BLANK) {
            animation.reset();
        }
        healthProgress = info.healthProgress() * animation.getProgress();
    }


    private static class TargetInfo {
        String name;
        double health;
        double maxHealth;
        EntityLivingBase entity;

        public TargetInfo(String name, double health, double maxHealth, EntityLivingBase entity) {
            this.name = name;
            this.health = health;
            this.maxHealth = maxHealth;
            this.entity = entity;
        }

        double healthProgress() {
            return Math.min(health, clamp(health / Math.max(0.1, maxHealth), 0.0, 1.0));
        }

        String displayHealth() {
            return String.valueOf(round(health, 1));
        }

        static final TargetInfo BLANK = new TargetInfo("", 0.0, 1.0, null);
        static final TargetInfo SELF = new TargetInfo("Temple Client", 20.0, 20.0, null);
    }
}
