# Isokinetic robot PC software



**TThis project was created by Yanggang Feng's lab. We thank all lab members for their contributions to this project.** Isokinetic robot PC software is an isokinetic robot visualization and control system. It facilitates real-time interaction with hardware via high-efficiency binary protocols, supporting both Serial and Network (TCP) communication. 

---

## Project Structure

| Directory/File | Description |
| :--- | :--- |
| **`src/`** | Core source code (Java) and FXML resources |
| **`lib/`** | External local JAR dependencies |
| **`target/`** | Maven build output (compiled JARs and artifacts) |
| **`classes/`** | Compiled bytecode generated during the build |
| **`pom.xml`** | Maven project configuration and dependency management |

---

## Development Environment

### Recommended IDE
- IntelliJ IDEA (version: 2025)

### IntelliJ IDEA Setup

1. **Import Project**
    - Open IntelliJ IDEA
    - Select `Open` → choose the project root directory
    - IntelliJ will automatically detect the Maven project (`pom.xml`)

2. **Configure JDK**
    - Navigate to: `File` → `Project Structure` → `Project`
    - Set Project SDK to **JDK 17**

3. **Enable Maven**
    - Ensure Maven dependencies are loaded automatically
    - If not, click `Reload All Maven Projects`

4. **JavaFX Configuration**
    - Ensure JavaFX SDK is properly configured
    - Or use Maven plugin (recommended, already configured in pom.xml)

5. **Run Application**
    - Locate main class: `Main.java` 
    - Right-click → `Run`

### Typical install time
System: Windows 11
CPU: i7 14700Hx
GPU: RTX4070laptop
Typical install time < 1 min