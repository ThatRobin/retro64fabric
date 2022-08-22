package com.dylanpdx.retro64.gui;

import com.dylanpdx.retro64.Retro64;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.RemoteMCharHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.mixin.client.rendering.MixinInGameHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class SMC64HeartOverlay extends GuiComponent {

    Random random = new Random();

    public SMC64HeartOverlay() {
    }




}
