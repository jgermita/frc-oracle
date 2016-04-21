@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

java -jar schedsim2016.jar calb
java -jar schedsim2016.jar cave
java -jar schedsim2016.jar idbo
java -jar schedsim2016.jar nvlv

java -jar schedsim2016.jar pncmp
java -jar schedsim2016.jar incmp
java -jar schedsim2016.jar micmp
java -jar schedsim2016.jar gacmp
java -jar schedsim2016.jar nccmp
java -jar schedsim2016.jar chcmp
java -jar schedsim2016.jar mrcmp
java -jar schedsim2016.jar necmp

java -jar schedsim2016.jar cur
java -jar schedsim2016.jar arc
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
