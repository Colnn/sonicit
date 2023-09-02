package dev.coln.sonicit.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.init.ItemInit;
import dev.coln.sonicit.networking.ModMessages;
import dev.coln.sonicit.networking.packet.customizer.CustomizeSonicC2SPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SonicCustomizerScreen extends AbstractContainerScreen<SonicCustomizerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SonicIt.MOD_ID, "textures/gui/sonic_customizer_gui.png");
    public SonicCustomizerScreen(SonicCustomizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    protected int imageHeight = 178;

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 2;
        createCustomizerScreen();
    }

    private void createCustomizerScreen() {
        //TODO: Show item model on button
        this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 20, 20, Component.literal("10"), (p_96335_) -> {
            ModMessages.sendToServer(new CustomizeSonicC2SPacket(new ItemStack(ItemInit.TEN_SCREWDRIVER.get()), this.menu.blockEntity.getBlockPos()));
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 82, this.height / 4 + 48 + -16, 20, 20, Component.literal("11"), (p_96335_) -> {
            ModMessages.sendToServer(new CustomizeSonicC2SPacket(new ItemStack(ItemInit.ELEVEN_SCREWDRIVER.get()), this.menu.blockEntity.getBlockPos()));
        }));
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
}
