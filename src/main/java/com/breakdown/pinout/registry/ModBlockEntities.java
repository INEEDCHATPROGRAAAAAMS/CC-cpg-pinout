package com.breakdown.pinout.registry;

import com.breakdown.pinout.PinoutMod;
import com.breakdown.pinout.content.pinout.PinoutBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PinoutMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PinoutBlockEntity>> PINOUT =
            BLOCK_ENTITIES.register("pinout", () ->
                    BlockEntityType.Builder.of(
                            PinoutBlockEntity::new,
                            ModBlocks.PINOUT.get()
                    ).build(null)
            );
}