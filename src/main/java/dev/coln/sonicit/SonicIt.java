package dev.coln.sonicit;

import com.mojang.logging.LogUtils;
import dev.coln.sonicit.init.*;
import dev.coln.sonicit.networking.ModMessages;
import dev.coln.sonicit.screen.MetalizerScreen;
import dev.coln.sonicit.screen.SonicCustomizerScreen;
import dev.coln.sonicit.screen.SonicWorkbenchScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SonicIt.MOD_ID)
public class SonicIt {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "sonicit";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static class ModCreativeTab extends CreativeModeTab {
        private ModCreativeTab(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.TEN_SCREWDRIVER_EXTENDED.get());
        }

        public static final ModCreativeTab instance = new ModCreativeTab(CreativeModeTab.TABS.length, "sonicit");
    }

    public SonicIt() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(modEventBus);
        SoundInit.SOUND_EVENTS.register(modEventBus);
        BlockInit.register(modEventBus);
        BlockEntityInit.register(modEventBus);
        MenuTypeInit.register(modEventBus);
        RecipeInit.register(modEventBus);
        ConfiguredFeatureInit.CONFIGURED_FEATURES.register(modEventBus);
        PlacedFeatureInit.PLACED_FEATURES.register(modEventBus);

        ModMessages.register();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code

            MenuScreens.register(MenuTypeInit.SONIC_WORKBENCH_MENU.get(), SonicWorkbenchScreen::new);
            MenuScreens.register(MenuTypeInit.METALIZER_MENU.get(), MetalizerScreen::new);
            MenuScreens.register(MenuTypeInit.SONIC_CUSTOMIZER_MENU.get(), SonicCustomizerScreen::new);
        }
    }
}
