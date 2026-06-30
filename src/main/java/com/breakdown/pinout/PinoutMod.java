package com.breakdown.pinout;

import com.breakdown.pinout.registry.ModBlockEntities;
import com.breakdown.pinout.registry.ModBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import com.breakdown.pinout.compat.cc.PinoutPeripheralProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(PinoutMod.MOD_ID)
public class PinoutMod {
    public static final String MOD_ID = "pinout";

    public PinoutMod(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(PinoutPeripheralProvider::register);
    }
}