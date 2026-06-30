package com.breakdown.pinout.compat.cc;

import com.breakdown.pinout.content.pinout.PinoutBlockEntity;
import com.breakdown.pinout.content.pinout.PinoutPeripheral;
import com.breakdown.pinout.registry.ModBlockEntities;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PinoutPeripheralProvider {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PeripheralCapability.get(),
                ModBlockEntities.PINOUT.get(),
                (blockEntity, side) -> {
                    if (blockEntity instanceof PinoutBlockEntity pinout) {
                        return new PinoutPeripheral(pinout);
                    }

                    return null;
                }
        );
    }
}