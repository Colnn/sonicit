package dev.coln.sonicit.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SwitchSonicC2SPacket {
    String[] modes = new String[2];

    public SwitchSonicC2SPacket() {

    }
    public SwitchSonicC2SPacket(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            this.modes[0] = "Basic";
            this.modes[1] = "Ranged";

            ServerPlayer player = context.getSender();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            CompoundTag sonicNBT = player.getItemInHand(hand).getTag();
            if(sonicNBT == null) {
                player.getItemInHand(hand).setTag(new CompoundTag());
                sonicNBT = player.getItemInHand(hand).getTag();
                sonicNBT.putInt("mode", 1);
            }
            int mode = sonicNBT.getInt("mode");

            player.getCooldowns().addCooldown(player.getItemInHand(InteractionHand.MAIN_HAND).getItem(), 3);
            if(mode >= modes.length) {
                mode = 1;
            } else {
                mode += 1;
            }
            sonicNBT.putInt("mode", mode);
            player.displayClientMessage(Component.literal("Current: " + modes[mode-1]), true);
        });
        return true;
    }
}
