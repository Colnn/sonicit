package dev.coln.sonicit.items;

import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.List;

public class ExtendedSonicScrewdriverItem extends Item {
    String[] modes = new String[2];
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public ExtendedSonicScrewdriverItem(Item.Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(hand != InteractionHand.MAIN_HAND) {
            return super.use(level, player, hand);
        }
        this.modes[0] = "Basic";
        this.modes[1] = "Ranged";
        CompoundTag sonicNBT = player.getItemInHand(hand).getTag();
        if(sonicNBT == null) {
            player.getItemInHand(hand).setTag(new CompoundTag());
            sonicNBT = player.getItemInHand(hand).getTag();
            sonicNBT.putInt("mode", 1);
        }
        int mode = sonicNBT.getInt("mode");
        int affected = 0;
        if(KeyboardHelper.isHoldingControl()) {
            Item newItem = null;
            if(player.getItemInHand(hand).is(ItemInit.TEN_SCREWDRIVER_EXTENDED.get())) {
                newItem = ItemInit.TEN_SCREWDRIVER.get();
            } else if(player.getItemInHand(hand).is(ItemInit.ELEVEN_SCREWDRIVER_EXTENDED.get())) {
                newItem = ItemInit.ELEVEN_SCREWDRIVER.get();
            }
            ItemStack itemStack = new ItemStack(newItem);
            itemStack.setTag(player.getItemInHand(hand).getTag());
            player.setItemInHand(hand, itemStack);
            return super.use(level, player, hand);
        }
        if(KeyboardHelper.isHoldingShift()){
            player.getCooldowns().addCooldown(this, 3);
            if(mode >= modes.length) {
                mode = 1;
            } else {
                mode += 1;
            }
            player.displayClientMessage(Component.literal("Current: " + modes[mode-1]), true);
        }  else if(mode == 2) {
            int RANGE = 5;
            List<BlockPos> blockPosList = BlockPos.betweenClosedStream(player.blockPosition().getX() - RANGE, player.blockPosition().getY() - RANGE, player.blockPosition().getZ() - RANGE, player.blockPosition().getX() + RANGE, player.blockPosition().getY() + RANGE, player.blockPosition().getZ() + RANGE).map(BlockPos::immutable).toList();
            player.getCooldowns().addCooldown(this, 40);
            RandomSource randomSource = RandomSource.create();
            float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);
            level.playSound(player, player.blockPosition(), SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
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
        }
        sonicNBT.putInt("mode", mode);
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getHand() != InteractionHand.MAIN_HAND) {
            return super.useOn(context);
        }
        CompoundTag sonicNBT = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getTag();
        if(sonicNBT == null) {
            context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).setTag(new CompoundTag());
            sonicNBT = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getTag();
            sonicNBT.putInt("mode", 1);
        }
        int mode = sonicNBT.getInt("mode");
        Player player = context.getPlayer();
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        Block block = blockState.getBlock();

        RandomSource randomSource = RandomSource.create();
        float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);

        if(!KeyboardHelper.isHoldingShift()) {
            if(mode == 1) {
                if (block == Blocks.TNT) {
                    TntBlock tntBlock = (TntBlock) block;
                    tntBlock.onCaughtFire(blockState, context.getLevel(), context.getClickedPos(), null, null);
                    context.getLevel().setBlock(context.getClickedPos(), Blocks.AIR.defaultBlockState(), 0);
                    player.getCooldowns().addCooldown(this, 20);
                    context.getLevel().playSound(player, context.getPlayer().blockPosition(), SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
                } else if (block == Blocks.REDSTONE_LAMP) {
                    context.getLevel().setBlock(context.getClickedPos(), blockState.cycle(LIT), 2);
                    player.getCooldowns().addCooldown(this, 20);
                    context.getLevel().playSound(player, context.getPlayer().blockPosition(), SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
                } else if (block == Blocks.JUKEBOX) {
                    JukeboxBlock jukeboxBlock = (JukeboxBlock) block;
                    jukeboxBlock.setRecord(context.getPlayer(), context.getLevel(), context.getClickedPos(), blockState, new ItemStack(ItemInit.DWHO_THEME.get()));
                    context.getLevel().levelEvent((Player)null, 1010, context.getClickedPos(), Registry.ITEM.getId(ItemInit.DWHO_THEME.get()));
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag sonicNBT = stack.getTag();
        if(sonicNBT == null) {
            tooltip.add(Component.literal( ChatFormatting.DARK_PURPLE + "Current mode: " + ChatFormatting.WHITE + "Basic"));
        }
        else {
            tooltip.add(Component.literal( ChatFormatting.DARK_PURPLE + "Current mode: " + ChatFormatting.WHITE + modes[sonicNBT.getInt("mode")-1]));
        }
        if(KeyboardHelper.isHoldingShift()) {
            tooltip.add(Component.literal(ChatFormatting.WHITE + "Identified by the Daleks as a \"sonic probe\", the sonic screwdriver was considered to be very advanced Gallifreyan technology, although somebody could make one by using resources found on 21st century Earth with help from Stenza technology. During the Dalek-Movellan War, Davros dismissed the sonic screwdriver as a \"simple\" tool."));
        } else {
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Hold [SHIFT] for lore"));
        }
        if(KeyboardHelper.isHoldingControl()) {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Basic: "));
            tooltip.add(Component.literal(ChatFormatting.WHITE + "Right-click on a block to \"sonic\" it. Works on Redstone Lamps, TNT and Jukeboxes"));
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Ranged: "));
            tooltip.add(Component.literal(ChatFormatting.WHITE + "Right-click on to \"sonic\" blocks in a range of 5 blocks. Works on Redstone Lamps, TNT, Lightning rods and Jukeboxes"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Shift + Right-click to switch modes."));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "CTRL + Right-click to unextend."));
        } else {
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Hold [CTRL] for functions"));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
