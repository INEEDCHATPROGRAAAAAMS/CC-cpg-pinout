package com.breakdown.pinout.content.pinout;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class PinoutPeripheral implements IPeripheral {
    private final PinoutBlockEntity blockEntity;

    public PinoutPeripheral(PinoutBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() {
        return "pinout";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof PinoutPeripheral peripheral
                && peripheral.blockEntity == this.blockEntity;
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Boolean> pinsConnected() {
        return blockEntity.getConnectedPins();
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Double> pinsVoltage() {
        return blockEntity.getVoltagesRelativeTo(9);
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Double> pinsVoltage(int referencePin) throws LuaException {
        validateLuaPin(referencePin);
        return blockEntity.getVoltagesRelativeTo(referencePin);
    }

    @LuaFunction(mainThread = true)
    public final double comparePin(IArguments args) throws LuaException {
        int pin = args.getInt(0);
        int referencePin = args.count() >= 2 ? args.getInt(1) : 9;

        validateLuaPin(pin);
        validateLuaPin(referencePin);

        return blockEntity.comparePin(pin, referencePin);
    }

    @LuaFunction(mainThread = true)
    public final void connectPin(IArguments args) throws LuaException {
        forEachPinArgument(args, true);
    }

    @LuaFunction(mainThread = true)
    public final void disconnectPin(IArguments args) throws LuaException {
        forEachPinArgument(args, false);
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Object> pinLayout() {
        Map<Integer, Object> layout = new LinkedHashMap<>();

        layout.put(1, new Object[] { null, 9, null });
        layout.put(2, new Object[] { 1, null, 5 });
        layout.put(3, new Object[] { 2, null, 6 });
        layout.put(4, new Object[] { 3, null, 7 });
        layout.put(5, new Object[] { 4, null, 8 });

        return layout;
    }

    private void forEachPinArgument(IArguments args, boolean connect) throws LuaException {
        Object value = args.get(0);

        if (value instanceof Number number) {
            setPin(number.intValue(), connect);
            return;
        }

        if (value instanceof Map<?, ?> table) {
            for (Object tableValue : table.values()) {
                if (!(tableValue instanceof Number number)) {
                    throw new LuaException("Expected pin number in table");
                }

                setPin(number.intValue(), connect);
            }

            return;
        }

        throw new LuaException("Expected pin number or table of pin numbers");
    }

    private void setPin(int pin, boolean connect) throws LuaException {
        validateLuaControlledPin(pin);

        if (connect) {
            blockEntity.connectPin(pin);
        } else {
            blockEntity.disconnectPin(pin);
        }
    }

    private static void validateLuaPin(int pin) throws LuaException {
        if (pin < 1 || pin > 9) {
            throw new LuaException("Pin must be between 1 and 9");
        }
    }

    private static void validateLuaControlledPin(int pin) throws LuaException {
        if (pin < 1 || pin > 8) {
            throw new LuaException("Only pins 1-8 can be connected or disconnected");
        }
    }
}