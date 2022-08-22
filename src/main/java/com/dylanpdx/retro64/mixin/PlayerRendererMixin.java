package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.mCharRenderer;
import com.dylanpdx.retro64.sm64.libsm64.MChar;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dylanpdx.retro64.events.clientEvents.mCharTick;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    public void renderMario(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!RemoteMCharHandler.getIsMChar(entity)) // don't render if not in R64 mode
            return;

        if (!entity.isLocalPlayer())
        {
            // Render remote players (multiplayer)
            RemoteMCharHandler.tickAll(); // Tick the animation of all remote players
            //mCharRenderer.renderOtherPlayer(rpe);
            if (RemoteMCharHandler.mChars.containsKey(entity)){
                // render mChar for other player
                MChar otherMChar = RemoteMCharHandler.mChars.get(entity);
                mCharRenderer.renderMChar(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight, otherMChar);
            }
            ((PlayerRenderer)(Object)this).renderNameTag(entity, entity.getDisplayName(), matrixStack, buffer, packedLight);
            return;
        } else {
            // Prevent player from being ticked if rendered in UI
            if (!(packedLight == 15728880 && partialTicks == 1.0F))
                mCharTick();
            else // Face the camera if in UI!
                matrixStack.mulPose(Quaternion.fromXYZ(
                        (float)Math.toRadians(0),
                        (float)Math.toRadians(180)- SM64EnvManager.selfMChar.state.faceAngle,
                        (float)Math.toRadians(0)));
            mCharRenderer.renderMChar(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight, SM64EnvManager.selfMChar);
            ci.cancel(); // prevent vanilla rendering
        }
    }

}
