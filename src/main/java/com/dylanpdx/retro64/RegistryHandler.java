package com.dylanpdx.retro64;

import com.dylanpdx.retro64.blocks.CastleStairsBlock;
import com.dylanpdx.retro64.blocks.DeepQuicksandBlock;
import com.dylanpdx.retro64.blocks.InstantQuicksandBlock;
import com.dylanpdx.retro64.creativetabs.Retro64ItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RegistryHandler {

    public static final Block CASTLE_STAIRS =  new CastleStairsBlock();
    public static final Block DEEP_QUICKSAND =  new DeepQuicksandBlock();
    public static final Block INSTANT_QUICKSAND =  new InstantQuicksandBlock();

    public static void register(){
        Registry.register(Registry.BLOCK, new ResourceLocation(Retro64.MOD_ID, "castlestairs"), CASTLE_STAIRS);
        Registry.register(Registry.BLOCK, new ResourceLocation(Retro64.MOD_ID, "deep_quicksand"), DEEP_QUICKSAND);
        Registry.register(Registry.BLOCK, new ResourceLocation(Retro64.MOD_ID, "instant_quicksand"), INSTANT_QUICKSAND);
        Registry.register(Registry.ITEM, new ResourceLocation(Retro64.MOD_ID, "castlestairs"), new BlockItem(CASTLE_STAIRS,new Item.Properties().tab(Retro64ItemGroup.RETRO64_TAB)));
        Registry.register(Registry.ITEM, new ResourceLocation(Retro64.MOD_ID, "deep_quicksand"), new BlockItem(DEEP_QUICKSAND,new Item.Properties().tab(Retro64ItemGroup.RETRO64_TAB)));
        Registry.register(Registry.ITEM, new ResourceLocation(Retro64.MOD_ID, "instant_quicksand"), new BlockItem(INSTANT_QUICKSAND,new Item.Properties().tab(Retro64ItemGroup.RETRO64_TAB)));
    }

}
