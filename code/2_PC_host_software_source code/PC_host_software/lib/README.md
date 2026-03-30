# External Libraries (lib)

This directory contains local Java Archive (`.jar`) files required by the **MotorVCS** project. While the project primarily uses Maven for dependency management, certain specific versions or non-standard libraries are stored here for stability and offline access.

---

## Key Dependencies

Based on the project configuration, this directory includes:

1.  **jSerialComm**:
    - **Purpose**: Provides cross-platform serial port access.
    - **Importance**: The `ComManage.java` class relies on this library to enumerate and communicate with motor hardware via RS232/TTL.
2.  **JTS Core (LocationTech)**:
    - **Purpose**: High-level support for spatial topology and coordinate geometry.
    - **Importance**: Used for processing motor trajectory coordinates and complex spatial data analysis.

---

## Configuration Guide

To ensure the project compiles and runs correctly, you must add these JAR files to your IDE's **Build Path**.

### 1. IntelliJ IDEA Configuration
1.  Go to **File** -> **Project Structure**.
2.  Select **Modules** -> **Dependencies** tab.
3.  Click the **+** icon and navigate to this `lib/` folder, select all JARs, and set the scope to `Compile`.



### 2. Maven (pom.xml) Reference
If a library is not available in the Central Repository, it is referenced in the `pom.xml` using a system path:
```xml
<dependency>
    <groupId>com.custom</groupId>
    <artifactId>custom-lib</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/your-library.jar</systemPath>
</dependency>