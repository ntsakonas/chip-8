package ntsakonas.retro.chipate.simulator.keymaps;

import java.util.HashMap;
import java.util.Map;

public class ConvenientKeyMap implements KeyboardMapping {

    Map<Character, Integer> keymap = new HashMap<>();

    public ConvenientKeyMap() {
        keymap.put('0', 0); // key 0
        keymap.put('1', 1); // key 1
        keymap.put('2', 2); // key 2
        keymap.put('3', 3); // key 3
        keymap.put('4', 4); // key 4
        keymap.put('5', 5); // key 5
        keymap.put('6', 6); // key 6
        keymap.put('7', 7); // key 7
        keymap.put('8', 8); // key 8
        keymap.put('9', 9); // key 9
        keymap.put('a', 10); // key A
        keymap.put('s', 11); // key B
        keymap.put('d', 12); // key C
        keymap.put('z', 13); // key D
        keymap.put('x', 14); // key E
        keymap.put('c', 15); // key F
    }

    @Override
    public int mapKey(char key) {
        Integer mappedKey = keymap.get(Character.toLowerCase(key));
        return mappedKey != null ? mappedKey : -1;
    }
}
