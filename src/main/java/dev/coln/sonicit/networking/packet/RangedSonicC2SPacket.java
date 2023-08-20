package dev.coln.sonicit.networking.packet;

import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class RangedSonicC2SPacket {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RangedSonicC2SPacket() {

    }
    public RangedSonicC2SPacket(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            int affected = 0;

            int RANGE = 5;
            List<BlockPos> blockPosList = BlockPos.betweenClosedStream(player.blockPosition().getX() - RANGE, player.blockPosition().getY() - RANGE, player.blockPosition().getZ() - RANGE, player.blockPosition().getX() + RANGE, player.blockPosition().getY() + RANGE, player.blockPosition().getZ() + RANGE).map(BlockPos::immutable).toList();
            player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 40);
            RandomSource randomSource = RandomSource.create();
            float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);
            ModMessages.sendToAllPlayers(new SonicSoundS2CPacket(player.blockPosition()));
            for (BlockPos blockPos : blockPosList) {
                BlockState blockState1 = level.getBlockState(blockPos);
                Block block1 = blockState1.getBlock();
                if(block1 == Blocks.TNT) {
                    TntBlock tntBlock = (TntBlock) block1;
                    tntBlock.onCaughtFire(blockState1, level, blockPos, null, null);
                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 1);
                    affected += 1;
                } else if (block1 == Blocks.REDSTONE_LAMP) {
                    level.setBlock(blockPos, blockState1.cycle(LIT) ,2);
                    affected += 1;
                } else if (block1 == Blocks.LIGHTNING_ROD) {
                    LightningRodBlock lightningRodBlock = (LightningRodBlock) block1;
                    lightningRodBlock.onLightningStrike(blockState1, level, blockPos);
                    affected += 1;
                } else if (block1 == Blocks.JUKEBOX) {
                    JukeboxBlock jukeboxBlock = (JukeboxBlock) block1;
                    jukeboxBlock.setRecord(player, level, blockPos, blockState1, new ItemStack(ItemInit.DWHO_THEME.get()));
                    level.levelEvent((Player)null, 1010, blockPos, Registry.ITEM.getId(ItemInit.DWHO_THEME.get()));
                }
            }
            player.displayClientMessage(Component.literal(ChatFormatting.GREEN + "Affected " + affected + " blocks."), true);
        });
        return true;
    }
}
