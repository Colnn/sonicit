package dev.coln.sonicit.networking;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(SonicIt.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(BasicSonicC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(BasicSonicC2SPacket::new)
                .encoder(BasicSonicC2SPacket::toBytes)
                .consumerMainThread(BasicSonicC2SPacket::handle)
                .add();

        net.messageBuilder(RangedSonicC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RangedSonicC2SPacket::new)
                .encoder(RangedSonicC2SPacket::toBytes)
                .consumerMainThread(RangedSonicC2SPacket::handle)
                .add();

        net.messageBuilder(ExtendSonicC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ExtendSonicC2SPacket::new)
                .encoder(ExtendSonicC2SPacket::toBytes)
                .consumerMainThread(ExtendSonicC2SPacket::handle)
                .add();

        net.messageBuilder(SwitchSonicC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SwitchSonicC2SPacket::new)
                .encoder(SwitchSonicC2SPacket::toBytes)
                .consumerMainThread(SwitchSonicC2SPacket::handle)
                .add();

        net.messageBuilder(SonicSoundS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SonicSoundS2CPacket::new)
                .encoder(SonicSoundS2CPacket::toBytes)
                .consumerMainThread(SonicSoundS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
