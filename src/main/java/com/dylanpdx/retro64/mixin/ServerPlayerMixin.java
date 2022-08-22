package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.Retro64;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.networking.SM64PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "giveExperiencePoints", at = @At("HEAD"))
    public void onXpChanged(int i, CallbackInfo ci) {
        var player = (ServerPlayer)(Object)this;
        Retro64.LOGGER.info("XP Collected");
        if (SM64EnvManager.selfMChar!=null) {
            SM64EnvManager.selfMChar.heal((byte) 1);
        }
        if (RemoteMCharHandler.getIsMChar(player)){
            for (ServerPlayer splayer : PlayerLookup.tracking(player)) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
                friendlyByteBuf.writeByte(1);
                ServerPlayNetworking.send(splayer, SM64PacketHandler.HEAL_PACKET, friendlyByteBuf);
            }
        }
    }
}
