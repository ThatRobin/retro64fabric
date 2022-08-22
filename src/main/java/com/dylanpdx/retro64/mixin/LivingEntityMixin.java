package com.dylanpdx.retro64.mixin;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.networking.SM64PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void tick(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var targetEntity = (LivingEntity)(Object)this;
        if (targetEntity instanceof ServerPlayer) {
            if (!RemoteMCharHandler.getIsMChar((Player) targetEntity) || source.msgId.equals("outOfWorld"))
                return;
            // cancel certain types of damage so it's handled by SM64
            if (targetEntity.hurtMarked ||
                    source.msgId.equals("inWall") ||
                    source.msgId.equals("lava") ||
                    source.msgId.equals("drown") ||
                    source.msgId.equals("inFire") ||
                    source.msgId.equals("magic")) // poison?
                return;
            targetEntity.hurtMarked = true;
            Vec3 pos = targetEntity.position().add(targetEntity.getForward());
            if (source.getSourcePosition() != null) // if the source has a position, use that
                pos = source.getSourcePosition();
            FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
            friendlyByteBuf.writeDouble(pos.x);
            friendlyByteBuf.writeDouble(pos.y);
            friendlyByteBuf.writeDouble(pos.z);
            friendlyByteBuf.writeInt(1);
            for (ServerPlayer player : PlayerLookup.tracking(targetEntity)) {
                ServerPlayNetworking.send(player, SM64PacketHandler.DAMAGE_PACKET, friendlyByteBuf);
            }
        }
    }
}
