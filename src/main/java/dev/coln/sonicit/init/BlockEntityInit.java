package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.block.entity.SynthesizerBlockEntity;
import dev.coln.sonicit.block.entity.SonicCustomizerBlockEntity;
import dev.coln.sonicit.block.entity.SonicWorkbenchBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SonicIt.MOD_ID);

    public static final RegistryObject<BlockEntityType<SonicWorkbenchBlockEntity>> SONIC_WORKBENCH =
            BLOCK_ENTITIES.register("sonic_workbench", () ->
                    BlockEntityType.Builder.of(SonicWorkbenchBlockEntity::new,
                            BlockInit.SONIC_WORKBENCH.get()).build(null));
    public static final RegistryObject<BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER =
            BLOCK_ENTITIES.register("synthesizer", () ->
                    BlockEntityType.Builder.of(SynthesizerBlockEntity::new,
                            BlockInit.SYNTHESIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SonicCustomizerBlockEntity>> SONIC_CUSTOMIZER =
            BLOCK_ENTITIES.register("sonic_customizer", () ->
                    BlockEntityType.Builder.of(SonicCustomizerBlockEntity::new,
                            BlockInit.SONIC_CUSTOMIZER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
