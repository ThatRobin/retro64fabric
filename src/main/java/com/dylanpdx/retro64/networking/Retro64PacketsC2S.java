package com.dylanpdx.retro64.networking;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.Utils;
import com.dylanpdx.retro64.gui.SMC64HeartOverlay;
import com.dylanpdx.retro64.sm64.libsm64.AnimInfo;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.mixin.client.compat117plus.InGameHudMixin;

import java.io.IOException;

public class Retro64PacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.TOGGLE_MARIO, Retro64PacketsC2S::toggleMario);
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.CHANGE_HEIGHT, Retro64PacketsC2S::changeHeight);
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.ATTACK_PACKET, Retro64PacketsC2S::attackPacket);
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.MCHAR_PACKET, Retro64PacketsC2S::mcharPacket);
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.MODEL_PACKET, Retro64PacketsC2S::modelPacket);
        ServerPlayNetworking.registerGlobalReceiver(SM64PacketHandler.HEAL_PACKET, Retro64PacketsC2S::healPacket);
    }

    private static void healPacket(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        var amount = friendlyByteBuf.readByte();
        minecraftServer.execute(() -> {
            if (SM64EnvManager.selfMChar!=null) {
                SM64EnvManager.selfMChar.heal(amount);
            }
        });
    }

    private static void modelPacket(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        Player sender = Minecraft.getInstance().level.getPlayerByUUID(friendlyByteBuf.readUUID());
        if (sender!=null && !sender.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
            try {
                RemoteMCharHandler.updateMChar(sender,AnimInfo.deserialize(friendlyByteBuf.readByteArray()),friendlyByteBuf.readShort(),friendlyByteBuf.readShort(),friendlyByteBuf.readShort(),friendlyByteBuf.readInt(),friendlyByteBuf.readInt(),new Vec3(friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void mcharPacket(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        FriendlyByteBuf friendlyByteBuf1 = new FriendlyByteBuf(Unpooled.buffer());
        friendlyByteBuf1.writeUUID(friendlyByteBuf.readUUID());
        var pos = new Vec3(friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble());
        minecraftServer.execute(() -> {
            serverPlayer.setPos(pos.x(),pos.y(),pos.z());
            serverPlayer.noPhysics=true;
        });
        friendlyByteBuf1.writeDouble(pos.x);
        friendlyByteBuf1.writeDouble(pos.y);
        friendlyByteBuf1.writeDouble(pos.z);
        try {
            friendlyByteBuf1.writeByteArray(AnimInfo.deserialize(friendlyByteBuf.readByteArray()).serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        friendlyByteBuf1.writeShort(friendlyByteBuf.readShort());
        friendlyByteBuf1.writeShort(friendlyByteBuf.readShort());
        friendlyByteBuf1.writeShort(friendlyByteBuf.readShort());
        friendlyByteBuf1.writeInt(friendlyByteBuf.readInt());
        friendlyByteBuf1.writeInt(friendlyByteBuf.readInt());

        for (ServerPlayer player : PlayerLookup.tracking(serverPlayer)) {
            ServerPlayNetworking.send(player, SM64PacketHandler.MODEL_PACKET, friendlyByteBuf1);
        }
    }

    private static void attackPacket(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        var targetID = friendlyByteBuf.readInt();
        var entity = serverPlayer.getLevel().getEntity(targetID);
        assert entity != null;
        var angle = friendlyByteBuf.readFloat();
        minecraftServer.execute(() -> {
            Utils.attackPacketApplyKnockback(entity, angle);
        });
    }

    private static void changeHeight(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        boolean isMChar = friendlyByteBuf.readBoolean();
        minecraftServer.execute(() -> {
            ResourceLocation heightId = new ResourceLocation("pehkui", "hitbox_height");
            if(ScaleRegistries.SCALE_TYPES.containsKey(heightId)) {
                ScaleData height = ScaleRegistries.SCALE_TYPES.get(heightId).getScaleData(serverPlayer);
                if (isMChar) {
                    height.setScale(0.6f);
                } else {
                    height.setScale(1F);
                }
            }
            serverPlayer.refreshDimensions();
        });
    }

    private static void toggleMario(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl packetListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        boolean isEnabled = friendlyByteBuf.readBoolean();
        minecraftServer.execute(() -> {
            var capability = Utils.getSmc64Capability(serverPlayer);
            capability.setIsEnabled(isEnabled);
            capability.sync();
        });
    }

}
