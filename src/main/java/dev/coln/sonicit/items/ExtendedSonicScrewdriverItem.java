package dev.coln.sonicit.items;

import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.ModMessages;
import dev.coln.sonicit.networking.packet.sonic.*;
import dev.coln.sonicit.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public class ExtendedSonicScrewdriverItem extends Item {
    String[] modes = new String[3];
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
            if(Minecraft.getInstance().isLocalServer()) {
                ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                Item item = itemStack.getItem();
                ItemStack newItem;
                if(item == ItemInit.ELEVEN_SCREWDRIVER_EXTENDED.get()) {
                    newItem = new ItemStack(ItemInit.ELEVEN_SCREWDRIVER.get());
                    newItem.setTag(itemStack.getTag());
                    player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
                } else if(item == ItemInit.TEN_SCREWDRIVER_EXTENDED.get()) {
                    newItem = new ItemStack(ItemInit.TEN_SCREWDRIVER.get());
                    newItem.setTag(itemStack.getTag());
                    player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
                }
            } else {
                ModMessages.sendToServer(new ExtendSonicC2SPacket());
            }
            return super.use(level, player, hand);
        } else if(KeyboardHelper.isHoldingShift()){
            this.modes[0] = "Basic";
            this.modes[1] = "Ranged";
            this.modes[2] = "Confuse";
            if(Minecraft.getInstance().isLocalServer()) {
                player.getCooldowns().addCooldown(player.getItemInHand(InteractionHand.MAIN_HAND).getItem(), 3);
                if(mode >= modes.length) {
                    mode = 1;
                } else {
                    mode += 1;
                }
                sonicNBT.putInt("mode", mode);
                player.displayClientMessage(Component.literal("Current: " + modes[mode-1]), true);
            } else {
                ModMessages.sendToServer(new SwitchSonicC2SPacket());
            }
            return super.use(level, player, hand);
        } else {
            if(Minecraft.getInstance().isLocalServer()) {
                if(mode == 2) {
                    int affected = 0;
                    int RANGE = 5;
                    List<BlockPos> blockPosList = BlockPos.betweenClosedStream(player.blockPosition().getX() - RANGE, player.blockPosition().getY() - RANGE, player.blockPosition().getZ() - RANGE, player.blockPosition().getX() + RANGE, player.blockPosition().getY() + RANGE, player.blockPosition().getZ() + RANGE).map(BlockPos::immutable).toList();
                    player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 40);
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
                } else if(mode == 3) {
                    int affected = 0;
                    int RANGE = 5;

                    BlockPos topCorner = player.blockPosition().offset(RANGE, RANGE, RANGE);
                    BlockPos bottomCorner = player.blockPosition().offset(-RANGE, -RANGE, -RANGE);
                    AABB box = new AABB(topCorner, bottomCorner);

                    player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 40);
                    List<Entity> entities = level.getEntities(null, box);
                    for (Entity target : entities){
                        if (target instanceof LivingEntity){
                            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1));
                            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 10));
                            affected += 1;
                        }
                    }
                    player.displayClientMessage(Component.literal(ChatFormatting.GREEN + "Affected " + affected + " blocks."), true);
                }
            } else {
                if(mode == 2) {
                    ModMessages.sendToServer(new RangedSonicC2SPacket());
                } else if(mode == 3) {
                    ModMessages.sendToServer(new ConfuseSonicC2SPacket());
                }
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
        if(Minecraft.getInstance().isLocalServer()) {
            Player player = context.getPlayer();
            Level level = context.getLevel();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            BlockPos blockPos = context.getClickedPos();
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            RandomSource randomSource = RandomSource.create();
            float random = Mth.randomBetween(randomSource, 0.5f, 2.0f);
            if (block == Blocks.TNT) {
                TntBlock tntBlock = (TntBlock) block;
                tntBlock.onCaughtFire(blockState, level, blockPos, null, null);
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 0);
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
                level.playSound(player, blockPos, SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
            } else if (block == Blocks.REDSTONE_LAMP) {
                level.setBlock(blockPos, blockState.cycle(LIT), 2);
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
                level.playSound(player, blockPos, SoundInit.SONIC_SOUND.get(), SoundSource.PLAYERS, 1, random);
            } else if (block == Blocks.JUKEBOX) {
                JukeboxBlock jukeboxBlock = (JukeboxBlock) block;
                jukeboxBlock.setRecord(player, level, blockPos, blockState, new ItemStack(ItemInit.DWHO_THEME.get()));
                level.levelEvent((Player)null, 1010, blockPos, Registry.ITEM.getId(ItemInit.DWHO_THEME.get()));
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
            }
        } else {
            if (mode == 1) {
                ModMessages.sendToServer(new BasicSonicC2SPacket());
            }
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
