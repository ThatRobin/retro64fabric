package com.dylanpdx.retro64.networking;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.Utils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;

public class Retro64PacketsS2C {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(SM64PacketHandler.DAMAGE_PACKET, Retro64PacketsS2C::damagePacket);
        ClientPlayNetworking.registerGlobalReceiver(SM64PacketHandler.DISABLE_MARIO, Retro64PacketsS2C::disableMario);
    }

    private static void disableMario(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        minecraft.execute(() -> {
            RemoteMCharHandler.mCharOff(minecraft.player);
        });
    }

    private static void damagePacket(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        var amount = friendlyByteBuf.readInt();
        var pos = new Vec3(friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble());
        minecraft.execute(() -> {
            if (SM64EnvManager.selfMChar!=null)
                SM64EnvManager.selfMChar.damage(amount, pos);
        });
    }

}
