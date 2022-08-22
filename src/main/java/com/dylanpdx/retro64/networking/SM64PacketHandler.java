package com.dylanpdx.retro64.networking;

import com.dylanpdx.retro64.Retro64;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class SM64PacketHandler {

    public static final ResourceLocation TOGGLE_MARIO = new ResourceLocation(Retro64.MOD_ID, "toggle_mario");
    public static final ResourceLocation CHANGE_HEIGHT = new ResourceLocation(Retro64.MOD_ID, "change_height");
    public static final ResourceLocation ATTACK_PACKET = new ResourceLocation(Retro64.MOD_ID, "attack_packet");
    public static final ResourceLocation MCHAR_PACKET = new ResourceLocation(Retro64.MOD_ID, "mchar_packet");
    public static final ResourceLocation MODEL_PACKET = new ResourceLocation(Retro64.MOD_ID, "model_packet");

    public static final ResourceLocation DISABLE_MARIO = new ResourceLocation(Retro64.MOD_ID, "disable_mario");
    public static final ResourceLocation DAMAGE_PACKET = new ResourceLocation(Retro64.MOD_ID, "damage_packet");
    public static final ResourceLocation HEAL_PACKET = new ResourceLocation(Retro64.MOD_ID, "heal_packet");

}
