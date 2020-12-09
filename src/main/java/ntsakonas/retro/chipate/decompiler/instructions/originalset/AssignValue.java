package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.decompiler.instructions.OpcodeUtil;

public class AssignValue extends BaseChipInstruction {

    public static Assign Constant = (lsb, msb) -> String.format("V%d = %02X", OpcodeUtil.nibbles(lsb)[1], msb);
    public static Assign ConstantToI = (lsb, msb) -> String.format("I = %04X", OpcodeUtil.addressFrom(lsb, msb));
    public static Assign AddConstantToI = (lsb, msb) -> String.format("I += V%d", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign DisplayPatternToI = (lsb, msb) -> String.format("I = 5byte LSDP(V%d)", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign DecimalEquivalentToI = (lsb, msb) -> String.format("MI = 3digitDec(V%d)", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign StoreToI = (lsb, msb) -> String.format("MI = V0:V%d", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign LoadFromI = (lsb, msb) -> String.format("V0:V%d = MI", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign Register = (lsb, msb) -> String.format("V%d = V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterOr = (lsb, msb) -> String.format("V%d |= V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterAnd = (lsb, msb) -> String.format("V%d &= V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterXor = (lsb, msb) -> String.format("V%d ^= V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterShiftRight = (lsb, msb) -> String.format("V%d = V%d >> 1", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterShiftLeft = (lsb, msb) -> String.format("V%d = V%d << 1", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterAddition = (lsb, msb) -> String.format("V%d += V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterSubtract = (lsb, msb) -> String.format("V%d -= V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Assign RegisterSubtractReverse = (lsb, msb) -> String.format("V%d = V%d - V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0], OpcodeUtil.nibbles(lsb)[1]);
    public static Assign Random = (lsb, msb) -> String.format("V%d = rand(%02X)", OpcodeUtil.nibbles(lsb)[1], msb);
    public static Assign TimerGet = (lsb, msb) -> String.format("V%d = timer()", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign TimerSet = (lsb, msb) -> String.format("timer() = V%d", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign ToneSet = (lsb, msb) -> String.format("tone() = V%d", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign Keyboard = (lsb, msb) -> String.format("V%d = hexkey()", OpcodeUtil.nibbles(lsb)[1]);
    public static Assign RegisterPlusConst = (lsb, msb) -> String.format("V%d += %02X", OpcodeUtil.nibbles(lsb)[1], msb);

    public AssignValue(int address, byte lsb, byte msb, Assign assign) {
        super(address, lsb, msb, assign.format(lsb, msb));
    }

    public static boolean validClass8Assignment(int variant) {
        return getClass8Assignment(variant) != null;
    }

    public static Assign getClass8Assignment(int variant) {
        if (variant == 0)
            return AssignValue.Register;
        else if (variant == 1)
            return AssignValue.RegisterOr;
        else if (variant == 2)
            return AssignValue.RegisterAnd;
        else if (variant == 3)
            return AssignValue.RegisterXor;
        else if (variant == 4)
            return AssignValue.RegisterAddition;
        else if (variant == 5)
            return AssignValue.RegisterSubtract;
        else if (variant == 6)
            return AssignValue.RegisterShiftRight;
        else if (variant == 7)
            return AssignValue.RegisterSubtractReverse;
        else if (variant == 0x0E)
            return AssignValue.RegisterShiftLeft;
        return null;
    }

    public static boolean validClassFAssignment(int variant) {
        return getClassFAssignment(variant) != null;
    }

    public static Assign getClassFAssignment(int variant) {
        if (variant == 7)
            return AssignValue.TimerGet;
        else if (variant == 0x0A)
            return AssignValue.Keyboard;
        else if (variant == 0x15)
            return AssignValue.TimerSet;
        else if (variant == 0x18)
            return AssignValue.ToneSet;
        else if (variant == 0x1E)
            return AssignValue.AddConstantToI;
        else if (variant == 0x29)
            return AssignValue.DisplayPatternToI;
        else if (variant == 0x33)
            return AssignValue.DecimalEquivalentToI;
        else if (variant == 0x55)
            return AssignValue.StoreToI;
        else if (variant == 0x65)
            return AssignValue.LoadFromI;

        return null;
    }

    public interface Assign {
        String format(byte lsb, byte msb);
    }
}
