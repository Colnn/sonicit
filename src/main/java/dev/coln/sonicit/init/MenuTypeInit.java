package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.screen.SynthesizerMenu;
import dev.coln.sonicit.screen.SonicCustomizerMenu;
import dev.coln.sonicit.screen.SonicWorkbenchMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypeInit {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SonicIt.MOD_ID);

    public static final RegistryObject<MenuType<SonicWorkbenchMenu>> SONIC_WORKBENCH_MENU =
            registerMenuType(SonicWorkbenchMenu::new, "sonic_workbench_menu");

    public static final RegistryObject<MenuType<SynthesizerMenu>> SYNTHESIZER_MENU =
            registerMenuType(SynthesizerMenu::new, "synthesizer_menu");

    public static final RegistryObject<MenuType<SonicCustomizerMenu>> SONIC_CUSTOMIZER_MENU =
            registerMenuType(SonicCustomizerMenu::new, "sonic_customizer_menu");

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                 String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
