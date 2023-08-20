package dev.coln.sonicit.networking;

import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.packet.SonicSoundS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandlerClass {
    public static void handlePacket(SonicSoundS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        RandomSource randomSource = RandomSource.create();
        float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);
        Minecraft.getInstance().player.getLevel().playSound(Minecraft.getInstance().player, msg.blockPos, SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
    }
}
