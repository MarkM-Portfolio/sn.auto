EXIT
SET oldPass=q413autumn
SET newPass=q114winter
for /f "tokens=*" %%A in (servers.txt) do (
 net use %%A /user:administrator %oldPass%
 C:\PSTools\PSTools\PsExec.exe %%A -u Administrator -p %oldPass% -w "C:\Users\Administrator\Desktop\LCRFT04_Hubbed_Grid" cmd /c "changeTxtPassword.bat %newPass%"
 C:\PSTools\PSTools\pspasswd.exe %%A administrator %newPass%
 C:\PSTools\PSTools\PsExec.exe %%A -u Administrator -p %newPass% -w "C:\GridManager" cmd /c "echo . | powershell -executionpolicy ByPass -File taskPassChange.ps1 %newPass%"
)
pause