package xyz.templecheats.templeclient.features.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.AttackEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Panic;
import xyz.templecheats.templeclient.util.autocrystal.DamageUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FakePlayer extends Module {
    /*
     * Setting
     */
    private final StringSetting name = new StringSetting("Name", this, "FakePlayer");
    private final BooleanSetting copyInventory = new BooleanSetting("Copy Inven", this, false);
    private final BooleanSetting damageSimulate = new BooleanSetting("Damage", this, true);
    private final BooleanSetting resistance = new BooleanSetting("Resistance", this, true);
    public final IntSetting vulnerabilityTick = new IntSetting("Vulnerability Tick", this, 0, 10, 4);
    public final IntSetting resetHealth = new IntSetting("Reset Health", this, 0, 36, 10);
    public final IntSetting tickRegen = new IntSetting("Tick Regen", this, 0, 30, 4);
    private final BooleanSetting moveButton = new BooleanSetting("Move", this, false);

    /*
     * Variables
     */
    private static final Random random = new Random();
    List<playerInfo> listPlayers = new ArrayList<>();
    private AttackableFakePlayer fakePlayer;
    private BlockPos respawnLocation;

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player", Keyboard.KEY_NONE, Category.Misc);

        this.registerSettings(name, moveButton, copyInventory, damageSimulate, resistance, vulnerabilityTick, resetHealth, tickRegen);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        GameProfile profile = new GameProfile(mc.player.getUniqueID(), name.getStringValue());

        fakePlayer = new AttackableFakePlayer(mc.world, profile);
        fakePlayer.setEntityId(-1882);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.setGameType(GameType.SURVIVAL);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;

        if (resistance.booleanValue()) {
            fakePlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(11) , 6942032 , 0));
        }
        if (copyInventory.booleanValue()) {
            fakePlayer.inventory.copyInventory(mc.player.inventory);
        } else {
            this.addArmor();
        }
        fakePlayer.onEntityUpdate();
        respawnLocation = mc.player.getPosition();
        mc.world.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);

        listPlayers.add(new playerInfo(fakePlayer.getName()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        this.disable();
    }

    @Override
    public void onDisable() {
        if (fakePlayer == null) {
            return;
        }
        if (!Panic.isPanic) {
            mc.world.removeEntityFromWorld(fakePlayer.getEntityId());
        }
        listPlayers.clear();
    }

    @Override
    public void onUpdate() {
        if (fakePlayer != null && moveButton.booleanValue()) {
            fakePlayer.moveRandomly();
        }
        for (int i = 0; i < listPlayers.size(); i++) {
            if (listPlayers.get(i).update()) {
                int finalI = i;
                Optional<EntityPlayer> fakePlayer = mc.world.playerEntities.stream().filter(e -> e.getName().equals(listPlayers.get(finalI).name)).findAny();
                if (fakePlayer.isPresent()) {
                    if (fakePlayer.get().getHealth() <= 3) {
                        mc.world.playSound(mc.player, fakePlayer.get().posX, fakePlayer.get().posY, fakePlayer.get().posZ, SoundEvents.ITEM_TOTEM_USE, mc.player.getSoundCategory(), 1.0f, 1.0f);
                    }
                    if (fakePlayer.get().getHealth() < 20) {
                        fakePlayer.get().setHealth(fakePlayer.get().getHealth() + 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEvent.Post event) {
        if (damageSimulate.booleanValue()) {
            boolean critical = ((mc.player.lastTickPosY < mc.player.prevChasingPosY && !mc.player.onGround)) &&
                    !mc.player.isInWater() &&
                    !mc.player.isInLava() &&
                    !mc.player.isRiding() &&
                    !mc.player.isOnLadder() &&
                    !mc.player.isSprinting();

            if (critical) {
                mc.player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT , 1f , 1f);
            } else if (mc.player.getCooledAttackStrength(0.5f) > 0.9) {
                mc.player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG , 1f , 1f);
            } else {
                mc.player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK , 1f , 1f);
            }
            for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.getName().equals(name.value())) {
                    float damage = 3 * (float) (critical ? 1.5 : 1.0);
                    entityPlayer.setHealth(damage);
                }
            }
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (damageSimulate.booleanValue()) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) event.getPacket();
                if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                        if (entity instanceof EntityEnderCrystal) {
                            if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                                for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                                    if (entityPlayer.getName().equals(name.value())) {

                                        Optional<playerInfo> data = listPlayers.stream().filter(e -> e.name.equals(entityPlayer.getName())).findAny();

                                        if (!data.isPresent() || !data.get().canPop()) {
                                            continue;
                                        }

                                        float damage = DamageUtil.calculateDamageThreaded(packetSoundEffect.getX() , packetSoundEffect.getY() , packetSoundEffect.getZ() , entityPlayer);

                                        if (damage > entityPlayer.getHealth() || fakePlayer.getHealth() <= 3) {
                                            entityPlayer.setHealth(resetHealth.intValue());
                                            mc.effectRenderer.emitParticleAtEntity(entityPlayer , EnumParticleTypes.TOTEM , 30);
                                            mc.world.playSound(entityPlayer.posX , entityPlayer.posY , entityPlayer.posZ , SoundEvents.ITEM_TOTEM_USE , entity.getSoundCategory() , 1.0F , 1.0F , false);
                                        } else entityPlayer.setHealth(entityPlayer.getHealth() - damage);

                                        data.get().pop = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addArmor() {
        // Helmet
        ItemStack helmet = createEnchantedItem(Items.DIAMOND_HELMET,
                new Enchantment[]{Enchantments.PROTECTION, Enchantments.UNBREAKING, Enchantments.RESPIRATION, Enchantments.AQUA_AFFINITY, Enchantments.MENDING});
        fakePlayer.inventory.armorInventory.set(3, helmet);

        // Chestplate
        ItemStack chestplate = createEnchantedItem(Items.DIAMOND_CHESTPLATE,
                new Enchantment[]{Enchantments.PROTECTION, Enchantments.UNBREAKING, Enchantments.MENDING});
        fakePlayer.inventory.armorInventory.set(2, chestplate);

        // Leggings
        ItemStack leggings = createEnchantedItem(Items.DIAMOND_LEGGINGS,
                new Enchantment[]{Enchantments.BLAST_PROTECTION, Enchantments.UNBREAKING, Enchantments.MENDING});
        fakePlayer.inventory.armorInventory.set(1, leggings);

        // Boots
        ItemStack boots = createEnchantedItem(Items.DIAMOND_BOOTS,
                new Enchantment[]{Enchantments.PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.DEPTH_STRIDER, Enchantments.UNBREAKING, Enchantments.MENDING});
        fakePlayer.inventory.armorInventory.set(0, boots);
    }

    private ItemStack createEnchantedItem(Item item, Enchantment[] enchantments) {
        ItemStack stack = new ItemStack(item);
        for (Enchantment enchantment : enchantments) {
            stack.addEnchantment(enchantment, enchantment.getMaxLevel());
        }
        return stack;
    }

    class AttackableFakePlayer extends EntityOtherPlayerMP {
        public AttackableFakePlayer(World worldIn, GameProfile gameProfileIn) {
            super(worldIn, gameProfileIn);
        }

        public void moveRandomly() {
            double x = respawnLocation.getX() + (random.nextDouble() - 0.5) * 1;
            double z = respawnLocation.getZ() + (random.nextDouble() - 0.5) * 1;
            this.setPosition(x, this.posY, z);
        }
    }

    public class playerInfo {
        final String name;
        int pop = -1;
        int regen = 0;

        public playerInfo(String name) {
            this.name = name;
        }

        boolean update() {
            if (pop != -1) {
                if (++pop >= vulnerabilityTick.intValue())
                    pop = -1;
            }
            if (++regen >= tickRegen.intValue()) {
                regen = 0;
                return true;
            } else return false;
        }

        boolean canPop() {
            return this.pop == -1;
        }
    }
}