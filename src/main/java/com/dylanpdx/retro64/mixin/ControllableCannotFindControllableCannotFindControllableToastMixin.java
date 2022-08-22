package com.dylanpdx.retro64.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.controllable.Controllable;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Controllable.CannotFindControllable.CannotFindControllableToast.class, remap = false)
public class ControllableCannotFindControllableCannotFindControllableToastMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void render(PoseStack poseStack, ToastComponent toastComponent, long delta, CallbackInfoReturnable<Toast.Visibility> cir) {
        cir.setReturnValue(Toast.Visibility.HIDE);
    }

}
