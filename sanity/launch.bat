echo @off
where python
if errorlevel 1 (
	echo Python is either not installed or is not in the path. Please fix this before continuing.
	return /b 1
)
python cmd_frontend.py -c conf.json -l output.log