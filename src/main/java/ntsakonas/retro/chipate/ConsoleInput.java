package ntsakonas.retro.chipate;

import java.util.Scanner;

//WIP
public class ConsoleInput implements SystemInput {

    private static Scanner inputScanner = null;

    public static Scanner getInput() {
        if (inputScanner == null)
            inputScanner = new Scanner(new java.io.BufferedInputStream(System.in), "UTF-8");
        return inputScanner;
    }

    @Override
    public char getKey() {
        return 0;
    }
}
