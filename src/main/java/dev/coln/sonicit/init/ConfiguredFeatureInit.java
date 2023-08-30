package dev.coln.sonicit.init;

import com.google.common.base.Suppliers;
import dev.coln.sonicit.SonicIt;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ConfiguredFeatureInit {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, SonicIt.MOD_ID);

    private static final Supplier<List<OreConfiguration.TargetBlockState>> LIVING_METAL_OVERWORLD_REPLACEMENT = Suppliers.memoize(() ->
            List.of(
                    OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlockInit.LIVING_METAL_ORE.get().defaultBlockState()),
                    OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BlockInit.DEEPSLATE_LIVING_METAL_ORE.get().defaultBlockState())
            )
    );

    public static final RegistryObject<ConfiguredFeature<?, ?>> LIVING_METAL_OVERWORLD_ORE = CONFIGURED_FEATURES.register("living_metal_overworld_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(LIVING_METAL_OVERWORLD_REPLACEMENT.get(), 7)));
}
