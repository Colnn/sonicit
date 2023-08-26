package dev.coln.sonicit.networking.packet;

import dev.coln.sonicit.init.ItemInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExtendSonicC2SPacket {
    public ExtendSonicC2SPacket() {

    }
    public ExtendSonicC2SPacket(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            Item item = itemStack.getItem();
            ItemStack newItem;
            if(item == ItemInit.ELEVEN_SCREWDRIVER.get()) {
                newItem = new ItemStack(ItemInit.ELEVEN_SCREWDRIVER_EXTENDED.get());
                newItem.setTag(itemStack.getTag());
                player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
            } else if(item == ItemInit.TEN_SCREWDRIVER.get()) {
                newItem = new ItemStack(ItemInit.TEN_SCREWDRIVER_EXTENDED.get());
                newItem.setTag(itemStack.getTag());
                player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
            } else if(item == ItemInit.ELEVEN_SCREWDRIVER_EXTENDED.get()) {
                newItem = new ItemStack(ItemInit.ELEVEN_SCREWDRIVER.get());
                newItem.setTag(itemStack.getTag());
                player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
            } else if(item == ItemInit.TEN_SCREWDRIVER_EXTENDED.get()) {
                newItem = new ItemStack(ItemInit.TEN_SCREWDRIVER.get());
                newItem.setTag(itemStack.getTag());
                player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
            }
        });
        return true;
    }
}
