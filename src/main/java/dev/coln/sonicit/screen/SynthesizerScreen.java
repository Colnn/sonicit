package dev.coln.sonicit.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.screen.renderer.FluidTankRenderer;
import dev.coln.sonicit.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

public class SynthesizerScreen extends AbstractContainerScreen<SynthesizerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SonicIt.MOD_ID, "textures/gui/synthesizer_gui.png");
    private FluidTankRenderer renderer;
    public SynthesizerScreen(SynthesizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    protected int imageHeight = 178;

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 2;
        assignFluidRenderer();
    }

    private void assignFluidRenderer() {
        renderer = new FluidTankRenderer(64000, true, 15, 48);
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
        renderer.render(stack, x + 37, y + 15, menu.getFluidStack());
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderFluidAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 55, 15)) {
            renderTooltip(pPoseStack, renderer.getTooltip(menu.getFluidStack(), TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderProgressArrow(PoseStack stack, int x, int y) {
        if(menu.isCrafting()) {
            blit(stack, x + 99, y + 33, 193, 33, 6, menu.getScaledProgress());
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

}
