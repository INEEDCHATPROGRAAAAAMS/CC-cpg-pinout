package com.breakdown.pinout.content.pinout;

import com.breakdown.pinout.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.collections.ModdedBlocks;
import org.patryk3211.powergrid.config.ResistanceValues;
import org.patryk3211.powergrid.config.ThermalValues;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class PinoutBlockEntity extends ElectricBlockEntity {
    private IElectricNode[] nodes;
    private SwitchedWire[] wires;
    private boolean[] connected;

    public PinoutBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PINOUT.get(), pos, state);
    }

    private void ensureConnectedArray() {
        if (connected == null) {
            connected = new boolean[8];
        }
    }

    private float pinContactResistance() {
        return ResistanceValues.get(ModdedBlocks.PUNCH_CARD_READER.get());
    }

    @Override
    public ThermalBehaviour specifyThermalBehaviour() {
        var block = ModdedBlocks.PUNCH_CARD_READER.get();

        return ThermalBehaviour.always(
                this,
                ThermalValues.getMass(block),
                ThermalBehaviour.dissipationFactor(
                        ThermalValues.getPower(block),
                        150f
                ),
                175f
        );
    }

    @Override
    public void electricalTick() {
        super.electricalTick();

        if (wires == null) {
            return;
        }

        for (int i = 0; i < 8; ++i) {
            applyPower(wires[i]);
        }
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        ensureConnectedArray();

        builder.setTerminalCount(9);

        nodes = new IElectricNode[9];
        wires = new SwitchedWire[8];

        for (int i = 0; i < 9; i++) {
            nodes[i] = builder.terminalNode(i);
        }

        IElectricNode common = nodes[0];

        for (int i = 0; i < 8; ++i) {
            wires[i] = builder.connectSwitch(
                    pinContactResistance(),
                    common,
                    nodes[i + 1],
                    connected[i]
            );
        }
    }

    public boolean isPinConnected(int pin) {
        ensureConnectedArray();
        validatePin(pin);

        if (pin == 9) {
            return true;
        }

        return connected[pin - 1];
    }

    public Map<Integer, Boolean> getConnectedPins() {
        Map<Integer, Boolean> result = new LinkedHashMap<>();

        for (int pin = 1; pin <= 9; pin++) {
            result.put(pin, isPinConnected(pin));
        }

        return result;
    }

    public void connectPin(int pin) {
        setPinConnected(pin, true);
    }

    public void disconnectPin(int pin) {
        setPinConnected(pin, false);
    }

    public void setPinConnected(int pin, boolean state) {
        ensureConnectedArray();
        validateControlledPin(pin);

        int index = pin - 1;

        if (connected[index] == state) {
            return;
        }

        connected[index] = state;

        if (wires != null && wires[index] != null) {
            wires[index].setState(state);
        }

        markPinStateChanged();
    }

    /**
     * Set multiple pins in one operation.
     *
     * Only pins included in the map are changed.
     * Pins not included are left as-is.
     *
     * This is useful for CC:Tweaked code which needs several pins to change
     * during one peripheral call instead of doing disconnect/connect cycles.
     */
    public void setPinsConnected(Map<Integer, Boolean> states) {
        ensureConnectedArray();

        boolean changed = false;

        for (var entry : states.entrySet()) {
            int pin = entry.getKey();
            boolean state = entry.getValue();

            validateControlledPin(pin);

            int index = pin - 1;

            if (connected[index] != state) {
                connected[index] = state;
                changed = true;
            }
        }

        if (!changed) {
            return;
        }

        applyAllWireStates();
        markPinStateChanged();
    }

    /**
     * Set all controllable pins 1-8 at once using a byte.
     *
     * bit 0 -> pin 1
     * bit 1 -> pin 2
     * bit 2 -> pin 3
     * bit 3 -> pin 4
     * bit 4 -> pin 5
     * bit 5 -> pin 6
     * bit 6 -> pin 7
     * bit 7 -> pin 8
     */
    public void setByte(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Byte value must be between 0 and 255");
        }

        ensureConnectedArray();

        boolean changed = false;

        for (int pin = 1; pin <= 8; pin++) {
            int bitIndex = pin - 1;
            boolean state = ((value >> bitIndex) & 1) == 1;
            int index = pin - 1;

            if (connected[index] != state) {
                connected[index] = state;
                changed = true;
            }
        }

        if (!changed) {
            return;
        }

        applyAllWireStates();
        markPinStateChanged();
    }

    private void applyAllWireStates() {
        if (wires == null) {
            return;
        }

        for (int i = 0; i < 8; i++) {
            if (wires[i] != null) {
                wires[i].setState(connected[i]);
            }
        }
    }

    private void markPinStateChanged() {
        setChanged();

        if (level != null && !level.isClientSide) {
            notifyUpdate();
        }
    }

    public double getVoltage(int pin) {
        validatePin(pin);

        if (nodes == null) {
            return 0.0;
        }

        return nodeForPin(pin).getVoltage();
    }

    public double getCurrent(int pin) {
        validatePin(pin);
        if (nodes == null) {
            return 0.0;
        }

        return nodeForPin(pin).getCurrent();
    }

    public Map<Integer, Double> getCurrents() {
        Map<Integer, Double> result = new LinkedHashMap<>();

        for (int pin = 1; pin <= 9; pin++) {
            result.put(pin, getCurrent(pin));
        }

        return result;
    }

    public double comparePin(int pin) {
        return comparePin(pin, 9);
    }

    public double comparePin(int pin, int referencePin) {
        validatePin(pin);
        validatePin(referencePin);

        return getVoltage(pin) - getVoltage(referencePin);
    }

    public Map<Integer, Double> getVoltagesRelativeTo(int referencePin) {
        validatePin(referencePin);

        Map<Integer, Double> result = new LinkedHashMap<>();

        for (int pin = 1; pin <= 9; pin++) {
            result.put(pin, comparePin(pin, referencePin));
        }

        return result;
    }

    private IElectricNode nodeForPin(int pin) {
        validatePin(pin);

        if (pin == 9) {
            return nodes[0];
        }

        return nodes[pin];
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        ensureConnectedArray();

        for (int i = 0; i < 8; i++) {
            connected[i] = tag.getBoolean("Pin" + (i + 1));
        }

        applyAllWireStates();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        ensureConnectedArray();

        for (int i = 0; i < 8; i++) {
            tag.putBoolean("Pin" + (i + 1), connected[i]);
        }
    }

    public static void validatePin(int pin) {
        if (pin < 1 || pin > 9) {
            throw new IllegalArgumentException("Pin must be between 1 and 9");
        }
    }

    public static void validateControlledPin(int pin) {
        if (pin < 1 || pin > 8) {
            throw new IllegalArgumentException("Only pins 1-8 can be connected or disconnected");
        }
    }
}