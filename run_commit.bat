@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

rem java -jar schedsim2016.jar calb
rem java -jar schedsim2016.jar idbo
rem java -jar schedsim2016.jar nvlv
rem java -jar schedsim2016.jar cave

rename 2016calb_predictions.csv 2016calb_predictions.html
rename 2016idbo_predictions.csv 2016idbo_predictions.html
rename 2016nvlv_predictions.csv 2016nvlv_predictions.html
rename 2016cave_predictions.csv 2016cave_predictions.html

%GIT_PATH% add -A
%GIT_PATH% commit -am "%date% %time% JPG"
%GIT_PATH% pull %BRANCH%
%GIT_PATH% push %BRANCH%
