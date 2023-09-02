package dev.coln.sonicit.networking.packet.customizer;

import dev.coln.sonicit.block.entity.SonicCustomizerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CustomizeSonicC2SPacket {
    public ItemStack itemStack;
    public BlockPos blockPos;
    public CustomizeSonicC2SPacket(ItemStack itemStack, BlockPos blockPos) {
        this.itemStack = itemStack;
        this.blockPos = blockPos;
    }
    public CustomizeSonicC2SPacket(FriendlyByteBuf buf) {
        itemStack = buf.readItem();
        blockPos = buf.readBlockPos();
    }
    public static void toBytes(CustomizeSonicC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeItem(packet.itemStack);
        buf.writeBlockPos(packet.blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.getSender().getLevel().getBlockEntity(blockPos);
            if(blockEntity instanceof SonicCustomizerBlockEntity) {
                SonicCustomizerBlockEntity blockEntity1 = (SonicCustomizerBlockEntity) blockEntity;
                itemStack.setTag(blockEntity1.itemHandler.getStackInSlot(0).getTag());
                if(!blockEntity1.itemHandler.getStackInSlot(0).isEmpty()) {
                    blockEntity1.itemHandler.setStackInSlot(0, itemStack);
                }
            }
        });
        return true;
    }
}
