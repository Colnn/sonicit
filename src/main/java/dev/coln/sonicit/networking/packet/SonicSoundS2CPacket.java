package dev.coln.sonicit.networking.packet;

import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.ClientPacketHandlerClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SonicSoundS2CPacket {
    public BlockPos blockPos;
    public SonicSoundS2CPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    public SonicSoundS2CPacket(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
    }
    public static void toBytes(SonicSoundS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.blockPos);
    }

    public static boolean handle(SonicSoundS2CPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(msg, context));
        });
        return true;
    }
}
