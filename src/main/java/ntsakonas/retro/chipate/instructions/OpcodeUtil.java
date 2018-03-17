package ntsakonas.retro.chipate.instructions;

public class OpcodeUtil
{
    public static int[] nibbles(byte b)
    {
        return new int[]{ (Byte.toUnsignedInt(b)>>4)&0x0f, Byte.toUnsignedInt(b) & 0x0f};
    }

    public static int addressFrom(byte lsb, byte msb)
    {
        return (Byte.toUnsignedInt(lsb) & 0x0f)* 256 + Byte.toUnsignedInt(msb);
    }

    public static byte[] addressToBytes(int address)
    {
        byte lsb = (byte) ((address >>8) & 0xff);
        byte msb = (byte) (address & 0xff);
        return new byte[]{lsb,msb};
    }
}
