package dev.coln.sonicit.integration;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.init.BlockInit;
import dev.coln.sonicit.recipe.SonicWorkbenchRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SonicWorkbenchRecipeCategory implements IRecipeCategory<SonicWorkbenchRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(SonicIt.MOD_ID, "sonic_workbench");
    public final static ResourceLocation TEXTURE = new ResourceLocation(SonicIt.MOD_ID, "textures/gui/sonic_workbench_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SonicWorkbenchRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 90);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.SONIC_WORKBENCH.get()));
    }

    @Override
    public RecipeType<SonicWorkbenchRecipe> getRecipeType() {
        return JEISonicItPlugin.SONIC_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Sonic Workbench");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SonicWorkbenchRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 71, 21).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 71, 43).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 71, 65).addIngredients(recipe.getIngredients().get(2));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 43).addItemStack(recipe.getResultItem());
    }
}
