package net.xiaoyu.you_have_treasure.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.xiaoyu.you_have_treasure.Config;

import java.util.ArrayList;
import java.util.List;

public class TreasureUtil {
    
    public static List<ItemStack> getCarriedTreasureItems(LivingEntity entity) {
        List<ItemStack> treasures = new ArrayList<>();
        List<? extends String> treasureItems = Config.getTreasureItems();

        // 主/副手
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        ItemStack[] handItems = {mainHand, offHand};

        for (ItemStack stack : handItems) {
            if (!stack.isEmpty() && isTreasureItem(stack, treasureItems)) {
                treasures.add(stack);
            }
        }

        // 玩家物品栏
        if (entity instanceof Player player) {
            Inventory inventory = player.getInventory();
            if (inventory != null) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty() && isTreasureItem(stack, treasureItems)) {
                        if (!stack.equals(mainHand) && !stack.equals(offHand)) {
                            treasures.add(stack);
                        }
                    }
                }
            }
        } else {
            // 实体物品槽位
            for (ItemStack stack : entity.getAllSlots()) {
                if (!stack.isEmpty() && isTreasureItem(stack, treasureItems)) {
                    if (!stack.equals(mainHand) && !stack.equals(offHand)) {
                        treasures.add(stack);
                    }
                }
            }
        }
        
        return treasures;
    }
    
    private static boolean isTreasureItem(ItemStack stack, List<? extends String> treasureItems) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && treasureItems.contains(itemId.toString());
    }
}