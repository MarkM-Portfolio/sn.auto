Set newPass=%1%
(
Set /p line1=
Set /p line2=
Set /p line3=
Set /p line4=
Set /p line5=
Set /p line6=
Set /p line7=
Set /p line8=
Set /p line9=
Set /p line10=
Set /p line11=
Set /p line12=
Set /p line13=
Set /p line14=
Set /p line15=
Set /p line16=
Set /p line17=
Set /p line18=
Set /p line19=
)<GridNode_v7.bat
echo %line1%>GridNode_v7.bat
echo %line2%>>GridNode_v7.bat
echo.>>GridNode_v7.bat
echo %line4%>>GridNode_v7.bat
echo %line5%>>GridNode_v7.bat
echo %line6%>>GridNode_v7.bat
echo.>>GridNode_v7.bat
echo %line8%>>GridNode_v7.bat
echo net use T: \\lcrft04.cnx.cwp.pnp-hcl.com\Temp\SeleniumFolder /USER:Administrator %newPass%>>GridNode_v7.bat
echo.>>GridNode_v7.bat
echo %line11%>>GridNode_v7.bat
echo %line12%>>GridNode_v7.bat
echo %line13%>>GridNode_v7.bat
echo %line14%>>GridNode_v7.bat
echo %line15%>>GridNode_v7.bat
echo.>>GridNode_v7.bat
echo %line17%>>GridNode_v7.bat
echo %line18%>>GridNode_v7.bat
echo %line19%>>GridNode_v7.bat