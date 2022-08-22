package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.Retro64;
import com.dylanpdx.retro64.SM64EnvManager;
import com.dylanpdx.retro64.Utils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow private int screenWidth;

    @Shadow private int screenHeight;

    @Shadow @Final private RandomSource random;

    @Shadow protected abstract void renderHeart(PoseStack poseStack, Gui.HeartType heartType, int x, int y, int i, boolean bl, boolean bl2);

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    public void cancelHeartRender(PoseStack poseStack, CallbackInfo ci) {
        var capability = Utils.getSmc64Capability(this.getCameraPlayer());
        if(capability.getIsEnabled() && SM64EnvManager.selfMChar != null) {
            render(poseStack, this.getCameraPlayer(), this.screenWidth, this.screenHeight);
            ci.cancel();
        }
    }

    public void render(PoseStack mStack, Player player, int width, int height) {
        if (!RemoteMCharHandler.getIsMChar(player) || (SM64EnvManager.selfMChar.state==null))
            return;
        var healthSlices = (SM64EnvManager.selfMChar.state.health&0xff00)>>8;
        RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);
        float healthMax=8.0f;
        int absorb=0;
        int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int left_height = 40;
        int left = width / 2 - 91;
        int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;
        renderHearts(mStack, player,left,top,11,-1,healthMax,healthSlices,8,absorb,false);
    }

    protected void renderHearts(PoseStack poseStack, Player thePlayer, int left, int top, int rowHeight, int regen, float healthMax, int health, int healthLast, int absorb, boolean highlight) {
        Gui.HeartType gui$hearttype = Gui.HeartType.NORMAL;
        int i = 9 * (thePlayer.level.getLevelData().isHardcore() ? 5 : 0);
        int j = Mth.ceil((double)healthMax / 2.0D);
        int k = Mth.ceil((double)absorb / 2.0D);
        int l = j * 2;

        for(int i1 = j + k - 1; i1 >= 0; --i1) {
            int j1 = i1 / 10;
            int k1 = i1 % 10;
            int l1 = left + k1 * 8;
            int i2 = top - j1 * rowHeight;
            if (health + absorb <= 2) {
                i2 += this.random.nextInt(2);
            }

            if (i1 < j && i1 == regen) {
                i2 -= 2;
            }

            this.renderHeart(poseStack, Gui.HeartType.CONTAINER, l1, i2, i, highlight, false);
            int j2 = i1 * 2;
            boolean flag = i1 >= j;
            if (flag) {
                int k2 = j2 - l;
                if (k2 < absorb) {
                    boolean flag1 = k2 + 1 == absorb;
                    this.renderHeart(poseStack, gui$hearttype == Gui.HeartType.WITHERED ? gui$hearttype : Gui.HeartType.ABSORBING, l1, i2, i, false, flag1);
                }
            }

            if (highlight && j2 < healthLast) {
                boolean flag2 = j2 + 1 == healthLast;
                this.renderHeart(poseStack, gui$hearttype, l1, i2, i, true, flag2);
            }

            if (j2 < health) {
                boolean flag3 = j2 + 1 == health;
                this.renderHeart(poseStack, gui$hearttype, l1, i2, i, false, flag3);
            }
        }

    }
}
