package ntsakonas.retro.chipate.debugger;

import java.util.Arrays;

public class DebugBreakpoints
{

    public static class BreakPoint
    {
        public final int address;
        public boolean enabled;

        public BreakPoint(int address)
        {
            this.address = address;
            this.enabled = true;
        }
    }

    private final int MAX_NUM_OF_PC_BREAKPOINTS = 32;
    BreakPoint[] pcBreakpoints = new DebugBreakpoints.BreakPoint[MAX_NUM_OF_PC_BREAKPOINTS];
    int numOfPcBreakPoints = 0;

    public DebugBreakpoints()
    {

    }

    public boolean hasBreakpoints()
    {
        return numOfPcBreakPoints > 0;
    }

    public BreakPoint[] getBreakpoints()
    {
        return Arrays.copyOf(pcBreakpoints,pcBreakpoints.length);
    }

    public boolean reachedBreakpoint(int programCounter)
    {
        for (int i = 0; i< numOfPcBreakPoints;i++)
        {
            DebugBreakpoints.BreakPoint breakPoint = pcBreakpoints[i];
            if (programCounter == breakPoint.address && breakPoint.enabled)
                return true;
        }
        return false;
    }

    public void addNewBreakPointAt(int address)
    {
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            if (pcBreakpoints[i].address == address)
            {
                System.out.println("Breakpoint already set at this location.");
                return;
            }
        }
        if (numOfPcBreakPoints < MAX_NUM_OF_PC_BREAKPOINTS)
            pcBreakpoints[numOfPcBreakPoints++] = new DebugBreakpoints.BreakPoint(address);
        else
            System.out.println("Cannot add more breakpoints.");
    }

    public void deleteBreakPointAt(int address)
    {
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            if (pcBreakpoints[i].address == address)
            {
                // erase breakpoint by moving the last breakpoint in this location
                numOfPcBreakPoints--;
                pcBreakpoints[i] = pcBreakpoints[numOfPcBreakPoints];
                pcBreakpoints[numOfPcBreakPoints] = null;
                return;
            }
        }
        System.out.println("Could not find this breakpoint.");
    }

    public void enableBreakPointAt(int address)
    {
        setBreakPointStatus(address,true);
    }

    public void disableBreakPointAt(int address)
    {
        setBreakPointStatus(address,false);
    }

    private void setBreakPointStatus(int address,boolean enabled)
    {
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            if (pcBreakpoints[i].address == address)
            {
                pcBreakpoints[i].enabled = enabled;
                return;
            }
        }
        System.out.println("Could not find this breakpoint.");
    }

    public void listBreakpoints()
    {
        System.out.println("Current breakpoints");
        System.out.println("ADDR STATUS");
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            System.out.println(String.format("%04X %S", pcBreakpoints[i].address
                    , pcBreakpoints[i].enabled ? "ENABLED" : "DISABLED"));
        }
    }

}
