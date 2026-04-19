# Firmware Architecture Documentation
**This project was created by Yanggang Feng's lab. We thank all lab members for their contributions to this project.**
This document describes the structure and functionality of the core firmware files, including system initialization, motor control, and real-time interrupt handling.

- **Development Environment**:

- IDE: Keil MDK-ARM V5
- Device Family Pack: STM32F4xx_DFP

---

## 1. `main.c` (Main Program & System Scheduling)

This file is primarily responsible for **low-level initialization** and **polling of non-real-time, low-priority tasks**.

### Main Functions

---

#### `main()`
- **Function**: Program entry point and main loop

##### Initialization Phase:
- Initialize:
  - HAL library
  - System clock
  - Peripherals (GPIO, DMA, ADC, I2C, TIM, UART, etc.)
- Initialize hardware:
  - Driver chip (`DRV_Init`)
  - Encoder (`SPI5012B_Init`)
  - EEPROM
- Start timers and interrupts

##### Main Loop `while(1)`:
Handles various asynchronous flags:

- ADC sampling complete flag (`gc_ADCSamplingFlag`)
  - Data processing
  - Fault state reset

- Passive braking mode (`gt_MInfo.mode`)
  - Execute dynamic braking

- Zero calibration flag (`gc_CalZeroFlag`)
  - Perform zero calibration

- Driver monitoring:
  - Read driver registers (`DRV_ReadData`)
  - Detect fault signal (`nFAULT`)

---

#### `UR4_Receive_Info()`
- **Function**: UART4 receive handler  
- **Details**:
  - Uses `HAL_UART_Receive` in blocking mode to receive 6 bytes
  - Parses data into:
    - Motor current (`Mot_Cur`)
    - Bus voltage (`Mot_Vol`)

---

#### `MX_TIM7_Init()`
- **Function**: Timer 7 initialization  
- **Details**:
  - Used for low-frequency monitoring tasks

---

#### `delay_us1(uint32_t u)`
- **Function**: Microsecond-level delay function  
- **Details**:
  - Implemented using empty loops
  - Suitable for short blocking delays

---

## 2. `motor.c` (Motor Driver & Low-Level Control)

This file implements **direct motor hardware control**, including PWM output and commutation logic.

---

#### `usr_Timer1_Init()`
- **Function**: Initialize TIM1 (advanced timer)  
- **Details**:
  - Outputs three-phase PWM
  - Initializes duty cycle registers (CCR1/2/3)

---

#### `DRV_Init()`
- **Function**: Driver chip initialization (DRV8323)  
- **Details**:
  - Enable driver via GPIO
  - Configure registers via SPI
  - Set:
    - Gain
    - Dead time
    - Protection parameters

---

#### `PWM_T_Output(uint8_t dir, uint8_t step, uint32_t duty)`
- **Function**: Core six-step commutation function  
- **Details**:
  - Inputs:
    - Direction (`dir`)
    - Step (`step`)
    - Duty cycle (`duty`)
  - Dynamically switches:
    - PWM channels
    - Low-side GPIOs
  - Implements phase current commutation

---

#### `motor_start()`
- **Function**: Motor startup wrapper  
- **Details**:
  - Reads global direction, Hall state, and duty cycle
  - Calls `PWM_T_Output`

---

#### `Ctrl_Mode4_Pro(uint32_t duty)`
- **Function**: Passive mode / dynamic braking  
- **Details**:
  - Controls:
    - High-side off
    - Low-side PWM conduction
  - Creates back-EMF short circuit for damping braking

---

#### `CalZeroFun()`
- **Function**: Encoder zero position calibration  
- **Details**:
  - Applies fixed PWM to lock rotor at a specific electrical angle
  - Reads mechanical angle
  - Calculates offset
  - Writes to EEPROM via I2C (address `0x24`)

---

## 3. `stm32f4xx_it.c` (Interrupts & Real-Time Control)

This file handles **all interrupt routines** and **high real-time control algorithms**.

---

### Main Functions

#### `filter(int32_t nextValue)`
- **Function**: Second-order IIR low-pass filter  


- **Purpose**:
  - Smooth noisy signals

---

#### `fputc(int ch, FILE* fp)`
- **Function**: Redirect `printf`  
- **Details**:
  - Outputs characters via UART4
  - Used for serial debugging

---

#### `HAL_TIM_IC_CaptureCallback()`
- **Function**: Input capture interrupt callback  
- **Details**:
  - Records:
    - Period (`uiCycle`)
    - High-level duration (`uiDutyCycle`)

---

#### `TIM3_IRQHandler()` Core Control Loop
- **Function**: 1 ms real-time control loop (1 kHz)

##### Core Tasks:

- **Stiffness control**

- **Speed calculation**

- **Mode control (`gc_MotorModeSelect`)**

- **Data interaction**
  - Read encoder
  - ADC sampling
  - UART data transmission

---

#### `TIM7_IRQHandler()`
- **Function**: Low-frequency monitoring  
- **Details**:
  - Detects stall or stop conditions
---

#### DMA & UART Interrupts

- **Functions**:
  - `DMAx_Streamx_IRQHandler()`
  - `USARTx_IRQHandler()`

- **Details**:
  - Call HAL handlers:
    - `HAL_DMA_IRQHandler`
    - `HAL_UART_IRQHandler`
  - Handle:
    - Data transfer completion
    - Serial communication
  - Clear flags and trigger callbacks

---

## Summary

| Module | Responsibility |
|--------|---------------|
| `main.c` | Initialization & low-priority tasks |
| `motor.c` | Motor driving & PWM control |
| `stm32f4xx_it.c` | Real-time control & interrupt handling |

---
