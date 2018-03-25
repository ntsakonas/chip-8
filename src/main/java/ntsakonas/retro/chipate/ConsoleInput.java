package ntsakonas.retro.chipate;

import java.util.Scanner;

//WIP
public class ConsoleInput implements SystemInput
{
    private static Scanner inputScanner = null;
    public static Scanner getInput()
    {
        if (inputScanner  == null)
            inputScanner = new Scanner(new java.io.BufferedInputStream(System.in), "UTF-8");
        return inputScanner;
    }

    /*
    public synchronized byte getKeyPressed()
    {
        if (getInput().hasNext())
        {
            System.out.println("K="+getInput().nextInt());
        }
        return 0;
    }

    public byte waitForKey()
    {
        return 0;
    }
   */
    @Override
    public char getKey()
    {
        return 0;
    }
}
