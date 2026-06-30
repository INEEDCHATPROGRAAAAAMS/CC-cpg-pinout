package com.breakdown.pinout.content.pinout;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.patryk3211.powergrid.collections.ModdedBlocks;
import org.patryk3211.powergrid.config.ResistanceValues;

import java.util.List;

public class PinoutBlockItem extends BlockItem {
    public PinoutBlockItem(PinoutBlock block, Item.Properties properties) {
        super(block, properties);
    }

    private static float pinContactResistance() {
        return ResistanceValues.get(ModdedBlocks.PUNCH_CARD_READER.get());
    }

    private static String formatResistance(float value) {
        if (value < 1.0f) {
            return String.format("%.3f mΩ", value * 1000.0f);
        } else if (value < 1000.0f) {
            return String.format("%.3f Ω", value);
        } else if (value < 1000000.0f) {
            return String.format("%.3f kΩ", value / 1000.0f);
        } else {
            return String.format("%.3f MΩ", value / 1000000.0f);
        }
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.literal("Hold ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Shift")
                            .withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" for electrical properties")
                            .withStyle(ChatFormatting.DARK_GRAY)));
            return;
        }

        tooltip.add(Component.literal("Electrical Properties")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal(" Contact resistance: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(formatResistance(pinContactResistance()))
                        .withStyle(ChatFormatting.DARK_AQUA)));
    }
}