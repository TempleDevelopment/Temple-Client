package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerControllerMP.class)
public interface IPlayerControllerMP {
    @Accessor(value = "curBlockDamageMP")
    float getCurBlockDamageMP();

    @Accessor("curBlockDamageMP")
    void setCurBlockDamageMP(float currentBlockDamage);

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int blockHitDelay);

    @Accessor("currentPlayerItem")
    void setCurrentPlayerItem(int currentPlayerItem);

    @Accessor(value = "currentBlock")
    BlockPos getCurrentBlock();

    @Invoker("syncCurrentPlayItem")
    void invokeSyncCurrentPlayItem();
}
