# Instructions to run on data
## Environment
- **Software**: MATLAB R2021b  

## Step 1: Open "unpack_data_frame.m"
## Step 2: Click on "run"

## Expected output:

Data unpacking completed. The parsed result is:
    header: [2×2 char]
       key: 0
      mode: 6
     data1: 50
     data2: 60
     data3: -768
     data4: 0
     data5: 0
     data6: 2500
     data7: 54
     data8: 835
     data9: 4246
    data10: 280
    data11: 2388
    status: 0
      tail: [2×1 char]

## Expected run time for the demo : less than 1 min
- **Operating System**: Windows 11  
- **CPU**: Intel i7-14700HX  
- **GPU**: NVIDIA RTX 4070 Laptop  

# Instructions for use

## Environment
- **Software**: MATLAB R2021b  


## How to run software on your data

### First, connect the robot to the app via Bluetooth to acquire the raw data (.txt file). An example of a raw data frame is as follows:
Data Frame Example:	bb440006320000003c00000000fdffff0000000000000000c4090000360000004303000096100000180100005409000000000d0a		


| Byte offset | Length (Bytes) | Data | Description |
| :---: | :---: | :---: | :---: |
| 0-1 | 2 | bb 44 | Header |
| 2 | 1 | 00 | Key |
| 3 | 1 | 06 | Mode |
| 4-7 | 4 | 32 00 00 00 | Data1 |
| 8-11 | 4 | 3c 00 00 00 | Data2 |
| 12-15 | 4 | 00 fd ff ff | Data3 |
| 16-19 | 4 | 00 00 00 00 | Data4 |
| 20-23 | 4 | 00 00 00 00 | Data5 |
| 24-27 | 4 | c4 09 00 00 | Data6 |
| 28-31 | 4 | 36 00 00 00 | Data7 |
| 32-35 | 4 | 43 03 00 00 | Data8 |
| 36-39 | 4 | 96 10 00 00 | Data9 |
| 40-43 | 4 | 18 01 00 00 | Data10 |
| 44-47 | 4 | 54 09 00 00 | Data11 |
| 48-49 | 2 | 00 00 | Status |
| 50-51 | 2 | 0d 0a | Tail |


### Second, open the MATLAB file 'unpack_data_frame.m'. Locate the filename variable, change it to the exact name of your new data file, and run the program.

