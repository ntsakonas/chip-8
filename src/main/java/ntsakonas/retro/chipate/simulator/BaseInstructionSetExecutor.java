package ntsakonas.retro.chipate.simulator;

public class BaseInstructionSetExecutor implements ChipInstructionExecutor {

    @Override
    public void executeCode(ChipInstructionMicrocode microCode, byte lsb, byte msb, Chip8System.SystemState systemState) {
        microCode.execute(lsb, msb, systemState);
    }
}
