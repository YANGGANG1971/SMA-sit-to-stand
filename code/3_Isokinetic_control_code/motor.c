// ******************************************************************************
// * File Name          : motor.c
// * Description        : Motor Control Program
// ******************************************************************************
#include "main.h"
#include "stm32f4xx_hal.h"
#include "gPara.h"
#include "string.h"
#include "math.h"
#include "UARTPro.h"
#include "ADC.h"
#define MOTOR_GLOBALS 1
#include "motor.h"

#define BLDC_TIM_PERIOD 3360
uint8_t hall[6] = {1,3,2,6,4,5};

// Timer mode PWM output initialization
void usr_Timer1_Init(void)
{
  // ... TIM1 configuration logic ...
  HAL_TIM_MspPostInit(&htim1);
	HAL_GPIO_WritePin(GPIOA,GPIO_PIN_7,0);
	HAL_GPIO_WritePin(GPIOB,GPIO_PIN_0,0);
	HAL_GPIO_WritePin(GPIOB,GPIO_PIN_1,0);
	
	HAL_TIM_PWM_Start(&htim1,TIM_CHANNEL_1); // Start Channel 1 PWM output
	TIM1->CCR1=BLDC_TIM_PERIOD;              // Set duty cycle
	TIM1->CCR2=BLDC_TIM_PERIOD; 
	TIM1->CCR3=BLDC_TIM_PERIOD; 
}

void Delayus(uint32_t u) { for(uint32_t i=0;i<u;i++) for(uint32_t j=0;j<80;j++); }

void DRV_Init(void) // Driver init
{
	motor_enable(0); DRVCS_ON; DRVCLK_OFF; DRVDI_0;
	HAL_Delay(100);
	DRV_WriteDta(3,0x3FF); // Unlock
	HAL_Delay(10);
    // ... further register writes ...
}

// PWM Trapezoidal output handler
void PWM_T_Output(uint8_t dir,uint8_t step,uint32_t duty)
{
	if(dir == 1)
	{
		switch(step)
		{
			case 5: // B+ A-
				HAL_GPIO_WritePin(GPIOB,GPIO_PIN_1,0); TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_3, 0);
				HAL_GPIO_WritePin(GPIOB,GPIO_PIN_0,1); TIM1->CCR2=BLDC_TIM_PERIOD; TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_2, 1);
				HAL_GPIO_WritePin(GPIOA,GPIO_PIN_7,1); TIM1->CCR1=BLDC_TIM_PERIOD*duty/4200; TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_1, 1);
				break;
            // ... Logic for other steps follow same pattern
		}
	}
}

void motor_start(void) { PWM_T_Output(gc_MDir,gc_Hall,gi_DutyValue); }

void Ctrl_Mode4_Pro(uint32_t duty)
{
	HAL_GPIO_WritePin(GPIOA,GPIO_PIN_7,1);
	HAL_GPIO_WritePin(GPIOB,GPIO_PIN_0,1);
	HAL_GPIO_WritePin(GPIOB,GPIO_PIN_1,1);
	TIM1->CCR1=4199*duty/2000;
	TIM1->CCR2=4199*duty/2000;
	TIM1->CCR3=4199*duty/2000;
	TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_1, TIM_CCx_ENABLE);
	TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_2, TIM_CCx_ENABLE);
	TIM_CCxChannelCmd(htim1.Instance, TIM_CHANNEL_3, TIM_CCx_ENABLE);
}
// Zero point calibration logic
void CalZeroFun(void)
{
	if(is_Hall_or_Encoder == 2)
	{
		usr_Timer1_Init(); gi_ZeroValue = gi_EncoderValue;
		for(uint8_t i=0;i<6;i++) // Search for Hall value that triggers movement
		{
			PWM_T_Output(gc_MDir,hall[i],3700); HAL_Delay(1500);
			PWM_T_Output(1,0,4199); HAL_Delay(500);
			if(abs((int)gi_ZeroValue - (int)gi_EncoderValue) > 10)
			{
                // ... Zero calculation logic ...
				DataSave[0] = gi_ZeroValue; DataSave[4] = gc_ZeroHall;
				I2C_EEPROM_WriteBuffer(0x24,DataSave,5); // Save to EEPROM
				break;
			}
		}
	}
}