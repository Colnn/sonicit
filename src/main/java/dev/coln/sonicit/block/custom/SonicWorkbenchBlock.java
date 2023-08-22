package dev.coln.sonicit.block.custom;

import dev.coln.sonicit.block.entity.SonicWorkbenchBlockEntity;
import dev.coln.sonicit.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

public class SonicWorkbenchBlock extends BaseEntityBlock {
    public SonicWorkbenchBlock(Properties properties) { super(properties); }

    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean isMoving) {
        if(blockState.getBlock() != blockState1.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if(blockEntity instanceof SonicWorkbenchBlockEntity) {
                ((SonicWorkbenchBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(blockState, level, blockPos, blockState1, isMoving);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(blockPos);
            if(entity instanceof SonicWorkbenchBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (SonicWorkbenchBlockEntity) entity, blockPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SonicWorkbenchBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityInit.SONIC_WORKBENCH.get(),
                SonicWorkbenchBlockEntity::tick);
    }
}
