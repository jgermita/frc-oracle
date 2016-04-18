@echo off
cd "C:\Users\jeremy\Documents\GitHub\frc-oracle"
REM run.bat
echo committing

git add *
git commit -m "%date% %time% JPG"
echo pushing
git push origin gh-pages
echo done. 