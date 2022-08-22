package com.dylanpdx.retro64;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

public class Retro64GameRules {

    public static void register(){
        GameRuleRegistry.register("forceMario", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    }
}
