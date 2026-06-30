package com.breakdown.pinout.content.pinout;

import com.breakdown.pinout.registry.ModBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.HorizontalElectricBlock;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;
import org.patryk3211.powergrid.electricity.info.Resistance;
import org.patryk3211.powergrid.utility.Lang;

import java.util.List;

@MethodsReturnNonnullByDefault
public class PinoutBlock extends HorizontalElectricBlock implements IBE<PinoutBlockEntity>, IHaveElectricProperties {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static Component bit(int i) {
        return Lang.builder()
                .text(Integer.toString(i))
                .style(ChatFormatting.DARK_GREEN)
                .component();
    }

    /*
     * Physical pin layout:
     *
     *   x 9 x
     *   1 x 5
     *   2 x 6
     *   3 x 7
     *   4 x 8
     *
     * Terminal index mapping:
     *   terminal 0 = pin 9/common
     *   terminal 1 = pin 1
     *   terminal 2 = pin 2
     *   terminal 3 = pin 3
     *   terminal 4 = pin 4
     *   terminal 5 = pin 5
     *   terminal 6 = pin 6
     *   terminal 7 = pin 7
     *   terminal 8 = pin 8
     */
    private static final TerminalBoundingBox[] TERMINALS = new TerminalBoundingBox[] {
            new TerminalBoundingBox(IDecoratedTerminal.COMMON, 7, 12, 11, 9, 14, 13),

            // Left bank: pins 1-4, top to bottom
            new TerminalBoundingBox(bit(1), 4, 11.5, 12, 6, 13.5, 13.5),
            new TerminalBoundingBox(bit(2), 4, 8.75, 13, 6, 10.75, 14.5),
            new TerminalBoundingBox(bit(3), 4, 6, 14, 6, 8, 15.5),
            new TerminalBoundingBox(bit(4), 4, 3, 15, 6, 5, 16.5),

            // Right bank: pins 5-8, top to bottom
            new TerminalBoundingBox(bit(5), 10, 11.5, 12, 12, 13.5, 13.5),
            new TerminalBoundingBox(bit(6), 10, 8.75, 13, 12, 10.75, 14.5),
            new TerminalBoundingBox(bit(7), 10, 6, 14, 12, 8, 15.5),
            new TerminalBoundingBox(bit(8), 10, 3, 15, 12, 5, 16.5),
    };

    private static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 17, 12),
            box(0, 0, 12, 16, 5, 16)
    );

    public PinoutBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, net.minecraft.core.Direction.NORTH));

        setTerminalCollection(
                HorizontalElectricBlock.horizontalNorthTerminals(this, TERMINALS, SHAPE)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var playerFacing = ctx.getPlayer() == null || !ctx.getPlayer().isShiftKeyDown()
                ? ctx.getHorizontalDirection().getOpposite()
                : ctx.getHorizontalDirection();

        return defaultBlockState().setValue(HORIZONTAL_FACING, playerFacing);
    }

    @Override
    public Class<PinoutBlockEntity> getBlockEntityClass() {
        return PinoutBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PinoutBlockEntity> getBlockEntityType() {
        return ModBlockEntities.PINOUT.get();
    }

    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        Resistance.switchResistance(resistance(), player, tooltip);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }
}