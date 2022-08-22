package com.dylanpdx.retro64;

import com.dylanpdx.retro64.config.Retro64Config;
import com.dylanpdx.retro64.events.serverEvents;
import com.dylanpdx.retro64.gui.SMC64HeartOverlay;
import com.dylanpdx.retro64.networking.Retro64PacketsC2S;
import com.dylanpdx.retro64.networking.SM64PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.mixin.event.lifecycle.PlayerManagerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Retro64 implements ModInitializer {

    public static final String MOD_ID = "retro64";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final SMC64HeartOverlay HEART_OVERLAY = new SMC64HeartOverlay();

    @Override
    public void onInitialize() {
        RegistryHandler.register();
        Retro64PacketsC2S.register();

        Retro64Config.registerConfigs();

        ServerPlayConnectionEvents.INIT.register((packetListener, minecraftServer) -> {
            if (packetListener.player != null){
                Player plr =  packetListener.player;
                if (plr.isLocalPlayer() && RemoteMCharHandler.wasMCharDimm!=null && RemoteMCharHandler.wasMCharDimm!=plr.level.dimension()){
                    // Very lazy fix - Don't tick the player until the world finishes loading
                    // TODO: timeout?
                    Thread t = new Thread(()->{
                        while (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        RemoteMCharHandler.mCharOn(plr);
                    });
                    t.start();

                }
            }
        });

        serverEvents sEvent = new serverEvents();

    }

}
