package ntsakonas.retro.chipate.simulator;

public interface ChipInstructionExecutor {

    void executeCode(ChipInstructionMicrocode microCode, byte lsb, byte msb, Chip8System.SystemState systemState);
}
