EXIT
SET pass=q114winter
for /f "tokens=*" %%A in (servers.txt) do (
  C:\PSTools\PSTools\psshutdown.exe %%A -u Administrator -p %pass% -r
)
pause