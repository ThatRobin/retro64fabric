package com.dylanpdx.retro64.config;

import com.dylanpdx.retro64.Retro64;
import com.mojang.datafixers.util.Pair;

public class Retro64Config {

    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String ROM_PATH;


    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(Retro64.MOD_ID + "_config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("retro64.rompath", "mods/baserom.us.z64"), "The path to the ROM file to be loaded");
    }

    private static void assignConfigs() {
        ROM_PATH = CONFIG.getOrDefault("retro64.rompath", "mods/baserom.us.z64");

        Retro64.LOGGER.info("All " + configs.getConfigsList().size() + " have been assigned properly");
    }

}
