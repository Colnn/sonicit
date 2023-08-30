package dev.coln.sonicit.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.coln.sonicit.SonicIt;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MetalizerScreen extends AbstractContainerScreen<MetalizerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SonicIt.MOD_ID, "textures/gui/metalizer_gui.png");
    public MetalizerScreen(MetalizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    protected int imageHeight = 178;

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 2;
    }

    @Override
    protected void renderBg(PoseStack stack, float PartialTick, int MouseX, int MouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(stack, x, y);
        renderLavaIndicator(stack, x, y);
    }

    private void renderProgressArrow(PoseStack stack, int x, int y) {
        if(menu.isCrafting()) {
            blit(stack, x + 99, y + 33, 193, 33, 6, menu.getScaledProgress());
        }
    }

    private void renderLavaIndicator(PoseStack stack, int x, int y) {
        blit(stack, x + 37, y + 15, 175, 15, 15, menu.getScaledLava());
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
    }
}
