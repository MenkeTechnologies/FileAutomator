# FILE//AUTOMATOR_

```
 ███████╗██╗██╗     ███████╗     █████╗ ██╗   ██╗████████╗ ██████╗ ███╗   ███╗ █████╗ ████████╗ ██████╗ ██████╗
 ██╔════╝██║██║     ██╔════╝    ██╔══██╗██║   ██║╚══██╔══╝██╔═══██╗████╗ ████║██╔══██╗╚══██╔══╝██╔═══██╗██╔══██╗
 █████╗  ██║██║     █████╗      ███████║██║   ██║   ██║   ██║   ██║██╔████╔██║███████║   ██║   ██║   ██║██████╔╝
 ██╔══╝  ██║██║     ██╔══╝      ██╔══██║██║   ██║   ██║   ██║   ██║██║╚██╔╝██║██╔══██║   ██║   ██║   ██║██╔══██╗
 ██║     ██║███████╗███████╗    ██║  ██║╚██████╔╝   ██║   ╚██████╔╝██║ ╚═╝ ██║██║  ██║   ██║   ╚██████╔╝██║  ██║
 ╚═╝     ╚═╝╚══════╝╚══════╝    ╚═╝  ╚═╝ ╚═════╝    ╚═╝    ╚═════╝ ╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```

> **[ SYSTEM ONLINE ]** A high-performance JavaFX desktop rig for searching files, previewing content, running batch ops, and playing media — all jacked into a single multi-pane interface.

> `BUILT WITH` Kotlin // JavaFX 24 // JDK 25

![multiple panes in file automator app](/fileAutomatorScreenshot1.png)

---

## >> FEATURE_MANIFEST

### `[01]` FILE SEARCH ENGINE
- Regex-powered file searching with `AND`/`OR` filter logic
- Case-insensitive and path-matching modes
- Real-time file system monitoring via watch service
- Auto-complete for directory paths
- Toggle hidden files and directories

### `[02]` MULTI-PANE INTERFACE
- **FILE BROWSER** — directory tree view with drag-and-drop
- **RESULTS GRID** — searchable, sortable results
- **PREVIEW PANE** — inline preview for images, PDFs, Office docs, code, and media
- **WEB VIEW** — integrated browser
- **TERMINAL** — embedded terminal emulator (TerminalFX)

### `[03]` FILE OPERATIONS
- Open, copy, move, rename, and delete files
- Secure delete (permanent wipe) and move to trash
- Batch copy with progress tracking, pause/resume/cancel
- Copy absolute path to clipboard

### `[04]` FILE PREVIEW MATRIX
| Format | Backend |
|--------|---------|
| Images | Direct inline render |
| PDFs | Page-by-page with thumbnails — `PDFBox` |
| Office | .doc/.docx & .xls/.xlsx extraction — `Apache POI` |
| Code | Syntax highlighting (Java, Kotlin, Python, Ruby, JS, XML, HTML, CSS) — `RichTextFX` |
| Archives | JAR file listing |
| Media | Video/audio thumbnail display |

### `[05]` MEDIA PLAYER
Full-featured playback rig — controls, volume, speed adjustment, loop/autoplay, and fit-to-screen.

### `[06]` CUSTOMIZATION PROTOCOL
- Theme editor with color pickers for bg, text, tree view, table view, and accents
- Font family and size selection
- Live CSS stylesheet reload
- Persistent styling via `~/modify.css`

### `[07]` SYSTEM MONITOR
- Real-time CPU and memory usage readout
- Activity indicators for long-running operations

---

## >> KEYBIND_MAP `[ MEDIA PLAYER ]`

```
 KEY        ACTION
 ─────────────────────────────────
 SPACE      Play / Pause
 F          Full screen
 Q          Snap to 25% screen
 H          Snap to 50% screen
 W          Fit to 100% screen
 N          Next track
 P          Previous track
 B          Rewind 15s
 0-9        Seek to minute 0–9
```

---

## >> TECH_STACK

```
 LANGUAGE    Kotlin 2.1.20
 UI          JavaFX 24 + FXML
 RUNTIME     JDK 25
 BUILD       Maven
 LIBRARIES   Apache POI // PDFBox // ControlsFX // RichTextFX
             TerminalFX // Bouncy Castle // Jackson
```

---

## >> BUILD_SEQUENCE

```bash
mvn clean package
```

---

```
 [ END OF LINE ]
```
