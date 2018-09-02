package ntsakonas.retro.chipate.instructions;


import ntsakonas.retro.chipate.instructions.originalset.*;

public class Instructions
{
    public interface Parser
    {
        ChipInstruction decode(int address, byte lsb, byte msb);
    }

    public static Parser parser()
    {
        // at the moment only the original instruction set is supported
        return new OriginalChip8SetParser();
    }

    private interface Handler
    {
        ChipInstruction decode(int address, byte lsb, byte msb);
    }

    private static class OriginalChip8SetParser implements Parser
    {
        Handler[] instructionHandlers;

        OriginalChip8SetParser()
        {
            initHandlers();
        }

        private void initHandlers()
        {
            instructionHandlers = new Handler[]{
                    class00handler,
                    class01handler,
                    class02handler,
                    class03handler,
                    class04handler,
                    class05handler,
                    class06handler,
                    class07handler,
                    class08handler,
                    class09handler,
                    class0Ahandler,
                    class0Bhandler,
                    class0Chandler,
                    class0Dhandler,
                    class0Ehandler,
                    class0Fhandler,
            };
        }


        Handler class00handler = (address, lsb, msb) ->
        {
            if (Byte.toUnsignedInt(msb) == 0xEE)
                return new ReturnFromSubroutine(address,lsb,msb);
            else if (Byte.toUnsignedInt(msb) == 0xE0)
                return new EraseDisplay(address,lsb,msb);
            else
                return new DoMachineLanguageSubroutine(address,lsb,msb);
        };

        Handler class01handler = (address, lsb, msb) -> new Goto(address,lsb,msb,false);
        Handler class02handler = (address, lsb, msb) -> new DoSubroutine(address,lsb,msb);
        Handler class03handler = (address, lsb, msb) -> new SkipNext(address,lsb,msb,SkipNext.EqualsConstant);
        Handler class04handler = (address, lsb, msb) -> new SkipNext(address,lsb,msb,SkipNext.NotEqualsConstant);
        Handler class05handler = (address, lsb, msb) -> new SkipNext(address,lsb,msb,SkipNext.EqualsRegister);
        Handler class06handler = (address, lsb, msb) -> new AssignValue(address,lsb,msb, AssignValue.Constant);
        Handler class07handler = (address, lsb, msb) -> new AssignValue(address,lsb,msb, AssignValue.RegisterPlusConst);
        Handler class08handler = (address, lsb, msb) ->
        {
            int variant = OpcodeUtil.nibbles(msb)[1];
            if (AssignValue.validClass8Assignment(variant))
                return new AssignValue(address,lsb,msb, AssignValue.getClass8Assignment(variant));
            else
                return null;
        };

        Handler class09handler = (address, lsb, msb) -> new SkipNext(address,lsb,msb,SkipNext.NotEqualsRegister);
        Handler class0Ahandler = (address, lsb, msb) -> new AssignValue(address,lsb,msb, AssignValue.ConstantToI);
        Handler class0Bhandler = (address, lsb, msb) -> new Goto(address,lsb,msb,true);
        Handler class0Chandler = (address, lsb, msb) -> new AssignValue(address,lsb,msb, AssignValue.Random);
        Handler class0Dhandler = (address, lsb, msb) -> new ShowPattern(address,lsb,msb);
        Handler class0Ehandler = (address, lsb, msb) ->
        {
            if (Byte.toUnsignedInt(msb) == 0x9E)
                return new SkipNext(address,lsb,msb,SkipNext.EqualsKey);
            else if (Byte.toUnsignedInt(msb) == 0xA1)
                return new SkipNext(address,lsb,msb,SkipNext.NotEqualsKey);
            return null;
        };
        Handler class0Fhandler = (address, lsb, msb) ->
        {
            int variant = Byte.toUnsignedInt(msb);
            if (AssignValue.validClassFAssignment(variant))
                return new AssignValue(address,lsb,msb, AssignValue.getClassFAssignment(variant));
            else
                return null;
        };

        public ChipInstruction decode(int address, byte lsb, byte msb)
        {
            int[] lsbNibbles = OpcodeUtil.nibbles(lsb);
            Handler handler = instructionHandlers[lsbNibbles[0]];
            ChipInstruction instruction = handler.decode(address,lsb,msb);
            return instruction != null ? instruction : new UnknownInstruction(address,lsb,msb);
        }
    }

}
