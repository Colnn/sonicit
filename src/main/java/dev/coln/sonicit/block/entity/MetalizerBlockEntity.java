package dev.coln.sonicit.block.entity;

import dev.coln.sonicit.init.BlockEntityInit;
import dev.coln.sonicit.networking.ModMessages;
import dev.coln.sonicit.networking.packet.FluidSyncS2CPacket;
import dev.coln.sonicit.recipe.MetalizerRecipe;
import dev.coln.sonicit.screen.MetalizerMenu;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MetalizerBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
                case 1 -> true;
                case 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final FluidTank FLUID_TANK = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToAllPlayers(new FluidSyncS2CPacket(this.fluid, worldPosition));
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.LAVA;
        }
    };

    private static final int LAVA_REQ = 32;

    public void setFluid(FluidStack stack) {
        this.FLUID_TANK.setFluid(stack);
    }

    public FluidStack getFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;
    private int lava = 0;
    private int maxLava = 100;

    public MetalizerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.METALIZER.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MetalizerBlockEntity.this.progress;
                    case 1 -> MetalizerBlockEntity.this.maxProgress;
                    case 2 -> MetalizerBlockEntity.this.lava;
                    case 3 -> MetalizerBlockEntity.this.maxLava;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> MetalizerBlockEntity.this.progress = value;
                    case 1 -> MetalizerBlockEntity.this.maxProgress = value;
                    case 2 -> MetalizerBlockEntity.this.lava = value;
                    case 3 -> MetalizerBlockEntity.this.maxLava = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Metalizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MetalizerMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.put("inventory", itemHandler.serializeNBT());
        compoundTag.putInt("metalizer.progress", this.progress);
        compoundTag = FLUID_TANK.writeToNBT(compoundTag);

        super.saveAdditional(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        itemHandler.deserializeNBT(compoundTag.getCompound("inventory"));
        progress = serializeNBT().getInt("metalizer.progress");
        FLUID_TANK.readFromNBT(compoundTag);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, MetalizerBlockEntity blockEntity) {
        if(level.isClientSide()) {
            return;
        }

        if(hasFluidItemInSourceSlot(blockEntity)) {
            transferItemFluidToFluidTank(blockEntity);
        }

        if(hasRecipe(blockEntity) && hasEnoughLava(blockEntity)) {
            blockEntity.progress++;
            extractLava(blockEntity);
            setChanged(level, blockPos, blockState);

            if(blockEntity.progress >= blockEntity.maxProgress) {
                craftItem(blockEntity);
            }
        } else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, blockState);
        }

    }

    private static void transferItemFluidToFluidTank(MetalizerBlockEntity blockEntity) {
        blockEntity.itemHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            int drainAmount = Math.min(blockEntity.FLUID_TANK.getSpace(), 1000);

            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if(blockEntity.FLUID_TANK.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                fillTankWithFluid(blockEntity, stack, handler.getContainer());
            }
        });
    }

    private static void fillTankWithFluid(MetalizerBlockEntity blockEntity, FluidStack stack, ItemStack container) {
        blockEntity.FLUID_TANK.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        blockEntity.itemHandler.extractItem(0, 1, false);
        blockEntity.itemHandler.insertItem(0, container, false);
    }

    private static boolean hasFluidItemInSourceSlot(MetalizerBlockEntity blockEntity) {
        return blockEntity.itemHandler.getStackInSlot(0).getCount() > 0;
    }

    private static boolean hasEnoughLava(MetalizerBlockEntity blockEntity) {
        return blockEntity.FLUID_TANK.getFluidAmount() >= LAVA_REQ * blockEntity.maxProgress;
    }

    private static void extractLava(MetalizerBlockEntity blockEntity) {
        blockEntity.FLUID_TANK.drain(LAVA_REQ, IFluidHandler.FluidAction.EXECUTE);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(MetalizerBlockEntity blockEntity) {
        Level level = blockEntity.level;
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<MetalizerRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(MetalizerRecipe.Type.INSTANCE, inventory, level);

        if(hasRecipe(blockEntity)) {
            blockEntity.itemHandler.extractItem(1, 1, false);
            blockEntity.itemHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItem().getItem(),
                    blockEntity.itemHandler.getStackInSlot(2).getCount() + 1));

            blockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(MetalizerBlockEntity blockEntity) {
        Level level = blockEntity.level;
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<MetalizerRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(MetalizerRecipe.Type.INSTANCE, inventory, level);


        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(2).getItem() == itemStack.getItem() || inventory.getItem(2).isEmpty();
    }
}
