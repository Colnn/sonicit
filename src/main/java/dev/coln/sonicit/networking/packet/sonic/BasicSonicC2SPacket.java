package dev.coln.sonicit.networking.packet.sonic;

import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BasicSonicC2SPacket {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public BasicSonicC2SPacket() {

    }
    public BasicSonicC2SPacket(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            BlockPos blockPos = getBlockPos(level, player);
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            RandomSource randomSource = RandomSource.create();
            float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);
            if (block == Blocks.TNT) {
                TntBlock tntBlock = (TntBlock) block;
                tntBlock.onCaughtFire(blockState, level, blockPos, null, null);
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 0);
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
                ModMessages.sendToAllPlayers(new SonicSoundS2CPacket(blockPos));
            } else if (block == Blocks.REDSTONE_LAMP) {
                level.setBlock(blockPos, blockState.cycle(LIT), 2);
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
                level.playSound(player, blockPos, SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
                ModMessages.sendToAllPlayers(new SonicSoundS2CPacket(blockPos));
            } else if (block == Blocks.JUKEBOX) {
                JukeboxBlock jukeboxBlock = (JukeboxBlock) block;
                jukeboxBlock.setRecord(player, level, blockPos, blockState, new ItemStack(ItemInit.DWHO_THEME.get()));
                level.levelEvent((Player)null, 1010, blockPos, Registry.ITEM.getId(ItemInit.DWHO_THEME.get()));
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
                ModMessages.sendToAllPlayers(new SonicSoundS2CPacket(blockPos));
            }
        });
        return true;
    }
    private BlockPos getBlockPos(ServerLevel level, Player player) {
        HitResult block = player.pick(20.0D, 0.0F, false);
        if(block.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) block).getBlockPos();
            return blockpos;
        }
        return null;
    }
}
