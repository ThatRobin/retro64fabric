package com.dylanpdx.retro64.capabilities;

import com.dylanpdx.retro64.RemoteMCharHandler;
import com.dylanpdx.retro64.Retro64;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class smc64CapabilityImplementation implements smc64CapabilityInterface {

    private static final String NBT_KEY_ENABLED = "smc64_enabled";

    private final Player player;

    private boolean isEnabled;

    public smc64CapabilityImplementation(Player player) {
        this.player = player;
    }

    @Override
    public boolean getIsEnabled() {
        return isEnabled;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public void sync() {
        smc64CapabilityInterface.sync(this.player);
        if (smc64Capability.INSTANCE.get(this.player).getIsEnabled()) {
            RemoteMCharHandler.mCharOn(this.player);
        } else {
            RemoteMCharHandler.mCharOff(this.player);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.isEnabled = tag.getBoolean(NBT_KEY_ENABLED);

    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean(NBT_KEY_ENABLED, this.isEnabled);
    }
}
