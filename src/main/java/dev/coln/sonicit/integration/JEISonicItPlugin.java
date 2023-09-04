package dev.coln.sonicit.integration;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.recipe.SynthesizerRecipe;
import dev.coln.sonicit.recipe.SonicWorkbenchRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEISonicItPlugin implements IModPlugin {
    public static RecipeType<SonicWorkbenchRecipe> SONIC_TYPE =
            new RecipeType<>(SonicWorkbenchRecipeCategory.UID, SonicWorkbenchRecipe.class);

    public static RecipeType<SynthesizerRecipe> SYNTHESIZER_TYPE =
            new RecipeType<>(SynthesizerRecipeCategory.UID, SynthesizerRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SonicIt.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                SonicWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new
                SynthesizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<SonicWorkbenchRecipe> recipesSonic = rm.getAllRecipesFor(SonicWorkbenchRecipe.Type.INSTANCE);
        registration.addRecipes(SONIC_TYPE, recipesSonic);
        List<SynthesizerRecipe> recipesSynthesizer = rm.getAllRecipesFor(SynthesizerRecipe.Type.INSTANCE);
        registration.addRecipes(SYNTHESIZER_TYPE, recipesSynthesizer);
    }
}
