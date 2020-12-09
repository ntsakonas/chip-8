package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class DisplayVideoRamCmd extends DebuggerCommand {

    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState) {
        System.out.println("---------------------------VRAM DUMP----------------------------");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        byte[] videoRam = systemState.getVideoRam();
        StringBuilder lineBuffer = new StringBuilder();
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 8; x++) {
                int vramPattern = Byte.toUnsignedInt(videoRam[8 * y + x]);
                int vramPatternMask = 0x80;
                for (int pixel = 0; pixel < 8; pixel++) {
                    if ((vramPattern & vramPatternMask) == vramPatternMask)
                        lineBuffer.append("*");
                    else
                        lineBuffer.append(".");
                    vramPatternMask >>= 1;
                }
            }
            System.out.println(lineBuffer.toString());
            lineBuffer.setLength(0);
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
