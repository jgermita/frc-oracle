@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

color 1f
java -jar schedsim2016_fromfile.jar cur
color 2f
java -jar schedsim2016_fromfile.jar arc
color 3f
java -jar schedsim2016_fromfile.jar gal
color 4f
java -jar schedsim2016_fromfile.jar new
color 5f
java -jar schedsim2016_fromfile.jar cars
color 6f
java -jar schedsim2016_fromfile.jar carv
color 7f
java -jar schedsim2016_fromfile.jar hop
color 8f
java -jar schedsim2016_fromfile.jar tes
color 9f


%GIT_PATH% add -A
%GIT_PATH% commit -am "%date% %time% - From File JPG"
%GIT_PATH% pull %BRANCH%
%GIT_PATH% push %BRANCH%
