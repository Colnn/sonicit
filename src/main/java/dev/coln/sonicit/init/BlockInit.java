package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.block.custom.SynthesizerBlock;
import dev.coln.sonicit.block.custom.SonicCustomizerBlock;
import dev.coln.sonicit.block.custom.SonicWorkbenchBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SonicIt.MOD_ID);

    public static final RegistryObject<Block> SONIC_WORKBENCH = registerBlock("sonic_workbench",
            () -> new SonicWorkbenchBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f).requiresCorrectToolForDrops().noOcclusion()), SonicIt.ModCreativeTab.instance);

    public static final RegistryObject<Block> SYNTHESIZER = registerBlock("synthesizer",
            () -> new SynthesizerBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f).requiresCorrectToolForDrops().noOcclusion()), SonicIt.ModCreativeTab.instance);

    public static final RegistryObject<Block> SONIC_CUSTOMIZER = registerBlock("sonic_customizer",
            () -> new SonicCustomizerBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f).requiresCorrectToolForDrops().noOcclusion()), SonicIt.ModCreativeTab.instance);

    public static final RegistryObject<Block> LIVING_METAL_ORE = registerBlock("living_metal_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(5f).requiresCorrectToolForDrops().noOcclusion()), SonicIt.ModCreativeTab.instance);

    public static final RegistryObject<Block> DEEPSLATE_LIVING_METAL_ORE = registerBlock("deepslate_living_metal_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(7f).requiresCorrectToolForDrops().noOcclusion()), SonicIt.ModCreativeTab.instance);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
