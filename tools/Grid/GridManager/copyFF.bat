for /f "tokens=*" %%A in (servers.txt) do (
  net use %%A /USER:Administrator q114winter
  xcopy /i/s/y C:\Firefox\24 %%A\c$\Firefox\24
)
pause