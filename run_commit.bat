@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

java -jar schedsim2016.jar arc
java -jar schedsim2016.jar cur
java -jar schedsim2016.jar gal
java -jar schedsim2016.jar new
java -jar schedsim2016.jar cars
java -jar schedsim2016.jar carv
java -jar schedsim2016.jar hop
java -jar schedsim2016.jar tes

%GIT_PATH% add -A
%GIT_PATH% commit -am "%date% %time% JPG"
%GIT_PATH% pull %BRANCH%
%GIT_PATH% push %BRANCH%
