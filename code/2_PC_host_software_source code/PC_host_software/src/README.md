# Package: main.java.club.hsspace.hs.motorcontrol

This package implements the core control logic of the application, covering protocol parsing, communication management, configuration handling, and UI integration.

---

##  Communication & Protocol Layer

### Comm.java (Interface)
Defines the communication protocol abstraction.  
Provides low-level utilities for:
- Bitwise operations
- Conversion between Java primitive types and Little-Endian byte streams

Serves as the protocol specification layer for motor hardware interaction.

### ComManage.java
Manages serial port communication.

Responsibilities:
- Continuous byte stream ingestion
- Frame boundary detection via state machine
- Parsing of fixed-length frames:
    - 32-byte PID frames
    - 52-byte telemetry frames

Ensures robust and deterministic decoding under high-frequency data streams.

### NetManage.java
Handles network-based communication.

Responsibilities:
- TCP client implementation using sockets
- Asynchronous data reception in a dedicated thread
- Decoupling network I/O from UI thread execution

---


### DataInterface.java (Interface)
Defines the data distribution contract between the communication layer and the UI layer.

Core methods:
- handleReceive: processes incoming telemetry data
- getPid: retrieves PID parameter data

Enables loose coupling and testability across modules.

### SettingManage.java
Manages application configuration lifecycle.

Responsibilities:
- Discovery and initialization of setting.properties
- Persistent storage and retrieval of configuration parameters
- Runtime access to user-defined settings such as baud rate

---

## UI & Application Entry

### Main.java
Application entry point.  
Initializes and launches the JavaFX runtime environment.

### HelloApplication.java
Primary stage initializer.

Responsibilities:
- Window creation
- Loading and binding of FXML layout

### MainController.java
UI controller implementation.

Responsibilities:
- Implements DataInterface
- Consumes parsed data from communication layer
- Updates real-time UI components such as charts and gauges

Acts as the integration layer between backend data flow and frontend visualization.

### AppLauncher.java
Provides an auxiliary entry mechanism for compatibility with native packaging and deployment environments.

### module-info.java
Defines module boundaries and dependencies using the Java Platform Module System.

---

## Resources

Located in src/main/resources.

### main-view.fxml
Declarative UI layout definition.

Defines:
- Component hierarchy
- Layout structure
- UI bindings

### image/
Static asset directory containing:
- Application icons
- Branding resources
- Background images

---



## Protocol Specifications

### Byte Order
- Little-endian (LSB first) for all multi-byte integers

### Baud Rate
- Default: 460800 bps

### Frame Delimiters
- Header (incoming to PC): `0xbb 0x44`
- Tail (fixed): `0x0d 0x0a` (\r\n)

---

## 1. 32-Byte Packet: PID Configuration (ST_PID)

Used for reading and writing motor control loop parameters.

| Byte Offset | Length | Field            | Description                  |
|------------|--------|------------------|------------------------------|
| 0 - 1      | 2      | Header           | 0xbb 0x44                    |
| 2          | 1      | Key / Cmd        | Fixed value 0x02             |
| 3          | 1      | Mode             | Motor control mode   |
| 4 - 7      | 4      | Target / S       | Target value / setpoint      |
| 8 - 11     | 4      | P (Kp)           | Proportional gain            |
| 12 - 15    | 4      | I (Ki)           | Integral gain                |
| 16 - 19    | 4      | D (Kd)           | Derivative gain              |
| 20 - 23    | 4      | Max Output       | Positive saturation limit    |
| 24 - 27    | 4      | Min Output       | Negative saturation limit    |
| 28 - 29    | 2      | Status           | 16-bit status flag bitmask   |
| 30 - 31    | 2      | Tail             | 0x0d 0x0a                    |

---

## 2. 52-Byte Packet: Telemetry / Status (ST_MINFO)

Used for high-speed streaming of real-time motor telemetry data.

| Byte Offset | Length | Field   | Description                |
|------------|--------|--------|----------------------------|
| 0 - 1      | 2      | Header | 0xbb 0x44                  |
| 2          | 1      | Key    | Fixed value 0x00           |
| 3          | 1      | Mode   | Motor control mode         |
| 4 - 7      | 4      | Data 1 | Monitoring 1               |
| 8 - 11     | 4      | Data 2 | Monitoring 2               |
| 12 - 15    | 4      | Data 3 | Monitoring 3               |
| 16 - 19    | 4      | Data 4 | Monitoring 4               |
| 20 - 23    | 4      | Data 5 | Monitoring 5               |
| 24 - 27    | 4      | Data 6 | Monitoring 6               |
| 28 - 31    | 4      | Data 7 | Monitoring 7               |
| 32 - 35    | 4      | Data 8 | Monitoring 8               |
| 36 - 39    | 4      | Data 9 | Monitoring 9               |
| 40 - 43    | 4      | Data 10| Monitoring 10              |
| 44 - 47    | 4      | Data 11| Monitoring 11              |
| 48 - 49    | 2      | Status | Status                     |
| 50 - 51    | 2      | Tail   | 0x0d 0x0a                  |

---

## Data Parsing Logic (Java Reference)

The system uses a sliding-window state machine to process incoming byte streams.  
The parsing workflow implemented in `ComManage.java` is as follows:

### 1. Header Detection
- Scan incoming buffer for:
    - `buffer[0] == 0xbb`
    - `buffer[1] == 0x44`

### 2. Frame Length Verification
- If buffer length ≥ 32 and `buffer[30..31] == 0x0d 0x0a`  
  → identify as PID packet

- If buffer length ≥ 52 and `buffer[50..51] == 0x0d 0x0a`  
  → identify as telemetry packet

### 3. Payload Extraction
- Skip first 4 bytes:
    - Header (2 bytes)
    - Key (1 byte)
    - Mode (1 byte)

- Parse remaining data:
    - Read every 4 bytes as Int32
    - Use Little-endian byte order:


### 4. Status Handling
- Extract 16-bit status field:
    - Located immediately before the 2-byte tail

### 5. Buffer Management
- After successful frame parsing:
    - Remove processed bytes from buffer
    - Slide window forward to continue parsing