package dev.coln.sonicit.integration;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.init.BlockInit;
import dev.coln.sonicit.recipe.SynthesizerRecipe;
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
import net.minecraft.world.item.Items;

public class SynthesizerRecipeCategory implements IRecipeCategory<SynthesizerRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(SonicIt.MOD_ID, "synthesizer");
    public final static ResourceLocation TEXTURE = new ResourceLocation(SonicIt.MOD_ID, "textures/gui/synthesizer_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SynthesizerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 90);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.SYNTHESIZER.get()));
    }

    @Override
    public RecipeType<SynthesizerRecipe> getRecipeType() {
        return JEISonicItPlugin.SYNTHESIZER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Synthesizer");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SynthesizerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 9, 20).addItemStack(new ItemStack(Items.LAVA_BUCKET));
        builder.addSlot(RecipeIngredientRole.INPUT, 93, 11).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 60).addItemStack(recipe.getResultItem());
    }
}
