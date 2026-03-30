// USER CODE BEGIN Header
// ******************************************************************************
// * @file           : main.c
// * @brief          : Main program body
// ******************************************************************************
// USER CODE END Header

#include "main.h"

// USER CODE BEGIN Includes
#include "UARTPro.h"
#include "ADC.h"
#include "motor.h"
#include "spi_TLE5012B.h"
#include "ControlLab.h" 
#define GPARA_GLOBALS 1
#include "gPara.h"
// USER CODE END Includes

// Private variables ---------------------------------------------------------
ADC_HandleTypeDef hadc1, hadc2;
DMA_HandleTypeDef hdma_adc1, hdma_adc2;
I2C_HandleTypeDef hi2c1;
TIM_HandleTypeDef htim1, htim2, htim3, htim6, htim7, htim8, htim13;
UART_HandleTypeDef huart4, huart1, huart3;
DMA_HandleTypeDef hdma_uart4_rx, hdma_uart4_tx, hdma_usart1_rx, hdma_usart1_tx, hdma_usart3_rx, hdma_usart3_tx;

// USER CODE BEGIN 0
// UART4 receive handler
uint8_t rxBuf[6];
void UR4_Receive_Info()
{
	HAL_UART_Receive(&huart4,rxBuf,sizeof (rxBuf),100); // Receive data
	gt_MInfo.Mot_Cur = (uint32_t)((rxBuf[2] <<8) + rxBuf[3]);
	gt_MInfo.Mot_Vol = (int32_t)((rxBuf[4] <<8) + rxBuf[5]);
}

void delay_us1(uint32_t u) // 1us software delay
{
	for(uint32_t i=0;i<u;i++)
		for(uint32_t j=0;j<35;j++);
}
// USER CODE END 0

int main(void)
{
  HAL_Init();
  SystemClock_Config();

  // Initialize peripherals
  MX_GPIO_Init(); MX_DMA_Init(); MX_ADC1_Init(); MX_I2C1_Init(); MX_TIM1_Init();
  MX_USART3_UART_Init(); MX_USART1_UART_Init(); MX_TIM3_Init(); MX_TIM6_Init();
  MX_TIM7_Init(); MX_ADC2_Init(); MX_TIM2_Init(); MX_TIM8_Init(); MX_TIM13_Init();
  MX_NVIC_Init();

  // USER CODE BEGIN 2
	HAL_Delay(100);
	MX_I2C1_eeprom_Init();
	PWRLED1_ON; LED1_ON;
	DataInit(); SPI5012B_Init(); _SPI5012B_Init(); DRV_Init();
	UART1_IT_Init(); UART3_IT_Init(); UART4_IT_Init();
	ADC_data_Init(); usr_Timer1_Init();
	HAL_Delay(100);
	gt_MInfo.Set_Speed = 3000; gt_MInfo.Mot_Cur = 2500; gi_DutyValue = 1000;
	
	HAL_TIM_PWM_Start(&htim13, TIM_CHANNEL_1);   // TIM13 Channel 1 PWM start
	HAL_TIM_IC_Start_IT(&htim2,TIM_CHANNEL_1);
	HAL_TIM_IC_Start_IT(&htim2,TIM_CHANNEL_2);
	HAL_TIM_Base_Start_IT(&htim7); // Start TIM7 IT
	HAL_TIM_Base_Start_IT(&htim3); // Start TIM3 IT
	HAL_TIM_Base_Start_IT(&htim6); // Start TIM6 IT
  // USER CODE END 2

  while (1)
  {
    // USER CODE BEGIN WHILE
		if(gc_ADCSamplingFlag == 1)
		{
			gc_ADCSamplingFlag = 0;
			ADC_SamplingPro();
			SWState = SWInput;
			gc_DRVFault = DRV_nFAULT;
		}
		if(gt_MInfo.mode==5)  // Passive mode: Dynamic Braking
		{
			Ctrl_Mode3_Pro(gi_DutyValue);
		}
		if(gc_CalZeroFlag == 1)
		{
			gc_CalZeroFlag = 0;
			CalZeroFun();
		}
		if(gc_DRVFault == 0) // Driver error handling
		{
			gc_DRVFaultData[0] = DRV_ReadData(0);
			gc_DRVFaultData[1] = DRV_ReadData(1);
			HAL_Delay(10);
		}
    // USER CODE END WHILE
  }
}

static void MX_TIM7_Init(void)
{
  // 84M main clock, 8400 prescaler = 0.0001s resolution. Max 0.655s.
  htim7.Instance = TIM7;
  htim7.Init.Prescaler = 840-1; // 10kHz counter clock
  htim7.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim7.Init.Period = 10000-1;  // 10Hz interrupt frequency
  htim7.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_Base_Init(&htim7) != HAL_OK) { Error_Handler(); }
}