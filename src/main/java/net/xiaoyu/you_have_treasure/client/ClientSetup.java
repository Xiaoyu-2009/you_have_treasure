package net.xiaoyu.you_have_treasure.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.model.HeadedModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.xiaoyu.you_have_treasure.YouHaveTreasure;

@EventBusSubscriber(modid = YouHaveTreasure.MOD_ID)
public class ClientSetup {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // 玩家渲染
        event.getSkins().forEach(skinName -> {
            PlayerRenderer renderer = event.getSkin(skinName);
            if (renderer != null) {
                renderer.addLayer(new TreasureRenderer<>(renderer));
            }
        });

        // 实体渲染
        for (EntityType<?> entityType : event.getEntityTypes()) {
            var renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                if (livingRenderer.getModel() instanceof HeadedModel) {
                    livingRenderer.addLayer(new TreasureRenderer(livingRenderer));
                }
            }
        }
    }
}