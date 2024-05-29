package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.command.Command;

public class DeathCoordsCommand extends Command {
    private BlockPos lastDeathCoords;

    public DeathCoordsCommand() {
        super();
        this.lastDeathCoords = null;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getName() {
        return ".deathcoords";
    }

    @Override
    public void execute(String[] args) {
        Minecraft mc = Minecraft.getMinecraft();

        if (lastDeathCoords == null) {
            sendMessage("No death coordinates recorded yet.", true);
        } else {
            String message = "Last death coordinates: X=" + lastDeathCoords.getX() +
                    " Y=" + lastDeathCoords.getY() +
                    " Z=" + lastDeathCoords.getZ();
            sendMessage(message, false);
        }
    }

    protected void sendMessage(String message, boolean isError) {
        String prefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String textColor = TextFormatting.WHITE.toString();
        if (isError) {
            prefix += TextFormatting.RED;
        }
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(prefix + textColor + message));
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!event.getEntityLiving().world.isRemote) {
            if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving() == Minecraft.getMinecraft().player) {
                lastDeathCoords = event.getEntityLiving().getPosition();
                sendMessage("Death coordinates logged.", false);
            }
        }
    }
}
