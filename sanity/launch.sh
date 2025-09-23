which python
if [$?]
	then
		echo "Python is either not installed or is not in the path. Please fix this before continuing."
		exit
fi
python cmd_frontend.py -c conf.json -l output.log
exit