package ntsakonas.retro.chipate.decompiler.instructions;

public interface ChipInstruction {

    int getAddress();

    String getOpcodes();

    String getMnemonic();
}
