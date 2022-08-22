package com.dylanpdx.retro64.creativetabs;

import com.dylanpdx.retro64.RegistryHandler;
import com.dylanpdx.retro64.Retro64;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class Retro64ItemGroup {
    public static final CreativeModeTab RETRO64_TAB = FabricItemGroupBuilder.build(new ResourceLocation(Retro64.MOD_ID,"retro64tab"), () -> new ItemStack(RegistryHandler.CASTLE_STAIRS.asItem()));
}
