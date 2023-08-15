package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.items.ExtendedSonicScrewdriverItem;
import dev.coln.sonicit.items.SonicScrewdriverItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SonicIt.MOD_ID);

    public static final RegistryObject<Item> TEN_SCREWDRIVER = ITEMS.register("10_screwdriver",
            () -> new SonicScrewdriverItem(new Item.Properties().stacksTo(1).tab(ModCreativeTab.instance)));
    public static final RegistryObject<Item> TEN_SCREWDRIVER_EXTENDED = ITEMS.register("10_screwdriver_extended",
            () -> new ExtendedSonicScrewdriverItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ELEVEN_SCREWDRIVER = ITEMS.register("11_screwdriver",
            () -> new SonicScrewdriverItem(new Item.Properties().stacksTo(1).tab(ModCreativeTab.instance)));
    public static final RegistryObject<Item> ELEVEN_SCREWDRIVER_EXTENDED = ITEMS.register("11_screwdriver_extended",
            () -> new ExtendedSonicScrewdriverItem(new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> DWHO_THEME = ITEMS.register("doctor_who_theme",
            () -> new RecordItem(8, SoundInit.DWHO_THEME, new Item.Properties().stacksTo(1), 3880));

    public static class ModCreativeTab extends CreativeModeTab {
        private ModCreativeTab(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TEN_SCREWDRIVER_EXTENDED.get());
        }

        public static final ModCreativeTab instance = new ModCreativeTab(CreativeModeTab.TABS.length, "sonicit");
    }
}
