// USER CODE BEGIN Header
// ******************************************************************************
// * @file    stm32f4xx_it.c
// * @brief   Interrupt Service Routines.
// ******************************************************************************
// USER CODE END Header

// Includes ------------------------------------------------------------------
#include "main.h"
#include "stm32f4xx_it.h"

// Private includes ----------------------------------------------------------
// USER CODE BEGIN Includes
#include "arm_math.h"
#include "filter.h"
#include "stdio.h"
// USER CODE END Includes

// Private define ------------------------------------------------------------
// USER CODE BEGIN PD
#define NZEROS 2
#define NPOLES 2
#define GAIN 0.33922  
// USER CODE END PD

// Private variables ---------------------------------------------------------
// USER CODE BEGIN PV
// Frequency measurement
uint32_t uiDutyCycle; // Duration (us)
uint32_t uiCycle;     // Period (us)
float32_t uiFrequency;// Frequency (Hz)

int flag_extension = 0; // Extension flag

uint32_t pos_time;      // High level duration
uint32_t cycle;         // Period (us)
uint32_t freq;          // Frequency
float pos_duty;         // Positive duty cycle

uint32_t count = 0, buffcount = 0;
uint32_t angletemp[51];      // History buffer for previous angles
int32_t avgspeedtemp[11] = 0; // History buffer for previous speeds
int32_t speedtemp = 0, angletemp1 = 0, angletemp2 = 0, speedtemp3 = 0, ADtemp = 0, average = 0, sum = 0;
static float32_t xv[NZEROS+1], yv[NPOLES+1]; // Filter buffers
uint32_t buffer_time[16];
int Fflag = 1;
int intter = 0, Sign = 1;
int tim7count = 0;
// USER CODE END PV

// Private function prototypes -----------------------------------------------
// USER CODE BEGIN PFP
static float32_t filter(int32_t nextValue){ // Second-order low-pass filter
	xv[0] = xv[1];
	xv[1] = xv[2]; 
    xv[2] = nextValue / GAIN;
	yv[0] = yv[1]; 
	yv[1] = yv[2]; 
    yv[2] = xv[0] + 2 * xv[1] + xv[2] - ( 0.9075 * yv[0]) - ( -0.5506 * yv[1]); // b2 b1 b0 a2 a1
    return yv[2];
}
// USER CODE END PFP

// USER CODE BEGIN 0
#include "UARTPro.h"
#include "gPara.h"
#include "ADC.h"
#include "motor.h"
#include "spi_TLE5012B.h"
#include "ControlLab.h"

uint16_t txTimer_cnt = 0;
uint32_t global_EncoderValue = 0;
uint16_t vol_flag = 1;              // Torque calibration flag
int32_t vol_temp;                   // Torque drift value
extern int32_t speed_temp1;
uint32_t cap_value1 = 0;            // Rising edge count
uint32_t cap_value2 = 0;            // Falling edge count (pulse width)
uint32_t cap_value3 = 0;            // Next rising edge count (period)

uint8_t cap_sta = 0;                // IC state: 0-incomplete, 1-calc
uint8_t cap_times = 0;              // Capture sequence index
// USER CODE END 0

// External variables --------------------------------------------------------
extern DMA_HandleTypeDef hdma_adc1;
extern DMA_HandleTypeDef hdma_adc2;
extern TIM_HandleTypeDef htim2;
extern TIM_HandleTypeDef htim3;
extern TIM_HandleTypeDef htim6;
extern TIM_HandleTypeDef htim7;
extern DMA_HandleTypeDef hdma_uart4_rx;
extern DMA_HandleTypeDef hdma_uart4_tx;
extern DMA_HandleTypeDef hdma_usart1_rx;
extern DMA_HandleTypeDef hdma_usart1_tx;
extern DMA_HandleTypeDef hdma_usart3_rx;
extern DMA_HandleTypeDef hdma_usart3_tx;
extern UART_HandleTypeDef huart4;
extern UART_HandleTypeDef huart1;
extern UART_HandleTypeDef huart3;
extern uint8_t StiffnessLock;

// USER CODE BEGIN EV
int fputc(int ch, FILE* fp) // Redirection for printf
{
	HAL_UART_Transmit(&huart4, (uint8_t *)&ch, 1, HAL_MAX_DELAY);
	return ch;
}

void HAL_TIM_IC_CaptureCallback(TIM_HandleTypeDef *htim)
{ 
	if(htim->Instance == htim2.Instance)
	{
		switch(htim->Channel) // Check which channel triggered the interrupt
		{
			case HAL_TIM_ACTIVE_CHANNEL_1:
				uiCycle = HAL_TIM_ReadCapturedValue(htim, TIM_CHANNEL_1);    // Period count
				break;
			case HAL_TIM_ACTIVE_CHANNEL_2:
				uiDutyCycle = HAL_TIM_ReadCapturedValue(htim, TIM_CHANNEL_2); // Duty cycle count
				break;
			default:break;
		}
	}
}
// USER CODE END EV

// Processor Interruption Handlers -------------------------------------------
void NMI_Handler(void) {}
void HardFault_Handler(void) { while (1) {} }
void MemManage_Handler(void) { while (1) {} }
void BusFault_Handler(void) { while (1) {} }
void UsageFault_Handler(void) { while (1) {} }
void SVC_Handler(void) {}
void DebugMon_Handler(void) {}
void PendSV_Handler(void) {}
void SysTick_Handler(void) { HAL_IncTick(); }

// Peripheral Interrupt Handlers ---------------------------------------------
void DMA1_Stream1_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_usart3_rx); }
void DMA1_Stream2_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_uart4_rx); }
void DMA1_Stream3_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_usart3_tx); }
void DMA1_Stream4_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_uart4_tx); }

void EXTI9_5_IRQHandler(void)
{
  HAL_GPIO_EXTI_IRQHandler(GPIO_PIN_6);
  HAL_GPIO_EXTI_IRQHandler(GPIO_PIN_7);
  HAL_GPIO_EXTI_IRQHandler(GPIO_PIN_8);
  // USER CODE BEGIN EXTI9_5_IRQn 1
	gc_Hall = (HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_6)<<2)|(HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_8)<<1)| HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_7);
	if(gc_prevHall == gc_Hall) return;
  // USER CODE END EXTI9_5_IRQn 1
}

void TIM2_IRQHandler(void) { HAL_TIM_IRQHandler(&htim2); uiFrequency = 10000.0f / (uiCycle+1); }

void TIM3_IRQHandler(void)
{
  HAL_TIM_IRQHandler(&htim3);
  // USER CODE BEGIN TIM3_IRQn 1
	USRT3_SendByte('s'); // Request data via RS485
	int stifftemp = 25;
	int32_t Mot_Speedtemp;
	if(StiffnessLock==0){
		if((gt_MInfo.NC - gt_MInfo.Mot_Cur) > stifftemp)
		{
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_10, 1);
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_11, 0);
		}
		else if(((gt_MInfo.NC - gt_MInfo.Mot_Cur) > -stifftemp) && ((gt_MInfo.NC - gt_MInfo.Mot_Cur) <= stifftemp))
		{
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_10, 0);
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_11, 0);
		}
		else{
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_10, 0);
				HAL_GPIO_WritePin(GPIOC, GPIO_PIN_11, 1);
		}
	}
    int i;
	for(i=50;i>0;i--){ // Update angle history buffer
		angletemp[i] = angletemp[i-1];
	}
	angletemp[0] = gi_EncoderValue; 
	gi_EncoderValue = ReadAngle(); // Get new value
	
	gi_EncoderSpeed = angletemp[0]- angletemp[50]; // Speed calc via displacement
	if(gi_EncoderSpeed>2000) gi_EncoderSpeed -=3600;
	else if(gi_EncoderSpeed<-2000) gi_EncoderSpeed +=3600;
		
	Mot_Speedtemp = gi_EncoderSpeed*0.5*0.883*28; // RPM
	gt_MInfo.Mot_Speed = Mot_Speedtemp/28*6;      // Convert to deg/s
	
	if(gc_MotorModeSelect == 1) // 1ms timer
	{
		if(is_Hall_or_Encoder == 1)
		{
			gc_Hall = (HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_6)<<2)|(HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_8)<<1)| HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_7);
			motor_start();
		}
		else if(is_Hall_or_Encoder == 2)
		{
			gc_Hall = (HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_6)<<2)|(HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_8)<<1)| HAL_GPIO_ReadPin(GPIOC,GPIO_PIN_7);
			motorEncoder_Start();
		}
	}
	else if(gc_MotorModeSelect == 6) // 1ms update, Braking/Const Speed
	{
		if(SWState == 1)
		{	
			if(Mot_Speedtemp < -20 && flag_extension == 1 ) flag_extension = 0;
			if(Mot_Speedtemp > 20 && flag_extension == 0 ) flag_extension = 1;

			if(flag_extension == 0){
				gt_MInfo.Sys_Cur = -60;
				Ctrl_Mode4_Pro(1998);
			}
			else{
				gt_MInfo.Sys_Cur = 60;
				speed_temp1 = Mot_Speedtemp;
				speed_temp=PID_Calc(&gt_Speed, gt_MInfo.Set_Speed, speed_temp1, 2000, 1);
				gi_DutyValue = (speed_temp);
				gt_MInfo.Sys_Vol = gi_DutyValue/20;
				Ctrl_Mode4_Pro(gi_DutyValue);
			}
		}
	}
	else { Ctrl_Mode4_Pro(1000); }
	
	gi_EncoderValue1 = ReadAngle();         // Leg encoder
	gt_MInfo.Angle = 2100-gi_EncoderValue1; // Test setup
	
	global_EncoderValue = gi_EncoderValue; 
	gc_ADCSamplingFlag = 1;
	if(++txTimer_cnt >= 10)
	{
		txTimer_cnt = 0;
		uart_sendDataFun();
	}
  // USER CODE END TIM3_IRQn 1
}

void USART1_IRQHandler(void) { UART1_DMA_REVC(); HAL_UART_IRQHandler(&huart1); }
void USART3_IRQHandler(void) { UART3_DMA_REVC(); HAL_UART_IRQHandler(&huart3); }
void UART4_IRQHandler(void) { UART4_DMA_REVC(); HAL_UART_IRQHandler(&huart4); }

void TIM7_IRQHandler(void)
{
  HAL_TIM_IRQHandler(&htim7);
  // USER CODE BEGIN TIM7_IRQn 1 
  angletemp1 = angletemp2;
  angletemp2 = ReadAngle();
  if(intter == buffcount) uiFrequency = 0;
  buffcount = intter;
  // USER CODE END TIM7_IRQn 1
}

void DMA2_Stream0_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_adc1); }
void DMA2_Stream2_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_usart1_rx); }
void DMA2_Stream3_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_adc2); }
void DMA2_Stream7_IRQHandler(void) { HAL_DMA_IRQHandler(&hdma_usart1_tx); }