package net.xiaoyu.you_have_treasure;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    private static final ModConfigSpec.ConfigValue<List<? extends String>> TREASURE_ITEMS;
    
    static {
        BUILDER.push("You Have Treasure Config");
        
        TREASURE_ITEMS = BUILDER
                .comment("List of items that should be rendered on the player's head when carried")
                .defineListAllowEmpty("treasureItems", 
                        List.of(""), 
                        obj -> obj instanceof String string && isValidResourceLocation(string));
        
        BUILDER.pop();
    }
    
    public static final ModConfigSpec SPEC = BUILDER.build();
    
    public static List<? extends String> getTreasureItems() {
        return TREASURE_ITEMS.get();
    }
    
    public static void registerConfig(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);
    }
    
    private static boolean isValidResourceLocation(String string) {
        try {
            ResourceLocation.parse(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}