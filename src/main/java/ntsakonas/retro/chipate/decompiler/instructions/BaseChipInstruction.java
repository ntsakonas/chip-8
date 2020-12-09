package ntsakonas.retro.chipate.decompiler.instructions;

public class BaseChipInstruction implements ChipInstruction {

    protected int address;
    protected byte lsb;
    protected byte msb;
    protected String mnemonic;

    public BaseChipInstruction(int address, byte lsb, byte msb, String mnemonic) {
        this.address = address;
        this.lsb = lsb;
        this.msb = msb;
        this.mnemonic = mnemonic;
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public String getOpcodes() {
        return String.format("%02x%02X", lsb, msb);
    }
}
