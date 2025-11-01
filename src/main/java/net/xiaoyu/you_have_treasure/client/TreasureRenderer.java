package net.xiaoyu.you_have_treasure.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.xiaoyu.you_have_treasure.network.NetworkHandler;
import net.xiaoyu.you_have_treasure.util.TreasureUtil;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TreasureRenderer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    
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
            return NetworkHandler.getTreasureItemsForEntity(player.getId());
        }

        return NetworkHandler.getTreasureItemsForEntity(entity.getId());
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