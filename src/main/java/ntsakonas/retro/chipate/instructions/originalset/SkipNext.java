package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.instructions.OpcodeUtil;

public class SkipNext extends BaseChipInstruction {

    public static Condition EqualsConstant = (lsb, msb) -> String.format("Skip next if V%d == %02X", OpcodeUtil.nibbles(lsb)[1], msb);
    public static Condition NotEqualsConstant = (lsb, msb) -> String.format("Skip next if V%d != %02X", OpcodeUtil.nibbles(lsb)[1], msb);
    public static Condition EqualsRegister = (lsb, msb) -> String.format("Skip next if V%d == V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Condition NotEqualsRegister = (lsb, msb) -> String.format("Skip next if V%d != V%d", OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]);
    public static Condition EqualsKey = (lsb, msb) -> String.format("Skip next if V%d == HexKey", OpcodeUtil.nibbles(lsb)[1]);
    public static Condition NotEqualsKey = (lsb, msb) -> String.format("Skip next if V%d != HexKey", OpcodeUtil.nibbles(lsb)[1]);

    public SkipNext(int address, byte lsb, byte msb, Condition condition) {
        super(address, lsb, msb, condition.format(lsb, msb));
    }

    public interface Condition {
        String format(byte lsb, byte msb);
    }
}
