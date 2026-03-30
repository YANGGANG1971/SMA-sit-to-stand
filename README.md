# Source Data for "Spinal neuromotor rehabilitation using a portable isokinetic training robot"

## 1. code
1.1 mobile app
1.2 Host PC
1.3 C language
1.4 Demo for plotting

## 2. data
This repository contains the underlying source data for the main figures and extended data figures presented in the manuscript titled **"Spinal neuromotor rehabilitation using a portable isokinetic training robot"**. 

The dataset includes clinical outcomes (e.g., sit-to-stand initial knee angles), biomechanical measurements (e.g., peak torque, range of motion, mechanical work), physiological muscle morphology parameters derived from MRI (ACSA, muscle volume, PCSA), electrophysiological metrics (femoral nerve conduction/CMAP), and surface electromyography (sEMG) data from Spinal Muscular Atrophy (SMA) type II pediatric patients who underwent a 6-week intervention with a portable isokinetic training robot.

### 2.1 File Format and Structure
All data are provided in comma-separated values (`.csv`) format for universal compatibility. The files are exported from the original master spreadsheet workbooks (`MainFig_Data.xlsx` and `ExtendDataFig_Data.xlsx`). 
The naming convention strictly follows the figure panels in the manuscript to facilitate easy cross-referencing.

### 2.2 Main Figures Data
* **`MainFig_Data.xlsx - Fig1.csv`**: Summary data comparing neuromuscular recovery (knee extension strength and ROM improvements) with existing rehabilitation strategies in the literature.
* **`MainFig_Data.xlsx - Fig2b.csv`**: Quadriceps muscle activation (sEMG) data during isotonic, isometric, and isokinetic knee extension.
* **`MainFig_Data.xlsx - Fig2c.csv`**: Representative trajectories of knee velocity, knee angle, and extension torque recorded during isokinetic training.
* **`MainFig_Data.xlsx - Fig2f.csv`**: Peak torque data generated at different stiffness settings, including the fourth-order polynomial fit used to determine the optimal stiffness for the variable stiffness mechanism.
* **`MainFig_Data.xlsx - Fig3a.csv` / `Fig3b.csv` / `Fig3c.csv`**: Longitudinal progression data of peak torque (3a), ROM (3b), and per movement work (3c) over 30 high-intensity training trials, including comparative biomechanical profiles between the initial (1st) and final (30th) trials.
* **`MainFig_Data.xlsx - Fig3e-g.csv`**: Statistical summaries of biomechanical improvements in peak torque (e), ROM (f), and work (g) across all participants.
* **`MainFig_Data.xlsx - Fig3h.csv`**: Detailed improvements in peak torque for both left and right legs for each participant across pre- and post-intervention.
* **`MainFig_Data.xlsx - Fig4b.csv`**: Quantitative changes in Anatomical Cross-Sectional Area (ACSA) of the left and right quadriceps muscles.
* **`MainFig_Data.xlsx - Fig4d.csv`**: Changes in quadriceps muscle volume of left and right legs derived from 3D MRI reconstruction.
* **`MainFig_Data.xlsx - Fig4g.csv`**: Changes in estimated Physiological Cross-Sectional Area (PCSA) of the left and right quadriceps muscles.
* **`MainFig_Data.xlsx - Fig5b.csv`**: Enhancement of femoral nerve conduction, quantified by Compound Muscle Action Potential (CMAP) amplitude from pre- to post-intervention.
* **`MainFig_Data.xlsx - Fig5c.csv`**: Data evaluating the enhancement of inter-limb coordination.
* **`MainFig_Data.xlsx - Fig6a-c.csv`**: Data assessing the retention of training-induced improvements in knee functional metrics (torque, angle, work) during follow-up periods.
* **`MainFig_Data.xlsx - Fig6d-f.csv`**: Data assessing the retention of improvements in muscle morphology (ACSA, volume, PCSA).
* **`MainFig_Data.xlsx - Fig6g.csv`**: Data assessing the retention of improvements in femoral nerve conduction (CMAP).
* **`MainFig_Data.xlsx - Fig6h.csv`**: Changes and retention of the lowest sit-to-stand initial knee angle across all study phases (Pre, Post, Follow-up 1, Follow-up 2).

### 2.3 Extended Data Figures Data
* **`ExtendDataFig_Data.xlsx - Extended Data Fig. 1a.csv`**: Increases in optimal stiffness from pre- to post-intervention.
* **`ExtendDataFig_Data.xlsx - Extended Data Fig. 1b.csv` / `Fig. 1c.csv`**: Increases in peak torque and ROM, including their relationship with respect to optimal stiffness changes.
* **`ExtendDataFig_Data.xlsx - Extended Data Fig. 3a.csv` / `Fig. 3b.csv`**: Time-series comparisons of isokinetic knee extension torque and sEMG amplitude (envelopes) between a representative healthy subject and an SMA type II participant (Pre and Post) for right (3a) and left (3b) legs.
* **`ExtendDataFig_Data.xlsx - Extended Data Fig. 4b.csv`**: Underlying recorded data corresponding to the graphical user interface during gamified isokinetic knee extension training.

### 2.4 Data Processing and Notes
* **Missing Values:** Cells left blank or marked with "N/A" or "/" indicate data points that were not applicable or not recorded for that specific trial/subject.
* **Units:** All units are explicitly stated in the headers of each `.csv` file (e.g., Torque in `Nm` or `Nm/kgm`, Angle in `°`, Area in `cm²` or `mm²/kgm`, CMAP in `mV`).
* **Normalization:** As noted in the manuscript (Fig. 6), relevant biomechanical and morphological indicators (peak torque, work, ACSA, muscle volume, and PCSA) were normalized by the product of each subject’s height and weight to account for growth and developmental factors. Normalized units are reflected in the respective data columns.

## 3. Contact Information
For any questions regarding the dataset or code availability, please contact the corresponding author:
**Yanggang Feng** School of Mechanical Engineering and Automation, Beihang University, Beijing, China.  
Email: yanggangfeng@buaa.edu.cn
