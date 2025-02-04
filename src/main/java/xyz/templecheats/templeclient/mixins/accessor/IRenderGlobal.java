package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderGlobal.class)
public interface IRenderGlobal {
    @Accessor(value = "damagedBlocks")
    Map<Integer, DestroyBlockProgress> getDamagedBlocks();
}
