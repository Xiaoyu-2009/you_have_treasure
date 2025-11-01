package net.xiaoyu.you_have_treasure.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.xiaoyu.you_have_treasure.YouHaveTreasure;
import net.xiaoyu.you_have_treasure.network.NetworkHandler;
import net.xiaoyu.you_have_treasure.util.TreasureUtil;

import java.util.List;

@EventBusSubscriber(modid = YouHaveTreasure.MOD_ID)
public class TreasureRenderer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    
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
    
    public TreasureRenderer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        List<ItemStack> treasureItems = getCarriedTreasureItems(entity);
        if (!treasureItems.isEmpty()) {
            renderTreasureItems(poseStack, buffer, packedLight, entity, treasureItems);
        }
    }
    
    private List<ItemStack> getCarriedTreasureItems(T entity) {
        if (entity instanceof Player player) {
            if (player == Minecraft.getInstance().player) {
                return TreasureUtil.getCarriedTreasureItems(player);
            }
            List<ItemStack> cachedItems = NetworkHandler.getTreasureItemsForEntity(player.getId());
            if (!cachedItems.isEmpty()) {
                return cachedItems;
            }
        }

        List<ItemStack> cachedItems = NetworkHandler.getTreasureItemsForEntity(entity.getId());
        if (!cachedItems.isEmpty()) {
            return cachedItems;
        }
        return TreasureUtil.getCarriedTreasureItems(entity);
    }
    
    private void renderTreasureItems(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, List<ItemStack> itemStacks) {
        poseStack.pushPose();

        this.getParentModel().getHead().translateAndRotate(poseStack);

        for (int i = 0; i < itemStacks.size(); i++) {
            poseStack.pushPose();

            poseStack.translate(0.0D, -i * 0.5D, 0.0D);

            if (itemStacks.get(i).getItem() instanceof BlockItem) {
                // 方块
                poseStack.translate(0.0D, -0.75D, 0.0D);
            } else {
                // 物品
                poseStack.translate(0.0D, -0.35D, 0.0D);
            }

            poseStack.scale(0.5F, 0.5F, 0.5F);

            poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStacks.get(i),
                ItemDisplayContext.HEAD,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
            );
            
            poseStack.popPose();
        }
        
        poseStack.popPose();
    }
}