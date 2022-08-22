package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.capabilities.smc64Capability;
import com.dylanpdx.retro64.networking.SM64PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        if (player != null){
            RemoteMCharHandler.mCharOff(player);
            //FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
            //ClientPlayNetworking.send(SM64PacketHandler.DISABLE_MARIO, friendlyByteBuf);
            smc64Capability.INSTANCE.sync(player);
        }
    }
}
