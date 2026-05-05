# Paste FSM Log Structure

This file documents the log format used for the Markov-chain testing task.

## Log Format

Each paste FSM transition is written as one line:

```text
FSM_LOG|time=2026-04-25T15:20:31.123|event=PasteCommand|from=S0_Idle|to=S1_PasteRequested|user=username
```

## Fields

| Field | Meaning |
| --- | --- |
| `FSM_LOG` | Prefix used to identify Markov-chain FSM log lines. |
| `time` | Timestamp when the transition was recorded. |
| `event` | The event that triggered the transition. |
| `from` | Source FSM state. |
| `to` | Target FSM state. |
| `user` | Operating-system user name. |

## Paste FSM States

| State | Meaning |
| --- | --- |
| `S0_Idle` | FreeMind is waiting for user action. |
| `S1_PasteRequested` | The user has selected the paste command. |
| `S2_ClipboardContentRead` | Clipboard content has been read. |
| `S3_ContentValidatedAndProcessed` | Clipboard content is valid and is being processed. |
| `S4_NodeInserted` | A new node has been inserted successfully. |
| `S5_PasteFailedNoAction` | Paste failed or no node was inserted. |

## Analyzer

`PasteMarkovAnalyzer.java` reads the log file and calculates transition probabilities.

```powershell
javac PasteMarkovAnalyzer.java
java PasteMarkovAnalyzer paste_fsm_log.txt
```
