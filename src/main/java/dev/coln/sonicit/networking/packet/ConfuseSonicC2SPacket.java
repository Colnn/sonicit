package dev.coln.sonicit.networking.packet;

import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.init.SoundInit;
import dev.coln.sonicit.networking.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ConfuseSonicC2SPacket {

    public ConfuseSonicC2SPacket() {

    }
    public ConfuseSonicC2SPacket(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            int affected = 0;
            int RANGE = 5;

            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            BlockPos blockPos = player.blockPosition();
            BlockPos topCorner = blockPos.offset(RANGE, RANGE, RANGE);
            BlockPos bottomCorner = blockPos.offset(-RANGE, -RANGE, -RANGE);
            AABB box = new AABB(topCorner, bottomCorner);

            List<Entity> entities = level.getEntities(null, box);
            for (Entity target : entities){
                if (target instanceof LivingEntity){
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 5));
                    affected += 1;
                }
            }
            player.displayClientMessage(Component.literal(ChatFormatting.GREEN + "Affected " + affected + " blocks."), true);
        });
        return true;
    }
}
