package net.xiaoyu.you_have_treasure;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.xiaoyu.you_have_treasure.network.NetworkHandler;

@Mod(YouHaveTreasure.MOD_ID)
public class YouHaveTreasure {
    public static final String MOD_ID = "you_have_treasure";

    public YouHaveTreasure(ModContainer modContainer, IEventBus modEventBus) {
        Config.registerConfig(modContainer);
        modEventBus.addListener(NetworkHandler::register);
    }
}