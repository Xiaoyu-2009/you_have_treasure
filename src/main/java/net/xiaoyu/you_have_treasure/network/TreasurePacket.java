package net.xiaoyu.you_have_treasure.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.xiaoyu.you_have_treasure.YouHaveTreasure;

import java.util.List;

public record TreasurePacket(int playerId, List<ItemStack> treasureItems) implements CustomPacketPayload {
    public static final Type<TreasurePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
        YouHaveTreasure.MOD_ID, "treasure_packet"
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, TreasurePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        TreasurePacket::playerId,
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
        TreasurePacket::treasureItems,
        TreasurePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}