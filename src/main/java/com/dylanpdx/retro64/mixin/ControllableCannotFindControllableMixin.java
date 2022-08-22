package com.dylanpdx.retro64.mixin;

import com.jab125.thonkutil.api.events.client.screen.TitleScreenRenderEvent;
import com.mrcrayfish.controllable.Controllable;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Controllable.CannotFindControllable.class, remap = false)
public class ControllableCannotFindControllableMixin {

    @Shadow private static boolean hasPoppedUp;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void render(TitleScreenRenderEvent a, CallbackInfo ci) {
        if (!hasPoppedUp) {
            Minecraft.getInstance().getToasts().addToast(new Controllable.CannotFindControllable.CannotFindControllableToast());
            hasPoppedUp = true;
        }
        ci.cancel();
    }
}
