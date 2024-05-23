package xyz.templecheats.templeclient.features.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.player.PlayerUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class AutoTotem extends Module {

    private final DoubleSetting health = new DoubleSetting("Health", this, 0.5, 20.0, 16.0);
    private final EnumSetting < AutoTotemMode > Mode = new EnumSetting < > ("Item", this, AutoTotemMode.Totem);
    private final EnumSetting < AutoTotemMode > FallbackMode = new EnumSetting < > ("Fallback", this, AutoTotemMode.Crystal);
    private final DoubleSetting FallDistance = new DoubleSetting("Fall Distance", this, 0.0f, 100.0f, 15.0f);
    private final BooleanSetting TotemOnElytra = new BooleanSetting("Totem On Elytra", this, true);
    private final BooleanSetting OffhandGapOnSword = new BooleanSetting("Sword + Gapple", this, false);
    private final BooleanSetting OffhandStrNoStrSword = new BooleanSetting("Strength + Sword", this, false);
    private final BooleanSetting HotbarFirst = new BooleanSetting("Hotbar First", this, false);

    public AutoTotem() {
        super("AutoTotem", "Automatically places a totem in your offhand", Keyboard.KEY_NONE, Module.Category.Combat);
        registerSettings(TotemOnElytra, OffhandGapOnSword, OffhandStrNoStrSword, HotbarFirst, health, FallDistance, Mode, FallbackMode);
    }

    @SubscribeEvent
    public final void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory)))
            return;

        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            if (health.doubleValue() <= PlayerUtil.GetHealthWithAbsorption() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && OffhandStrNoStrSword.booleanValue() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                SwitchOffHandIfNeed(AutoTotemMode.Strength);
                return;
            }

            if (health.doubleValue() <= PlayerUtil.GetHealthWithAbsorption() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && OffhandGapOnSword.booleanValue()) {
                SwitchOffHandIfNeed(AutoTotemMode.Gap);
                return;
            }
        }

        if (health.doubleValue() > PlayerUtil.GetHealthWithAbsorption() || Mode.value() == AutoTotemMode.Totem || (TotemOnElytra.booleanValue() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= FallDistance.doubleValue() && !mc.player.isElytraFlying()) || noNearbyPlayers()) {
            SwitchOffHandIfNeed(AutoTotemMode.Totem);
            return;
        }
        SwitchOffHandIfNeed(Mode.value());
    }

    private void SwitchOffHandIfNeed(AutoTotemMode val) {
        Item item = GetItemFromModeVal(val);

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = HotbarFirst.booleanValue() ? PlayerUtil.GetRecursiveItemSlot(item) : PlayerUtil.GetItemSlot(item);

            Item fallback = GetItemFromModeVal(FallbackMode.value());

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = PlayerUtil.GetRecursiveItemSlot(fallback);

                if (slot == -1 && fallback != Items.TOTEM_OF_UNDYING) {
                    fallback = Items.TOTEM_OF_UNDYING;

                    if (item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                        slot = PlayerUtil.GetRecursiveItemSlot(fallback);
                    }
                }
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP,
                        mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    public Item GetItemFromModeVal(AutoTotemMode val) {
        switch (val) {
            case Crystal:
                return Items.END_CRYSTAL;
            case Gap:
                return Items.GOLDEN_APPLE;
            case Pearl:
                return Items.ENDER_PEARL;
            case Chorus:
                return Items.CHORUS_FRUIT;
            case Strength:
                return Items.POTIONITEM;
            case Shield:
                return Items.SHIELD;
            default:
                break;
        }

        return Items.TOTEM_OF_UNDYING;
    }

    private boolean noNearbyPlayers() {
        return AutoTotemMode.Crystal == Mode.value() && mc.world.playerEntities.stream().noneMatch(e -> e != mc.player && isValidTarget(e));
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) {
            return false;
        }
        return !(mc.player.getDistance(entity) > 15);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + Mode.value().name() + ChatFormatting.RESET;
    }

    public enum AutoTotemMode {
        Totem,
        Gap,
        Crystal,
        Pearl,
        Chorus,
        Strength,
        Shield,
    }
}