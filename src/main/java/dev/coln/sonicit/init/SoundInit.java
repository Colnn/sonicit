package dev.coln.sonicit.init;

import dev.coln.sonicit.SonicIt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SonicIt.MOD_ID);

    public static final RegistryObject<SoundEvent> SONIC_SOUND = SOUND_EVENTS.register("sonic", () -> new SoundEvent(new ResourceLocation(SonicIt.MOD_ID, "sonic")));
    public static final RegistryObject<SoundEvent> DWHO_THEME = SOUND_EVENTS.register("theme", () -> new SoundEvent(new ResourceLocation(SonicIt.MOD_ID, "theme")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
