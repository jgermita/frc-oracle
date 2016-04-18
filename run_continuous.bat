@echo off
:loop
color cf
run_commit.bat
color 2f
echo Waiting 60 seconds to update!
sleep 60
echo Updating...
GOTO loop