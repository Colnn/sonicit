package dev.coln.sonicit.block.entity;

import dev.coln.sonicit.init.BlockEntityInit;
import dev.coln.sonicit.init.BlockInit;
import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.screen.SonicWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SonicWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public SonicWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.SONIC_WORKBENCH.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SonicWorkbenchBlockEntity.this.progress;
                    case 1 -> SonicWorkbenchBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> SonicWorkbenchBlockEntity.this.progress = value;
                    case 1 -> SonicWorkbenchBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Sonic Workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SonicWorkbenchMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.put("inventory", itemHandler.serializeNBT());
        compoundTag.putInt("sonic_workbench.progress", this.progress);

        super.saveAdditional(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        itemHandler.deserializeNBT(compoundTag.getCompound("inventory"));
        progress = serializeNBT().getInt("sonic_workbench.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SonicWorkbenchBlockEntity blockEntity) {
        if(level.isClientSide()) {
            return;
        }

        if(hasRecipe(blockEntity)) {
            blockEntity.progress++;
            setChanged(level, blockPos, blockState);

            if(blockEntity.progress >= blockEntity.maxProgress) {
                craftItem(blockEntity);
            }
        } else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, blockState);
        }

    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(SonicWorkbenchBlockEntity blockEntity) {
        if(hasRecipe(blockEntity)) {
            blockEntity.itemHandler.extractItem(1, 1, false);
            blockEntity.itemHandler.setStackInSlot(2, new ItemStack(ItemInit.TEN_SCREWDRIVER.get(),
                    blockEntity.itemHandler.getStackInSlot(2).getCount() + 1));

            blockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(SonicWorkbenchBlockEntity blockEntity) {
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        boolean hasSonicEmitterInFirstSlot = blockEntity.itemHandler.getStackInSlot(1).getItem() == ItemInit.SONIC_EMITTER.get();

        return hasSonicEmitterInFirstSlot && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, new ItemStack(ItemInit.TEN_SCREWDRIVER.get(), 1));
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(2).getItem() == itemStack.getItem() || inventory.getItem(2).isEmpty();
    }
}
