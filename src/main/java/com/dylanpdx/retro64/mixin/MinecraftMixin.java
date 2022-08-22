package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.gui.LibLoadWarnScreen;
import com.dylanpdx.retro64.sm64.libsm64.LibSM64;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    private static boolean initScreenDone = false;

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void setScreen(Screen guiScreen, CallbackInfo ci) {
        if (initScreenDone)
            return;
        var rom = SM64EnvManager.getROMFile();
        if (guiScreen instanceof TitleScreen){
            if (!LibSM64.libFileExists() || !LibSM64.isSupportedVersion() || rom==null){
                Component reason;
                if (!LibSM64.libFileExists())
                    reason = Component.translatable("menu.retro64.warnNoDLL");
                else if (!LibSM64.isSupportedVersion())
                    reason = Component.translatable("menu.retro64.warnWrongVersion");
                else// if (!rom.exists())
                    reason = Component.translatable("menu.retro64.warnMissingROM");
                ((Minecraft)(Object)this).setScreen(new LibLoadWarnScreen(reason));
            }else{
                try {
                    SM64EnvManager.initLib();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        initScreenDone = true;
    }
}
