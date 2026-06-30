package com.breakdown.pinout.registry;

import com.breakdown.pinout.PinoutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PinoutMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PINOUT_TAB =
            CREATIVE_TABS.register("pinout_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pinout"))
                    .icon(() -> ModBlocks.PINOUT_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.PINOUT_ITEM.get());
                    })
                    .build()
            );
}