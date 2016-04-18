@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

:loop
color cf

rem java -jar schedsim2016.jar calb
java -jar schedsim2016.jar idbo
rem java -jar schedsim2016.jar nvlv
rem java -jar schedsim2016.jar cave

%GIT_PATH% add -A
%GIT_PATH% commit -am "%date% %time% JPG"
%GIT_PATH% pull %BRANCH%
%GIT_PATH% push %BRANCH%

color 2f
echo Waiting 10 minutes seconds to update!
sleep 600
echo Updating...
GOTO loop