package com.dylanpdx.retro64.capabilities;

import com.dylanpdx.retro64.Retro64;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;

public class smc64Capability implements EntityComponentInitializer {


    public static final ComponentKey<smc64CapabilityInterface> INSTANCE;

    static {
        INSTANCE = ComponentRegistry.getOrCreate(new ResourceLocation(Retro64.MOD_ID, "mario"), smc64CapabilityInterface.class);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(INSTANCE, smc64CapabilityImplementation::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
