package com.dylanpdx.retro64.capabilities;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.world.entity.player.Player;

public interface smc64CapabilityInterface extends AutoSyncedComponent {

    boolean getIsEnabled();
    void setIsEnabled(boolean isEnabled);

    void sync();

    static void sync(Player player) {
        smc64Capability.INSTANCE.sync(player);
    }
}
