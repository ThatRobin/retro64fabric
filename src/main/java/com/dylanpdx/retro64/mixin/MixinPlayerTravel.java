package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayerTravel {

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/world/entity/player/Player;travel(Lnet/minecraft/world/phys/Vec3;)V", cancellable = true)
    private void plrTravel(CallbackInfo ci){
        var thisPlr = ((Player)(Object)this);
        if (!thisPlr.isLocalPlayer())
            return;
        if (RemoteMCharHandler.getIsMChar(thisPlr)){
            try {
                thisPlr.tryCheckInsideBlocks();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ci.cancel();
        }

    }

}
