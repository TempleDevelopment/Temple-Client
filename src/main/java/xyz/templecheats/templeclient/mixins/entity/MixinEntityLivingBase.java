package xyz.templecheats.templeclient.mixins.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.event.events.player.JumpEvent;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jumpHook(CallbackInfo ci) {
        JumpEvent event = new JumpEvent(this.motionX, this.motionZ);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}