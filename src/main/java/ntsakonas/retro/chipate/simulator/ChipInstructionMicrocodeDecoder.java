package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.instructions.OpcodeUtil;

import java.util.Random;

abstract class ChipInstructionMicrocodeDecoder
{

    public abstract ChipInstructionMicrocode decode(byte lsb, byte msb);

    public static ChipInstructionMicrocodeDecoder decoder()
    {
        return new BaseInstructionSetDecoder();
    }

    private static class BaseInstructionSetDecoder extends ChipInstructionMicrocodeDecoder
    {
        ChipInstructionMicrocode[] microcodeHandlers;

        BaseInstructionSetDecoder()
        {
            initialiseMicroCodeHandler();
        }

        private void initialiseMicroCodeHandler()
        {
            microcodeHandlers = new ChipInstructionMicrocode[]
                    {
                       Class0Microcode,
                       Class1Microcode,
                       Class2Microcode,
                       Class3Microcode,
                       Class4Microcode,
                       Class5Microcode,
                       Class6Microcode,
                       Class7Microcode,
                       Class8Microcode,
                       Class9Microcode,
                       ClassAMicrocode,
                       ClassBMicrocode,
                       ClassCMicrocode,
                       ClassDMicrocode,
                       ClassEMicrocode,
                       ClassFMicrocode,
                    };
        }

        private ChipInstructionMicrocode Class0Microcode = (lsb, msb, state) ->
        {
            if (lsb == 0x00 && msb == (byte)0xEE)
                state.returnFromSubroutine();
            else if (lsb ==0x00 && msb == (byte)0xE0)
            {
                state.eraseDisplay();
                state.setProgramCounter(state.getProgramCounter() + 2);
            }
            else
                throw new RuntimeException(String.format("sorry, cannot run roms that call machine language code pc=%04X",state.getProgramCounter()));
        };


        private ChipInstructionMicrocode Class1Microcode = (lsb, msb, state) ->
        {
            int targetAddress = OpcodeUtil.addressFrom(lsb,msb);
            state.setProgramCounter(targetAddress);
        };

        private ChipInstructionMicrocode Class2Microcode = (lsb, msb, state) ->
        {
            int targetAddress = OpcodeUtil.addressFrom(lsb,msb);
            state.setProgramCounter(state.getProgramCounter() + 2);
            state.enterSubroutine();
            state.setProgramCounter(targetAddress);
        };

        private ChipInstructionMicrocode Class3Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            if (state.getRegister(X) == msb)
                state.setProgramCounter(state.getProgramCounter() + 2);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class4Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            if (state.getRegister(X) != msb)
                state.setProgramCounter(state.getProgramCounter() + 2);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class5Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            int Y = OpcodeUtil.nibbles(msb)[0];
            if (state.getRegister(X) == state.getRegister(Y))
                state.setProgramCounter(state.getProgramCounter() + 2);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class6Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            state.setRegister(X,msb);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class7Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            state.setRegister(X,(byte)(Byte.toUnsignedInt(state.getRegister(X)) + Byte.toUnsignedInt(msb)));
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class8Microcode = (lsb, msb, s) ->
        {
            int[] lsbNibbles = OpcodeUtil.nibbles(lsb);
            int[] msbNibbles = OpcodeUtil.nibbles(msb);
            int X = lsbNibbles[1];
            int Y = msbNibbles[0];
            int operation = msbNibbles[1];

            if (operation == 0)
            {
                s.setRegister(X, s.getRegister(Y));
            } else if (operation == 1)
            {
                // in the real Chip-8 VF is changed because it is used as temporary storage
                s.setRegister(X, (byte) (Byte.toUnsignedInt(s.getRegister(X)) | Byte.toUnsignedInt(s.getRegister(Y))));
            } else if (operation == 2)
            {
                // in the real Chip-8 VF is changed because it is used as temporary storage
                s.setRegister(X, (byte) (Byte.toUnsignedInt(s.getRegister(X)) & Byte.toUnsignedInt(s.getRegister(Y))));
            } else if (operation == 3)
            {
                // part of the original instruction set but undocumented
                s.setRegister(X, (byte) (Byte.toUnsignedInt(s.getRegister(X)) ^ Byte.toUnsignedInt(s.getRegister(Y))));
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
            } else if (operation == 6)
            {
                // part of the original instruction set but undocumented
                // NOTE:: there are too many conflicting opinions what the instruction does
                // I chose to implement the version "shift Y value and save the result to X, modifying X)
                int sourceValue = Byte.toUnsignedInt(s.getRegister(Y));
                s.setRegister(X, (byte) (sourceValue >>> 1));
                s.setRegister(0x0F, (byte)(sourceValue & 0x01));
            }else if (operation == 7)
            {
                // part of the original instruction set but undocumented
                // NOTE:: look like operation 5 but the subtraction order is reversed
                byte x_value = s.getRegister(X);
                byte y_value = s.getRegister(Y);
                int diff = (Byte.toUnsignedInt(y_value) - Byte.toUnsignedInt(x_value));
                s.setRegister(X, (byte) (diff & 0xFF));
                s.setRegister(0x0F, y_value < x_value ? (byte) 0 : (byte) 1);
            } else if (operation == 0x0E)
            {
                // part of the original instruction set but undocumented
                // NOTE:: there are too many conflicting opinions what the instruction does
                // I chose to implement the version "shift Y value and save the result to X, modifying X)
                int sourceValue = Byte.toUnsignedInt(s.getRegister(Y));
                s.setRegister(X, (byte) (sourceValue << 1));
                s.setRegister(0x0F, (byte)(sourceValue & 0x80));
            }
            else
            {
                throw new RuntimeException(String.format("unknown instruction [%02X %02X]",lsb,msb));
            }
            s.setProgramCounter(s.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode Class9Microcode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            int Y = OpcodeUtil.nibbles(msb)[0];
            if (state.getRegister(X) != state.getRegister(Y))
                state.setProgramCounter(state.getProgramCounter() + 2);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode ClassAMicrocode = (lsb, msb, state) ->
        {
            state.setIndexRegister(OpcodeUtil.addressFrom(lsb,msb));
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode ClassBMicrocode = (lsb, msb, state) ->
        {
            int targetAddress = OpcodeUtil.addressFrom(lsb,msb);
            state.setProgramCounter(targetAddress + Byte.toUnsignedInt(state.getRegister(0)));
        };

        private ChipInstructionMicrocode ClassCMicrocode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            Random random = new Random();
            byte rand = (byte) ((random.nextInt(256) & Byte.toUnsignedInt(msb)) & 0xff);
            state.setRegister(X,rand);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode ClassDMicrocode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            int Y = OpcodeUtil.nibbles(msb)[0];
            int N = OpcodeUtil.nibbles(msb)[1];

            int I = state.getIndexRegister();
            // patterns are 1 byte wide and up to 15 bytes long
            int numOfPatternBytes = (Math.min(N,15));
            byte posX = state.getRegister(X);
            byte posY = state.getRegister(Y);
            //System.out.println(String.format("write %d (%d) @ (%d,%d)",numOfPatternBytes,N,posX,posY));
            boolean spriteCollisionDetected = false;
            for (int i=0;i<numOfPatternBytes;i++)
            {
                byte patternByte = state.readMemory(state.getIndexRegister() + i);
                spriteCollisionDetected |= state.writeVram(posX, (byte) ((posY + i) & 0xff),patternByte);
            }
            state.setRegister(0x0F, spriteCollisionDetected ? (byte) 1: (byte) 0);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode ClassEMicrocode = (lsb, msb, state) ->
        {
            if (msb != (byte)0x9E && msb != (byte)0xA1)
                throw new RuntimeException(String.format("unknown instruction [%02X %02X]",lsb,msb));

            // The original documentation is ambiguous on teh meaning of this opcode.
            // other sources indicate that register X contains the key value
            int X = OpcodeUtil.nibbles(lsb)[1];
            boolean isKeyPressed = state.isKeyPressed(state.getRegister(X) & 0x0F);
            boolean conditionMatched = msb == (byte)0x9E ? isKeyPressed : !isKeyPressed;
            if (conditionMatched)
                state.setProgramCounter(state.getProgramCounter() + 2);
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        private ChipInstructionMicrocode ClassFMicrocode = (lsb, msb, state) ->
        {
            int X = OpcodeUtil.nibbles(lsb)[1];
            if (msb == 0x07)
                state.setRegister(X,state.getTimer());
            else if (msb == 0x0A)
                state.setRegister(X,state.waitForKey());
            else if (msb == 0x15)
                state.setTimer(state.getRegister(X));
            else if (msb == 0x18)
                state.setTone(state.getRegister(X));
            else if (msb == 0x1E)
                state.setIndexRegister(state.getIndexRegister()+state.getRegister(X));
            else if (msb == 0x29)
                state.setIndexRegister(state.getDisplayPatternAddress(OpcodeUtil.nibbles(state.getRegister(X))[1]));
            else if (msb == 0x33)
            {
                byte value = state.getRegister(X);
                int indexAddress = state.getIndexRegister();
                for (int i=2;i>=0;i--)
                {
                    state.writeMemory(indexAddress + i, (byte) (value % 10));
                    value = (byte) (value / 10);
                }
            }
            else if (msb == 0x55)
            {
                int indexAddress = state.getIndexRegister();
                for (int i=0;i<=X;i++)
                    state.writeMemory(indexAddress++,state.getRegister(i));
                state.setIndexRegister(indexAddress);
            }
            else if (msb == 0x65)
            {
                int indexAddress = state.getIndexRegister();
                for (int i=0;i<=X;i++)
                    state.setRegister(i,state.readMemory(indexAddress++));
                state.setIndexRegister(indexAddress);
            }else
            {
                throw new RuntimeException(String.format("unknown instruction [%02X %02X]",lsb,msb));
            }
            state.setProgramCounter(state.getProgramCounter() + 2);
        };

        public ChipInstructionMicrocode decode(byte lsb, byte msb)
        {
            int[] lsbNibbles = OpcodeUtil.nibbles(lsb);
            return microcodeHandlers[lsbNibbles[0]];
        }
    }
}
