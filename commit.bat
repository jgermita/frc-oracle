@echo off
set GIT_PATH="C:\Program Files\Git\bin\git.exe"
set BRANCH = "origin gh-pages"

%GIT_PATH% add -A
%GIT_PATH% commit -am "%date% %time% JPG"
%GIT_PATH% pull %BRANCH%
%GIT_PATH% push %BRANCH%
