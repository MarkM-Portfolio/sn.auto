SET newPass=q114winter
for /f "tokens=*" %%A in (servers.txt) do (
 net use %%A /user:administrator %newPass%
 C:\PSTools\PSTools\PsExec.exe %%A -u Administrator -p %newPass% -w "C:\GridManager" cmd /c "echo . | powershell -executionpolicy ByPass -File taskPassChange.ps1 %newPass%"
)
pause