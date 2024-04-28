package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.BlockResetEvent;
import xyz.templecheats.templeclient.event.events.player.LeftClickBlockEvent;
import xyz.templecheats.templeclient.event.events.player.RotationUpdateEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.combat.Aura;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.math.MathUtil;
import xyz.templecheats.templeclient.util.render.enums.ProgressBoxModifiers;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.rotation.Rotation;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.world.BlockUtil;

public class FastBreak extends Module {
    /*
     * Settings
     */
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Packet);
    public final EnumSetting<InventoryManager.Switch> mineSwitch = new EnumSetting<>("Switch", this, InventoryManager.Switch.Packet);
    public final DoubleSetting damage = new DoubleSetting("Damage", this, 0.0, 1.0, 0.8);
    public DoubleSetting breakerDelay = new DoubleSetting("Breaker Delay", this, 0, 10, 2);

    public final EnumSetting<Rotation.Rotate> rotate = new EnumSetting<>("Rotation", this, Rotation.Rotate.None);
    public final BooleanSetting strict = new BooleanSetting("AlternateSwap", this, true);
    public final BooleanSetting strictReMine = new BooleanSetting("StrictBreak", this, true);

    public final BooleanSetting reset = new BooleanSetting("NoReset", this, false);

    public final DoubleSetting range = new DoubleSetting("Range", this, 0.0, 6.0, 5.0);

    public final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final EnumSetting<ProgressBoxModifiers> renderMode = new EnumSetting<>("Render Mode", this, ProgressBoxModifiers.Grow);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0, 255, 200);
    private final DoubleSetting defaultOpacityVal = new DoubleSetting("DefaultOpacity", this, 0, 255, 200);

    /*
     * Variables
     */
    private BlockPos minePosition;
    private BlockPos lastMinePos;
    private EnumFacing mineFacing;
    private float normalizedOpacity = opacity.floatValue() / opacity.floatValue();
    private static float mineDamage;
    private int mineBreaks;
    private int previousHaste;
    private int breakTick = 0;
    private boolean before = false;

    public FastBreak() {
        super("FastBreak", "Break blocks faster", Keyboard.KEY_NONE, Category.World);
        registerSettings(strict, strictReMine, reset, range, damage, render, defaultOpacityVal, opacity, renderMode, mineSwitch, rotate, mode);
    }

    @Override
    public void onUpdate() {
        if (mode.value() != Mode.Vanilla) {
            if (mc.player.isPotionActive(MobEffects.HASTE)) {
                mc.player.removePotionEffect(MobEffects.HASTE);
            }

            if (previousHaste > 0) {
                mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, previousHaste));
            }
        }

        if (!mc.player.capabilities.isCreativeMode) {
            if (minePosition != null) {
                double mineDistance = MathUtil.getDistanceToCenter(mc.player, minePosition);
                if (mineBreaks >= 2 && strictReMine.booleanValue() || mineDistance > range.doubleValue()) {
                    minePosition = null;
                    mineFacing = null;
                    mineDamage = 0;
                    mineBreaks = 0;
                }
            }

            if (mode.value().equals(Mode.Damage)) {
                if (((IPlayerControllerMP) mc.playerController).getCurBlockDamageMP() > damage.floatValue()) {
                    ((IPlayerControllerMP) mc.playerController).setCurBlockDamageMP(1);
                    mc.playerController.onPlayerDestroyBlock(minePosition);
                }
            }

            else if (mode.value().equals(Mode.Packet)) {
                if (minePosition != null && !mc.world.isAirBlock(minePosition)) {
                    if (mineDamage >= 1) {
                        if (!AutoCrystal.INSTANCE.isEnabled() && !Aura.INSTANCE.isEnabled()) {
                            int previousSlot = mc.player.inventory.currentItem;
                            int swapSlot = TempleClient.inventoryManager.searchSlot(getEfficientItem(mc.world.getBlockState(minePosition)).getItem(), InventoryManager.InventoryRegion.HOTBAR) + 36;

                            if (strict.booleanValue()) {
                                short nextTransactionID = mc.player.openContainer.getNextTransactionID(mc.player.inventory);

                                ItemStack itemstack = mc.player.openContainer.slotClick(swapSlot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                                mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, swapSlot, mc.player.inventory.currentItem, ClickType.SWAP, itemstack, nextTransactionID));
                            }

                            else {
                                TempleClient.inventoryManager.switchToItem(getEfficientItem(mc.world.getBlockState(minePosition)).getItem(), mineSwitch.value());
                            }

                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, minePosition, mineFacing));
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePosition, EnumFacing.UP));

                            if (strict.booleanValue()) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePosition, mineFacing));
                            }

                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, minePosition, mineFacing));

                            if (previousSlot != -1) {
                                if (strict.booleanValue()) {
                                    short nextTransactionID = mc.player.openContainer.getNextTransactionID(mc.player.inventory);
                                    ItemStack itemstack = mc.player.openContainer.slotClick(swapSlot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                                    mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, swapSlot, mc.player.inventory.currentItem, ClickType.SWAP, itemstack, nextTransactionID));

                                    mc.player.connection.sendPacket(new CPacketConfirmTransaction(mc.player.inventoryContainer.windowId, nextTransactionID, true));
                                }
                                else {
                                    TempleClient.inventoryManager.switchToSlot(previousSlot, InventoryManager.Switch.Packet);
                                }
                            }

                            mineDamage = 0;
                            mineBreaks++;
                        }
                    }
                    mineDamage += getBlockStrength(mc.world.getBlockState(minePosition), minePosition);
                }

                else {
                    mineDamage = 0;
                }
            }

            else if (mode.value().equals(Mode.Vanilla)) {
                ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
                mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE.setPotionName("SpeedMine"), 80950, 1, false, false));
            }

            else if (this.mode.value().equals(Mode.Breaker) && this.lastMinePos != null) {
                if (this.before) {
                    if (!(mc.world.getBlockState(lastMinePos).getBlock() instanceof BlockAir)) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastMinePos, mineFacing));
                    }
                } else if (mc.world.getBlockState(lastMinePos).getBlock() instanceof BlockAir) {
                    this.before = true;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            previousHaste = mc.player.getActivePotionEffect(MobEffects.HASTE).getDuration();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            mc.player.removePotionEffect(MobEffects.HASTE);
        }
        if (previousHaste > 0) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, previousHaste));
        }
        minePosition = null;
        mineFacing = null;
        mineDamage = 0;
        mineBreaks = 0;
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (mode.value().equals(Mode.Packet) && !mc.player.capabilities.isCreativeMode && render.booleanValue()) {
            if (minePosition != null) {
                if (!mc.world.isAirBlock(minePosition)) {
                    drawProgressBox();
                } else {
                    if (normalizedOpacity > 0.0F) {
                        normalizedOpacity = (normalizedOpacity - 0.047F * event.partialTicks);
                        GradientShader.setup(normalizedOpacity);
                        RenderUtil.boxShader(lastMinePos);
                        RenderUtil.outlineShader(lastMinePos);
                        GradientShader.finish();
                    }

                }
            }
        } else if (mode.value().equals(Mode.Breaker) && !mc.player.capabilities.isCreativeMode && render.booleanValue()) {
            GradientShader.setup(defaultOpacityVal.floatValue() / 255f);
            RenderUtil.boxShader(lastMinePos);
            RenderUtil.outlineShader(lastMinePos);
            GradientShader.finish();
        }
    }

    private void drawProgressBox() {
        normalizedOpacity = opacity.floatValue() / opacity.floatValue();

        AxisAlignedBB mineBox = mc.world.getBlockState(minePosition).getSelectedBoundingBox(mc.world , minePosition);
        Vec3d mineCenter = mineBox.getCenter();
        AxisAlignedBB shrunkMineBox = new AxisAlignedBB(mineCenter.x , mineCenter.y , mineCenter.z , mineCenter.x , mineCenter.y , mineCenter.z);

        RenderUtil.boxShader(shrunkMineBox.maxX , shrunkMineBox.maxY , shrunkMineBox.maxZ , shrunkMineBox.minX , shrunkMineBox.maxY , shrunkMineBox.minZ);
        float progress = (1 - mineDamage);
        switch (renderMode.value()) {
            case Grow: {
                ProgressBoxModifiers.Grow.renderBreaking(shrunkMineBox , progress , opacity.floatValue());
                break;
            }
            case Shrink: {
                ProgressBoxModifiers.Shrink.renderBreaking(shrunkMineBox , progress , opacity.floatValue());
                break;
            }
            case Cross: {
                ProgressBoxModifiers.Cross.renderBreaking(shrunkMineBox , progress , opacity.floatValue());
                break;
            }
            case Fade: {
                ProgressBoxModifiers.Fade.renderBreaking(shrunkMineBox.offset(0.5 , 0.5 , 0.5).contract(1 , 1 , 1) , progress , opacity.floatValue());
                break;
            }
            case UnFill: {
                ProgressBoxModifiers.UnFill.renderBreaking(shrunkMineBox.offset(0.5 , 0.5 , 0.5).contract(1 , 0 , 1) , progress , opacity.floatValue());
                break;
            }
            case Fill: {
                ProgressBoxModifiers.Fill.renderBreaking(shrunkMineBox.offset(0.5 , 0.5 , 0.5).contract(1 , 0 , 1) , progress , opacity.floatValue());
                break;
            }
            case Static: {
                ProgressBoxModifiers.Static.renderBreaking(shrunkMineBox.offset(0.5 , 0.5 , 0.5).contract(1 , 1 , 1) , progress , opacity.floatValue());
                break;
            }
        }
        lastMinePos = minePosition;
    }

    public boolean isActive() {
        return isEnabled() && minePosition != null && !mc.world.isAirBlock(minePosition) && mineDamage > 0;
    }

    @SubscribeEvent
    public void onLeftClickBlock(LeftClickBlockEvent event) {
        if (BlockUtil.isBreakable(event.getPos()) && !mc.player.capabilities.isCreativeMode) {
            if (mode.value().equals(Mode.Creative)) {
                mc.playerController.onPlayerDestroyBlock(event.getPos());
                mc.world.setBlockToAir(event.getPos());
            }

            if (mode.value().equals(Mode.Packet)) {
                if (!event.getPos().equals(minePosition)) {
                    minePosition = event.getPos();
                    mineFacing = event.getFace();
                    mineDamage = 0;
                    mineBreaks = 0;

                    if (minePosition != null && mineFacing != null) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePosition, mineFacing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePosition, EnumFacing.UP));
                    }
                }
            }
            else if (mode.value().equals(Mode.Breaker)) {
                if (this.lastMinePos == null || event.getPos().getX() != this.lastMinePos.getX() || event.getPos().getY() != this.lastMinePos.getY() || event.getPos().getZ() != this.lastMinePos.getZ()) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    this.before = true;
                    this.lastMinePos = event.getPos();
                    this.mineFacing = event.getFace();
                }
                if (this.breakerDelay.doubleValue() <= this.breakTick++) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastMinePos, mineFacing));
                    event.isCancelable();
                    this.breakTick = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRotationUpdate(RotationUpdateEvent event) {
        if (isActive() && mc.player != null) {
            if (!rotate.value().equals(Rotation.Rotate.None)) {
                if (mineDamage > 0.95) {
                    event.setCanceled(true);
                    if (minePosition != null) {
                        Rotation mineRotation = RotationUtil.rotationCalculate(minePosition.add(0.5, 0.5, 0.5));
                        if (rotate.value().equals(Rotation.Rotate.Client)) {
                            mc.player.rotationYaw = mineRotation.getYaw();
                            mc.player.rotationYawHead = mineRotation.getYaw();
                            mc.player.rotationPitch = mineRotation.getPitch();
                        }
                        TempleClient.rotationManager.setRotation(mineRotation);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockReset(BlockResetEvent event) {
        if (reset.booleanValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            if (strict.booleanValue()) {
                mineDamage = 0;
            }
        }
    }

    /**
     * Searches the most efficient item for a specified position
     * @param state The {@link IBlockState} position to find the most efficient item for
     * @return The most efficient item for the specified position
     */
    public ItemStack getEfficientItem(IBlockState state) {
        int bestSlot = -1;
        double bestBreakSpeed = 0;
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty()) {
                float breakSpeed = mc.player.inventory.getStackInSlot(i).getDestroySpeed(state);

                if (breakSpeed > 1) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)) > 0) {
                        breakSpeed += (float) (StrictMath.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)), 2) + 1);
                    }
                    if (breakSpeed > bestBreakSpeed) {
                        bestBreakSpeed = breakSpeed;
                        bestSlot = i;
                    }
                }
            }
        }
        if (bestSlot != -1) {
            return mc.player.inventory.getStackInSlot(bestSlot);
        }

        return mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem);
    }

    /**
     * Finds the block strength of a specified block
     * @param state The {@link IBlockState} block state of the specified block
     * @param position The {@link BlockPos} position of the specified block
     * @return The block strength of the specified block
     */
    public float getBlockStrength(IBlockState state, BlockPos position) {
        float hardness = state.getBlockHardness(mc.world, position);
        if (hardness < 0) {
            return 0;
        }
        if (!canHarvestBlock(state.getBlock(), position)) {
            return getDigSpeed(state) / hardness / 100F;
        }
        else {
            return getDigSpeed(state) / hardness / 30F;
        }
    }

    /**
     * Check whether or not a specified block can be harvested
     * @param block The {@link Block} block to check
     * @param position The {@link BlockPos} position of the block to check
     * @return Whether or not the block can be harvested
     */
    @SuppressWarnings("deprecation")
    public boolean canHarvestBlock(Block block, BlockPos position) {
        IBlockState worldState = mc.world.getBlockState(position);
        IBlockState state = worldState.getBlock().getActualState(worldState, mc.world, position);
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack stack = getEfficientItem(state);
        String tool = block.getHarvestTool(state);
        if (stack.isEmpty() || tool == null) {
            return mc.player.canHarvestBlock(state);
        }
        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);
        if (toolLevel < 0) {
            return mc.player.canHarvestBlock(state);
        }
        return toolLevel >= block.getHarvestLevel(state);
    }

    /**
     * Finds the dig speed of a specified block
     * @param state {@link IBlockState} The block state of the specified block
     * @return The dig speed of the specified block
     */
    @SuppressWarnings("all")
    public float getDigSpeed(IBlockState state) {
        float digSpeed = getDestroySpeed(state);
        if (digSpeed > 1) {
            ItemStack itemstack = getEfficientItem(state);
            int efficiencyModifier = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemstack);
            if (efficiencyModifier > 0 && !itemstack.isEmpty()) {
                digSpeed += StrictMath.pow(efficiencyModifier, 2) + 1;
            }
        }
        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1 + (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float fatigueScale;
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    fatigueScale = 0.3F;
                    break;
                case 1:
                    fatigueScale = 0.09F;
                    break;
                case 2:
                    fatigueScale = 0.0027F;
                    break;
                case 3:
                default:
                    fatigueScale = 8.1E-4F;
            }

            digSpeed *= fatigueScale;
        }
        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            digSpeed /= 5;
        }
        if (!mc.player.onGround) {
            digSpeed /= 5;
        }

        return (digSpeed < 0 ? 0 : digSpeed);
    }

    /**
     * Finds the destroy speed of a specified position
     * @param state {@link IBlockState} The position to get the destroy speed for
     * @return The destroy speed of the specified position
     */
    public float getDestroySpeed(IBlockState state) {
        float destroySpeed = 1;

        if (getEfficientItem(state) != null && !getEfficientItem(state).isEmpty()) {
            destroySpeed *= getEfficientItem(state).getDestroySpeed(state);
        }

        return destroySpeed;
    }

    public enum Mode {
        Packet,
        Damage,
        Breaker,
        Vanilla,
        Creative
    }
}
