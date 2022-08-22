package com.dylanpdx.retro64;

import com.dylanpdx.retro64.events.clientEvents;
import com.dylanpdx.retro64.networking.Retro64PacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;

public class Retro64Client implements ClientModInitializer {

    private static final RenderType CUTOUT = RenderType.cutout();
    public static boolean hasControllerSupport=false;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegistryHandler.CASTLE_STAIRS, CUTOUT);
        Keybinds.register();
        clientEvents cEvent=new clientEvents();
        cEvent.gameTick();
        Retro64PacketsS2C.register();
        if (FabricLoader.getInstance().isModLoaded("controllable")) {
            hasControllerSupport=true;
        }


    }
}
