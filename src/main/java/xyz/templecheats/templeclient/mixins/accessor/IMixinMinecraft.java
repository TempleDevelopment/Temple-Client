package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface IMixinMinecraft {
    @Accessor("rightClickDelayTimer")
    void setRightClickDelayTimer(int rightClickDelayTimer);
}
