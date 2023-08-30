package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class PlacedFeatureInit {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, SonicIt.MOD_ID);

    public static final RegistryObject<PlacedFeature> LIVING_METAL_OVERWORLD_ORE = PLACED_FEATURES.register("living_metal_overworld_ore",
            () -> new PlacedFeature(ConfiguredFeatureInit.LIVING_METAL_OVERWORLD_ORE.getHolder().get(),
                    commonOrePlacement(1, HeightRangePlacement.triangle(
                            VerticalAnchor.absolute(-20),
                            VerticalAnchor.absolute(40)
                    ))));

    private static List<PlacementModifier> commonOrePlacement(int countPerChunk, PlacementModifier height) {
        return orePlacement(CountPlacement.of(countPerChunk), height);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }
}
