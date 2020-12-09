package ntsakonas.retro.chipate.decompiler.instructions;

class UnknownInstruction extends BaseChipInstruction {

    public UnknownInstruction(int address, byte lsb, byte msb) {
        super(address, lsb, msb, String.format("[%02x %02x] data or invalid opcode", lsb, msb));
    }
}
