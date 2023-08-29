package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import dev.coln.sonicit.items.ExtendedSonicScrewdriverItem;
import dev.coln.sonicit.items.SonicScrewdriverItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SonicIt.MOD_ID);

    public static final RegistryObject<Item> TEN_SCREWDRIVER = ITEMS.register("10_screwdriver",
            () -> new SonicScrewdriverItem(new Item.Properties().stacksTo(1).tab(SonicIt.ModCreativeTab.instance)));
    public static final RegistryObject<Item> TEN_SCREWDRIVER_EXTENDED = ITEMS.register("10_screwdriver_extended",
            () -> new ExtendedSonicScrewdriverItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ELEVEN_SCREWDRIVER = ITEMS.register("11_screwdriver",
            () -> new SonicScrewdriverItem(new Item.Properties().stacksTo(1).tab(SonicIt.ModCreativeTab.instance)));
    public static final RegistryObject<Item> ELEVEN_SCREWDRIVER_EXTENDED = ITEMS.register("11_screwdriver_extended",
            () -> new ExtendedSonicScrewdriverItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TWELVE_SCREWDRIVER = ITEMS.register("12_screwdriver",
            () -> new ExtendedSonicScrewdriverItem(new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> SONIC_EMITTER = ITEMS.register("sonic_emitter",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SONIC_HULL = ITEMS.register("sonic_hull",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> LIVING_METAL = ITEMS.register("living_metal",
            () -> new Item(new Item.Properties().tab(SonicIt.ModCreativeTab.instance)));


    public static final RegistryObject<Item> DWHO_THEME = ITEMS.register("doctor_who_theme",
            () -> new RecordItem(8, SoundInit.DWHO_THEME, new Item.Properties().stacksTo(1), 3880));
}
