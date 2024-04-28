package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface IRender {

    @Accessor("renderOutlines")
    boolean getRenderOutlines();

    @Invoker int callGetTeamColor(Entity entityIn);
}
