package ntsakonas.retro.chipate.simulator.keymaps;

import java.util.HashMap;
import java.util.Map;

public class ConvenientKeyMap implements KeyboardMapping
{
    Map<Character,Integer> keymap = new HashMap<>();

    public ConvenientKeyMap()
    {
        keymap.put('0',0); // key A
        keymap.put('1',1); // key A
        keymap.put('2',2); // key A
        keymap.put('3',3); // key A
        keymap.put('4',4); // key A
        keymap.put('5',5); // key A
        keymap.put('6',6); // key A
        keymap.put('7',7); // key A
        keymap.put('8',8); // key A
        keymap.put('9',9); // key A
        keymap.put('a',10); // key A
        keymap.put('s',11); // key B
        keymap.put('d',12); // key C
        keymap.put('z',13); // key D
        keymap.put('x',14); // key E
        keymap.put('c',15); // key F
    }

    @Override
    public int mapKey(char key)
    {
        Integer mappedKey = keymap.get(Character.toLowerCase(key));
        return mappedKey != null ? mappedKey : -1;
    }
}
