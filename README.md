# chip-8
Simulator for the [Chip8 system](https://en.wikipedia.org/wiki/CHIP-8)

## Introduction
This started as a weekend project to create a decompiler for Chip8 roms. 

Immediately after that I expanded the idea to a full simulator with basic debugging functionality.

All the information for the system comes from the system manuals (found in the docs folder) and scattered information on various sites.
To the best of my knowledge the information I got is complete.

## Decompiler
The decompiler accepts the name of the rom file and prints the decompiled program.
It does not have advanced features as data flow analysis or able to tell code vs data areas.

Not all the mnemonics used in the decompiler are the original ones found in the manual, because some of them do not have very intuitive names. 
So in some cases a semantically similar mnemonic is used, influenced by higer level languages. 

For example, 
- `SKIP NEXT IF V3 == 0` instead of `SKIP;V3 EQ 0`
- `SKIP NEXT IF V0 != HEXKEY` instead of `SKIP;V0 NE KEY`
- `V1 &= V0` instead of `V1 = V1 AND V0`
- `V1 += V0` instead of `V1 = V1 + V0`

here is an example of decompiling the `maze.rom`

```
--- Chip-8 decompiler by Nick Tsakonas (c) 2018
--- asm listing of rom [roms/maze.rom]

ADDR  OPCD  MNEMONIC
-----------------------------------------------
0200  A21E  I = 021E
0202  C201  V2 = RAND(01)
0204  3201  SKIP NEXT IF V2 == 01
0206  A21A  I = 021A
0208  D014  SHOW 4MI@V0V1
020A  7004  V0 += 04
020C  3040  SKIP NEXT IF V0 == 40
020E  1200  GOTO 0200
0210  6000  V0 = 00
0212  7104  V1 += 04
0214  3120  SKIP NEXT IF V1 == 20
0216  1200  GOTO 0200
0218  1218  GOTO 0218
021A  8040  V0 = V4
021C  2010  CALL 0010
021E  2040  CALL 0040
0220  8010  V0 = V1

--- asm listing done!
```


## Simulator
The simulator is not trying to be a cycle-accurate simulator but only close to what the original system might be like.
There is one function missing, the generation of sound (the beep command).

A set of roms found for free is included in the `roms` folder. I have tested extensively all of those and compared the output 
with other simulators online. There are a couple of them behaving differently (and I am not sure why) but most of them work without
any glitch.

The keyboard mapping for the simulator is just a convenient one for a PC keyabord and not the one found in the manual.

The key mapping is as follows:

|Chip key| PC key|
|---|---|
| 0 - 9 | 0 - 9|
| A | A|
| B | S|
| C | D|
| D | Z|
| E | X|
| F | C|


Every game has its own keymap, some of those are documented in the _Chip8 Reference Manual - Peter Miller.pdf_ 
found in the docs folder.

## Debugger

When the simulator is started with the `--debug` option, a basic (console) debugger is attached and breaks at the beginning of the program.
From there, you can step in the program and inspect the state of the cpu and memory.

It is not yet a fully fledged debugger but it was added out of necessity during development to debug the simulator.
It was put together rather quickly but it provides basic commands for general use.

|Command| Description|
|---|---|
| S | Single step|
| R | Resume execution|
| SR | Show registers|
| SV | Show Video Ram contents|
|SM xxxx| Show memory dump from address xxxx|
|BPL|Show list of break points|
|BPA xxxx|Add breakpoint at address xxxx|
|BPR xxxx|Remove breakpoint at address xxxx|
|BPD xxxx|Disable breakpoint at address xxxx|
|BPE xxxx|Enable breakpoint at address xxxx|

### Example commands
Here is the output of some commands as the `pong.rom` is being debugged.

```
(PAUSED)>s
BREAK at address 0214
  0214  6802  V8 = 02
  0216  6060  V0 = 60
  0218  F015  TIMER() = V0
  021A  F007  V0 = TIMER()
  021C  3000  SKIP NEXT IF V0 == 00
  021E  121A  GOTO 021A
(PAUSED)>s
BREAK at address 0216
  0216  6060  V0 = 60
  0218  F015  TIMER() = V0
  021A  F007  V0 = TIMER()
  021C  3000  SKIP NEXT IF V0 == 00
  021E  121A  GOTO 021A
  0220  C717  V7 = RAND(17)
(PAUSED)>sr
Chip-8 status:
pc: 0216   sp:0ECF   I:DEAD0020
V00 = 00   V01 = 00   V02 = 00   V03 = 00
V04 = 29   V05 = 00   V06 = 03   V07 = 00
V08 = 02   V09 = 00   V10 = 02   V11 = 0C
V12 = 3F   V13 = 0C   V14 = 00   V15 = 00

Stack (HH -----> LL) stack top:0ECF

12 02 00 00 00 00 00 00 
00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00 
---------------------------------------------
(PAUSED)>sv
---------------------------VRAM DUMP----------------------------
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
....................****.................****...................
....................*..*.................*..*...................
....................*..*.................*..*...................
....................*..*.................*..*...................
....................****.................****...................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
..*............................................................*
..*............................................................*
..*............................................................*
..*............................................................*
..*............................................................*
..*............................................................*
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
................................................................
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(PAUSED)>sm 0200
ADDR --------------------- HEX --------------------- ----- ASCII ----
0200 6A 02 6B 0C 6C 3F 6D 0C A2 EA DA B6 DC D6 6E 00 j.k.l.m.......n.
0210 22 D4 66 03 68 02 60 60 F0 15 F0 07 30 00 12 1A ..f.h.......0...
0220 C7 17 77 08 69 FF A2 F0 D6 71 A2 EA DA B6 DC D6 ..w.i....q......
0230 60 01 E0 A1 7B FE 60 04 E0 A1 7B 02 60 1F 8B 02 ................
0240 DA B6 60 0C E0 A1 7D FE 60 0D E0 A1 7D 02 60 1F ................
0250 8D 02 DC D6 A2 F0 D6 71 86 84 87 94 60 3F 86 02 .......q........
0260 61 1F 87 12 46 02 12 78 46 3F 12 82 47 1F 69 FF a...F..xF...G.i.
0270 47 00 69 01 D6 71 12 2A 68 02 63 01 80 70 80 B5 G.i..q..h.c..p..
0280 12 8A 68 FE 63 0A 80 70 80 D5 3F 01 12 A2 61 02 ..h.c..p......a.
0290 80 15 3F 01 12 BA 80 15 3F 01 12 C8 80 15 3F 01 ................
02A0 12 C2 60 20 F0 18 22 D4 8E 34 22 D4 66 3E 33 01 .........4..f.3.
02B0 66 03 68 FE 33 01 68 02 12 16 79 FF 49 FE 69 FF f.h.3.h...y.I.i.
02C0 12 C8 79 01 49 02 69 01 60 04 F0 18 76 01 46 40 ..y.I.i.....v.F.
02D0 76 FE 12 6C A2 F2 FE 33 F2 65 F1 29 64 14 65 00 v..l...3.e..d.e.
02E0 D4 55 74 15 F2 29 D4 55 00 EE 80 80 80 80 80 80 .Ut....U........
02F0 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ................
(PAUSED)>
```

# License
This work is released under the GNU General Public License v3.0
