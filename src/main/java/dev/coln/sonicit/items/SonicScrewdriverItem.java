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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SonicScrewdriverItem extends Item {
    public SonicScrewdriverItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(KeyboardHelper.isHoldingControl()) {
            Item newItem = null;
            if(player.getItemInHand(hand).is(ItemInit.TEN_SCREWDRIVER.get())) {
                newItem = ItemInit.TEN_SCREWDRIVER_EXTENDED.get();
            } else if(player.getItemInHand(hand).is(ItemInit.ELEVEN_SCREWDRIVER.get())) {
                newItem = ItemInit.ELEVEN_SCREWDRIVER_EXTENDED.get();
            }
            ItemStack itemStack = new ItemStack(newItem);
            itemStack.setTag(player.getItemInHand(hand).getTag());
            player.setItemInHand(hand, itemStack);
            return super.use(level, player, hand);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag sonicNBT = stack.getTag();
        tooltip.add(Component.literal( ChatFormatting.DARK_PURPLE + "Current mode: " + ChatFormatting.WHITE + "Unextended"));
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
