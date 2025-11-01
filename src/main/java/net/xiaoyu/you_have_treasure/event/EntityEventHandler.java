package net.xiaoyu.you_have_treasure.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.xiaoyu.you_have_treasure.YouHaveTreasure;
import net.xiaoyu.you_have_treasure.network.TreasurePacket;
import net.xiaoyu.you_have_treasure.util.TreasureUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = YouHaveTreasure.MOD_ID)
public class EntityEventHandler {
    private static final Map<UUID, String> entityLastTreasureData = new HashMap<>();
    
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!(livingEntity.level() instanceof ServerLevel)) {
            return;
        }

        List<ItemStack> treasureItems = TreasureUtil.getCarriedTreasureItems(livingEntity);

        StringBuilder sb = new StringBuilder();
        for (ItemStack stack : treasureItems) {
            sb.append(stack.getItem()).append(':').append(stack.getCount()).append(';');
        }
        String currentData = sb.toString();

        UUID entityId = livingEntity.getUUID();
        String lastData = entityLastTreasureData.getOrDefault(entityId, "");
        
        if (!currentData.equals(lastData)) {
            TreasurePacket packet = new TreasurePacket(livingEntity.getId(), treasureItems);
            PacketDistributor.sendToPlayersTrackingEntity(livingEntity, packet);

            entityLastTreasureData.put(entityId, currentData);
        }
    }
}