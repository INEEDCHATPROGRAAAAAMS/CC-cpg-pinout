package com.breakdown.pinout.registry;

import com.breakdown.pinout.PinoutMod;
import com.breakdown.pinout.content.pinout.PinoutBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(PinoutMod.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PinoutMod.MOD_ID);

    public static final DeferredBlock<Block> PINOUT = BLOCKS.register(
            "pinout",
            () -> new PinoutBlock(
                    BlockBehaviour.Properties.of()
                            .strength(1.5f, 6.0f)
                            .sound(SoundType.METAL)
            )
    );

    public static final DeferredHolder<Item, BlockItem> PINOUT_ITEM =
            ITEMS.register("pinout", () -> new BlockItem(
                    PINOUT.get(),
                    new Item.Properties()
            ));
}