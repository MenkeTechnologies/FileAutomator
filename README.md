# File Automator

A feature-rich JavaFX desktop application for searching files, previewing content, performing batch operations, and playing media — all from a single multi-pane interface.

Built with Kotlin, JavaFX 24, and JDK 25.

![multiple panes in file automator app](/fileAutomatorScreenshot1.png)

## Features

### File Search
- Regex-based file searching with AND/OR filter logic
- Case-insensitive and path-matching modes
- Real-time file system monitoring via watch service
- Auto-complete for directory paths
- Toggle hidden files and directories

### Multi-Pane Interface
- **File Browser** — directory tree view with drag-and-drop
- **Results Table** — searchable, sortable results
- **Preview Pane** — inline preview for images, PDFs, Office docs, code, and media
- **Web View** — integrated browser
- **Terminal** — embedded terminal emulator (TerminalFX)

### File Operations
- Open, copy, move, rename, and delete files
- Secure delete (permanent) and move to trash
- Batch copy with progress tracking, pause/resume/cancel
- Copy absolute path to clipboard

### File Preview
- **Images** — direct inline preview
- **PDFs** — page-by-page rendering with thumbnails (PDFBox)
- **Office** — Word (.doc/.docx) and Excel (.xls/.xlsx) content extraction (Apache POI)
- **Code** — syntax highlighting for Java, Kotlin, Python, Ruby, JavaScript, XML, HTML, CSS (RichTextFX)
- **Archives** — JAR file listing
- **Media** — video/audio thumbnail display

### Media Player
Full-featured media player with playback controls, volume, speed adjustment, loop/autoplay, and fit-to-screen options.

### Customization
- Theme editor with color pickers for background, text, tree view, table view, and accents
- Font family and size selection
- Live CSS stylesheet reload
- Persistent styling via `~/modify.css`

### System Monitoring
- Real-time CPU and memory usage display
- Activity indicators for long-running operations

## Keyboard Shortcuts (Media Player)

| Key | Action |
|-----|--------|
| `Space` | Play / Pause |
| `F` | Full screen |
| `Q` | Snap to 25% screen |
| `H` | Snap to 50% screen |
| `W` | Fit to 100% screen |
| `N` | Next track |
| `P` | Previous track |
| `B` | Rewind 15 seconds |
| `0`–`9` | Seek to minute 0–9 |

## Tech Stack

- **Language:** Kotlin 2.1.20
- **UI:** JavaFX 24 with FXML
- **Runtime:** JDK 25
- **Build:** Maven
- **Key Libraries:** Apache POI, PDFBox, ControlsFX, RichTextFX, TerminalFX, Bouncy Castle, Jackson

## Building

```bash
mvn clean package
```
