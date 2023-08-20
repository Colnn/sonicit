package dev.coln.sonicit.items;

import dev.coln.sonicit.networking.ModMessages;
import dev.coln.sonicit.networking.packet.BasicSonicC2SPacket;
import dev.coln.sonicit.networking.packet.ExtendSonicC2SPacket;
import dev.coln.sonicit.networking.packet.RangedSonicC2SPacket;
import dev.coln.sonicit.networking.packet.SwitchSonicC2SPacket;
import dev.coln.sonicit.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ExtendedSonicScrewdriverItem extends Item {
    String[] modes = new String[2];
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;


    public ExtendedSonicScrewdriverItem(Item.Properties properties) { super(properties); }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(hand != InteractionHand.MAIN_HAND) {
            return super.use(level, player, hand);
        }
        CompoundTag sonicNBT = player.getItemInHand(hand).getTag();
        if(sonicNBT == null) {
            player.getItemInHand(hand).setTag(new CompoundTag());
            sonicNBT = player.getItemInHand(hand).getTag();
            sonicNBT.putInt("mode", 1);
        }
        int mode = sonicNBT.getInt("mode");

        if(KeyboardHelper.isHoldingControl()) {
            ModMessages.sendToServer(new ExtendSonicC2SPacket());
            return super.use(level, player, hand);
        } else if(KeyboardHelper.isHoldingShift()){
            ModMessages.sendToServer(new SwitchSonicC2SPacket());
            return super.use(level, player, hand);
        } else {
            if(mode == 2) {
                ModMessages.sendToServer(new RangedSonicC2SPacket());
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
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
        if(mode == 1) {
            ModMessages.sendToServer(new BasicSonicC2SPacket());
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag sonicNBT = stack.getTag();
        this.modes[0] = "Basic";
        this.modes[1] = "Ranged";

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
