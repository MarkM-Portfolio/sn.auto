SET password=q413autumn
for /f "tokens=*" %%A in (servers.txt) do (
  net use %%A /user:administrator %password%
  C:\PSTools\PSTools\PsExec.exe %%A -u Administrator -p %password% -e cmd /c "java -jar \\lcgrid1\Share\bsoAuth\bsoAuth.jar"
)  
pause