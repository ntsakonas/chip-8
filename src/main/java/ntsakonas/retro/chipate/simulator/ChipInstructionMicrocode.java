package ntsakonas.retro.chipate.simulator;

interface ChipInstructionMicrocode
{

    // microcode can be
    // reg -> A
    // A->reg
    // A+=C
    // A+=reg
    // I=MMMM
    // [I]=A
    // [I]=C
    void execute(byte lsb, byte msb, Chip8System.SystemState state);
}
