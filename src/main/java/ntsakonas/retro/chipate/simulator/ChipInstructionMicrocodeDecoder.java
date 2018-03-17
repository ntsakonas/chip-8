package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.instructions.OpcodeUtil;

abstract class ChipInstructionMicrocodeDecoder
{

    public abstract ChipInstructionMicrocode decode(byte lsb, byte msb);

    public static ChipInstructionMicrocodeDecoder decoder()
    {
        return new BaseInstructionSetDecoder();
    }

    private static class BaseInstructionSetDecoder extends ChipInstructionMicrocodeDecoder
    {
        ChipInstructionMicrocode[] microcodeHanlders;

        BaseInstructionSetDecoder()
        {
            initialiseMicroCodeHanlder();
        }

        private void initialiseMicroCodeHanlder()
        {
            microcodeHanlders = new ChipInstructionMicrocode[]
                    {
                       null,
                       null,
                       null,
                       null,
                       null,
                       null,
                       null,
                       null,
                       Class8Microcode
                    };
        }

        private ChipInstructionMicrocode Class8Microcode = (lsb, msb, s) ->
        {
            int[] lsbNibbles = OpcodeUtil.nibbles(lsb);
            int[] msbNibbles = OpcodeUtil.nibbles(msb);
            int Y = msbNibbles[0];
            int X = lsbNibbles[1];
            int operation = msbNibbles[1];
            if (operation == 0)
            {
                s.setRegister(X, s.getRegister(Y));
            } else if (operation == 1)
            {
                //TODO:: find how VF is changed
                s.setRegister(X, (byte) (Byte.toUnsignedInt(s.getRegister(X)) / Byte.toUnsignedInt(s.getRegister(Y))));
            } else if (operation == 2)
            {
                //TODO:: find how VF is changed
                s.setRegister(X, (byte) (Byte.toUnsignedInt(s.getRegister(X)) & Byte.toUnsignedInt(s.getRegister(Y))));
            } else if (operation == 4)
            {
                int sum = (Byte.toUnsignedInt(s.getRegister(X)) + Byte.toUnsignedInt(s.getRegister(Y)));
                s.setRegister(X, (byte) (sum & 0xFF));
                s.setRegister(0x0F, sum > 0xff ? (byte) 1 : (byte) 0);
            } else if (operation == 5)
            {
                byte x_value = s.getRegister(X);
                byte y_value = s.getRegister(Y);
                int diff = (Byte.toUnsignedInt(x_value) - Byte.toUnsignedInt(y_value));
                s.setRegister(X, (byte) (diff & 0xFF));
                s.setRegister(0x0F, x_value < y_value ? (byte) 0 : (byte) 1);
            }
            s.setProgramCounter(s.getProgramCounter() + 2);
        };


        public ChipInstructionMicrocode decode(byte lsb, byte msb)
        {
            int[] lsbNibbles = OpcodeUtil.nibbles(lsb);
            return microcodeHanlders[lsbNibbles[0]];
        }

    }
}
