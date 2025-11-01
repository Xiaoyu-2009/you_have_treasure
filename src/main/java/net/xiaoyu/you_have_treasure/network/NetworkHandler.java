package net.xiaoyu.you_have_treasure.network;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.xiaoyu.you_have_treasure.YouHaveTreasure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class NetworkHandler {
    private static final Map<Integer, List<ItemStack>> entityTreasureMap = new HashMap<>();
    
    public static void handleTreasurePacket(TreasurePacket packet) {
        entityTreasureMap.put(packet.playerId(), packet.treasureItems());
    }

    public static List<ItemStack> getTreasureItemsForEntity(int entityId) {
        return entityTreasureMap.getOrDefault(entityId, Collections.emptyList());
    }
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(YouHaveTreasure.MOD_ID).optional();

        registrar.playBidirectional(
            TreasurePacket.TYPE,
            TreasurePacket.STREAM_CODEC,
            (payload, context) -> {
                if (context.flow().isClientbound()) {
                    context.enqueueWork(() -> {
                        handleTreasurePacket(payload);
                    });
                }
            }
        );
    }
}