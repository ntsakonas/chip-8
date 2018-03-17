package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.instructions.OpcodeUtil;

public class DoSubroutine extends BaseChipInstruction
{
    public DoSubroutine(int address, byte lsb, byte msb)
    {
        super(address,lsb,msb,String.format("CALL %04X", OpcodeUtil.addressFrom(lsb,msb)));
    }
}
