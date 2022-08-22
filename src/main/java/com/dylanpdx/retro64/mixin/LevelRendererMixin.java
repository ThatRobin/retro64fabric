package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.RenType;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.sm64.SM64SurfaceType;
import com.dylanpdx.retro64.sm64.libsm64.LibSM64;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dylanpdx.retro64.events.clientEvents.isDebug;
import static com.dylanpdx.retro64.events.clientEvents.mCharTick;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V", at = @At("TAIL"))
    public void renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        LocalPlayer plr = Minecraft.getInstance().player;
        if (plr.isAlive() && RemoteMCharHandler.getIsMChar(plr)){
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON){
                mCharTick();
            }
            if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT){
                Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
            }
        }
        // render debug
        if (isDebug()){
            var stack = poseStack;
            var rt = RenType.getDebugRenderType();
            var buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(rt);
            stack.pushPose();
            Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            stack.translate(-cam.x, -cam.y, -cam.z);
            PoseStack.Pose p = stack.last();
            for (var surf : SM64EnvManager.surfaces){
                float cR=0;
                float cG=0;
                float cB=0;
                if (surf.type == (short) SM64SurfaceType.SURFACE_BURNING.value) {
                    cR = 1;
                    cG = 0;
                    cB = 0;
                } else if (surf.type == (short) SM64SurfaceType.SURFACE_HANGABLE.value) {
                    cR = 1;
                    cG = 0;
                    cB = 1;
                } else if (surf.type == (short) SM64SurfaceType.SURFACE_ICE.value) {
                    cR = 0;
                    cG = 1;
                    cB = 1;
                } else if (surf.type == (short) SM64SurfaceType.SURFACE_SHALLOW_QUICKSAND.value) {
                    cR = .3f;
                    cG = .3f;
                    cB = .3f;
                } else {
                    cR = 1;
                    cG = 1;
                    cB = 1;
                }
                for (int i = 0; i < surf.vertices.length; i += 3)
                    buffer.vertex(p.pose(),surf.vertices[i]/ LibSM64.SCALE_FACTOR,(surf.vertices[i+1]/LibSM64.SCALE_FACTOR)+0.01f,surf.vertices[i+2]/LibSM64.SCALE_FACTOR).color(cR,cG,cB,1f).endVertex();
            }
            stack.popPose();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(rt);
        }
    }
}
