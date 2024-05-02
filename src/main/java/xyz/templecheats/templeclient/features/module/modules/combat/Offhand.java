package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.Arrays;
import java.util.Map;

public class Offhand extends Module {
    /*
     * Settings
     */
    private final EnumSetting < Item > item = new EnumSetting < > ("Item", this, Item.Crystal);
    private final IntSetting totemHp = new IntSetting("Totem HP", this, 0, 36, 16);
    private final BooleanSetting gappleInHole = new BooleanSetting("Gap In Hole", this, false);
    private final IntSetting gappleInHoleHP = new IntSetting("Gap Hole HP", this, 0, 36, 16);
    private final BooleanSetting delay = new BooleanSetting("Delay", this, false);

    /*
     * Variables
     */
    private boolean switching = false;
    private int last_slot;
    private Map < String, Item > itemMap;

    public Offhand() {
        super("Offhand", "Puts items in you're offhand", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(gappleInHole, delay, gappleInHoleHP, totemHp, item);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (mc.player == null)
            return;

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {

            if (switching) {
                swap_items(last_slot, 2);
                return;
            }

            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

            if (hp > totemHp.intValue()) {
                if (gappleInHole.booleanValue() && hp > gappleInHoleHP.intValue() && is_in_hole()) {
                    swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.booleanValue() ? 1 : 0);
                    return;
                }
                switch (item.value()) {
                    case Crystal:
                        swap_items(get_item_slot(Items.END_CRYSTAL), 0);
                        break;
                    case Gapple:
                        swap_items(get_item_slot(Items.GOLDEN_APPLE), delay.booleanValue() ? 1 : 0);
                        break;
                    case Totem:
                        swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.booleanValue() ? 1 : 0);
                        break;
                }
            } else {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.booleanValue() ? 1 : 0);
                return;
            }

            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), delay.booleanValue() ? 1 : 0);
            }

        }

    }

    public void swap_items(int slot, int step) {
        if (slot == -1)
            return;
        if (step == 0) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = true;
            last_slot = slot;
        }
        if (step == 2) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = false;
        }

        mc.playerController.updateController();
    }

    private boolean is_in_hole() {

        BlockPos player_block = GetLocalPlayerPosFloored();

        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR &&
                mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR &&
                mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR &&
                mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
    }

    private int get_item_slot(net.minecraft.item.Item input) {
        if (input == mc.player.getHeldItemOffhand().getItem())
            return -1;
        for (int i = 36; i >= 0; i--) {
            final net.minecraft.item.Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == input) {
                if (i < 9) {
                    if (input == Items.GOLDEN_APPLE) {
                        return -1;
                    }
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    public BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public enum Item {
        Crystal,
        Gapple,
        Totem
    }
}