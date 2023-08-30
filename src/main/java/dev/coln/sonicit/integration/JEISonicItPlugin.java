package dev.coln.sonicit.integration;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.recipe.MetalizerRecipe;
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

    public static RecipeType<MetalizerRecipe> METALIZER_TYPE =
            new RecipeType<>(MetalizerRecipeCategory.UID, MetalizerRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SonicIt.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                SonicWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new
                MetalizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<SonicWorkbenchRecipe> recipesSonic = rm.getAllRecipesFor(SonicWorkbenchRecipe.Type.INSTANCE);
        registration.addRecipes(SONIC_TYPE, recipesSonic);
        List<MetalizerRecipe> recipesMetalizer = rm.getAllRecipesFor(MetalizerRecipe.Type.INSTANCE);
        registration.addRecipes(METALIZER_TYPE, recipesMetalizer);
    }
}
