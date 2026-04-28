# RideTrust Econometrics Analysis

## Project Overview
This project contains an econometrics analysis focused on **RideTrust**, examining the factors that influence consumer trust, willingness to pay a premium ("Trust Premium"), and the likelihood of adopting the service ("Adoption Propensity"). 

The study leverages survey data and applies statistical modeling—specifically Ordinary Least Squares (OLS) and Logistic (Logit) regressions—to quantify how variables like safety fears, demographic factors (gender, income), and service guarantees (verified drivers, zero cancellations) impact user behavior and intent.

## Repository Structure
- **`#PYTHONCODE#_Econometrics_Final_Project.ipynb`**: The core Jupyter Notebook containing all data preparation, cleaning, transformations, and regression models.
- **`#INPUTFILE#_RideTrust_Survey_Data.csv`**: The raw survey data used as the primary input for the analysis.
- **`#PYTHONPROCESSED#_RideTrust_Processed_Data_FINAL.xlsx`**: The final processed dataset generated after cleaning and feature engineering in Python.
- **`Econometrics Final Project.pdf`**: The final comprehensive report documenting the methodology, empirical findings, and econometric interpretations.

## Methodology & Models
The analysis is driven by two primary econometric models implemented using the `statsmodels` library in Python:

### Model 1: OLS Regression (Estimating the Trust Premium)
- **Objective**: To estimate the premium users are willing to pay for the RideTrust service based on their safety perceptions and demographics.
- **Dependent Variable**: `Log_WTP` (Logarithmic transformation of the maximum Willingness to Pay, derived from `Q17_WTP_Max_Price`).
- **Independent Variables**: 
  - `Q7_Safety_Fear` (Continuous/Ordinal rating of safety concern)
  - `Female_Dummy` (Binary: 1 if Female, 0 if Male)
  - `Income Dummies` (Categorical bins transformed for regression readability, e.g., `< 5 LPA`, `15-25 LPA`)
- **Method**: Fits a linear model with an interaction term between safety fear and gender (`Q7_Safety_Fear * Female_Dummy`) to observe varied effects across demographics.

### Model 2: Logit Regression (Estimating Adoption Propensity)
- **Objective**: To predict the likelihood of high adoption intent based on specific service features.
- **Dependent Variable**: `High_Intent_7` (Binary classification: 1 if `Q20_Download_Intent` is exactly 7, 0 otherwise. This strict classification was chosen to avoid perfect separation in the model).
- **Independent Variables**:
  - `Q12_Verified` (Importance of verified drivers)
  - `Q11_Zero_Cancel` (Importance of a zero-cancellation policy)
  - `Trips_Per_Month` (Frequency of ride-hailing usage)
- **Method**: Employs Maximum Likelihood Estimation (MLE) to fit a logistic curve estimating adoption probability.

## Setup & Installation
To run the Jupyter Notebook locally and reproduce the results, ensure you have Python installed along with the required scientific libraries.

1. **Clone/Download the Repository** to your local machine.
2. **Install Dependencies**:
   You will need the following Python packages. You can install them via pip:
   ```bash
   pip install pandas numpy statsmodels seaborn matplotlib jupyter openpyxl
   ```
3. **Run the Notebook**:
   Navigate to the project directory and launch Jupyter:
   ```bash
   jupyter notebook "#PYTHONCODE#_Econometrics_Final_Project.ipynb"
   ```
4. **Execution**:
   Run the cells sequentially. The notebook will automatically read the raw `.csv` survey data, clean the variables (e.g., parsing income brackets to valid dummy identifiers), fit the statsmodels formulas, and output the OLS and Logit summary tables.

## Key Statistical Findings (Preview)
- **Trust Premium (OLS)**: The OLS model achieved an R-squared of ~0.746, indicating a strong fit. Factors like `Q7_Safety_Fear` and upper-tier income brackets (`15-25 LPA`, `25+ LPA`) showed statistically significant positive coefficients on Willingness to Pay. The base formula clearly highlights how safety fears translate into a monetary trust premium.
- **Adoption Intent (Logit)**: The logistic regression confirms that features like `Q12_Verified` (p=0.020) and `Q11_Zero_Cancel` (p=0.013) are statistically significant drivers, measurably increasing the odds of strong download intent among surveyed users.
