package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.recipe.SynthesizerRecipe;
import dev.coln.sonicit.recipe.SonicWorkbenchRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SonicIt.MOD_ID);


    public static final RegistryObject<RecipeSerializer<SonicWorkbenchRecipe>> SONIC_WORKBENCH_SERIALIZER =
            SERIALIZERS.register("sonic_workbench", () -> SonicWorkbenchRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SynthesizerRecipe>> SYNTHESIZER_SERIALIZER =
            SERIALIZERS.register("synthesizer", () -> SynthesizerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
