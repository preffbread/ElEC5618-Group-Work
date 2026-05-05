# Markov Chain Testing for Paste Functionality

This folder contains the Week 7 / Markov-chain testing work for the FreeMind
paste functionality.

## Selected Functionality

The selected functionality is the paste operation in FreeMind. This continues
from the previous FSM testing task.

## Implementation Summary

FSM transition logging was added to the FreeMind paste flow:

| File | Purpose |
| --- | --- |
| `freemind/freemind/freemind/main/FsmPasteLogger.java` | Writes FSM transitions to the console and to `paste_fsm_log.txt`. |
| `freemind/freemind/freemind/modes/mindmapmode/actions/PasteAction.java` | Logs the start of the paste operation and clipboard read step. |
| `freemind/freemind/freemind/modes/mindmapmode/actions/xml/actors/PasteActor.java` | Logs content validation, successful insertion, and failed paste outcomes. |
| `PasteMarkovAnalyzer.java` | Reads the log file and calculates transition probabilities. |

## Log Generation

After FreeMind is built and started, perform paste actions in the GUI. The log
will be printed to the console and also written to:

```text
paste_fsm_log.txt
```

If FreeMind is started through the Ant `run` target, this file is expected to
appear in the runtime directory used by that target.

## Log Analysis

Compile and run the analyzer:

```powershell
cd D:\Git\ElEC5618-Group-Work\07_Markov_chain
javac PasteMarkovAnalyzer.java
java PasteMarkovAnalyzer path\to\paste_fsm_log.txt
```

The output lists each transition count, the total number of outgoing
transitions from the source state, and the calculated probability.

## Current Environment Note

The analyzer has been checked with Java 8 and compiles successfully. Building
and running the full FreeMind product requires Ant, but `ant` is not currently
available in the local command-line environment.
