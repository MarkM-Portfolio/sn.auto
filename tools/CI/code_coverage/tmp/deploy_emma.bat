@echo off

if "%1"=="" goto usage
if "%2"=="" goto usage
if "%3"=="" goto usage
if "%4"=="" goto usage
if "%5"=="" goto usage
goto begin

:usage
    echo Usage: %0 ^<hostname^> ^<username^> ^<password^> ^<remote script dir^> ^<remote Emma home dir^>
    exit /B 1

:begin
set HOSTNAME=%1
set USERNAME=%2
set PASSWORD=%3
set REMOTE_SCRIPT_DIR=%4
set REMOTE_EMMA_HOME_DIR=%5

set FILENAME=instrument_for_code_coverage.sh
echo Copying %FILENAME% to %USERNAME%@%HOSTNAME%:/%REMOTE_SCRIPT_DIR%...
pscp -l %USERNAME% -pw %PASSWORD% %FILENAME% %USERNAME%@%HOSTNAME%:%REMOTE_SCRIPT_DIR%
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

echo Converting to unix line endings for %REMOTE_SCRIPT_DIR%/%FILENAME% on %HOSTNAME%....
plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% dos2unix %REMOTE_SCRIPT_DIR%/%FILENAME%
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

echo Adding execute permissions for %REMOTE_SCRIPT_DIR%/%FILENAME% on %HOSTNAME%....
plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% chmod +x %REMOTE_SCRIPT_DIR%/%FILENAME%
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

echo Creating directory %REMOTE_EMMA_HOME_DIR%/scripts on %HOSTNAME%...
plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% mkdir -p %REMOTE_EMMA_HOME_DIR%/scripts
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

echo Opening permissions on directory %REMOTE_EMMA_HOME_DIR%/scripts on %HOSTNAME%...
plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% chmod 755 %REMOTE_EMMA_HOME_DIR%/scripts
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

for %%F in (emma_generate_reports.sh emma_generate_all_reports.sh emma_get_coverage_data.sh dump_coverage_data.sh emma_instr.sh emma_instr_ci.sh emma_jar_lists.sh emma_jar_lists_ci.sh  gen_executive_summary.sh gen_executive_summary.py) do (
    echo Copying %%F to %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%/scripts...
    pscp -l %USERNAME% -pw %PASSWORD% %%F %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%/scripts
    if errorlevel 1 (
        echo FAILED
        exit /B 1
    )

    echo Converting to unix line endings for %REMOTE_EMMA_HOME_DIR%/scripts/%%F on %HOSTNAME%....
    plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% dos2unix %REMOTE_EMMA_HOME_DIR%/scripts/%%F
    if errorlevel 1 (
        echo FAILED
        exit /B 1
    )
    
    echo Adding execute permissions for %REMOTE_EMMA_HOME_DIR%/scripts/%%F on %HOSTNAME%....
    plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% chmod +x %REMOTE_EMMA_HOME_DIR%/scripts/%%F
    if errorlevel 1 (
        echo FAILED
        exit /B 1
    )
)
    
for %%F in (componentPackages.xml emma_filter.txt) do (
    echo Copying %%F to %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%...
    pscp -l %USERNAME% -pw %PASSWORD% %%F %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%
    if errorlevel 1 (
        echo FAILED
        exit /B 1
    )

    echo Converting to unix line endings for %REMOTE_EMMA_HOME_DIR%/%%F on %HOSTNAME%....
    plink -ssh -l %USERNAME% -pw %PASSWORD% %HOSTNAME% dos2unix %REMOTE_EMMA_HOME_DIR%/%%F
    if errorlevel 1 (
        echo FAILED
        exit /B 1
    )
)

set FILENAME=emma.jar
echo Copying %FILENAME% to %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%...
pscp -l %USERNAME% -pw %PASSWORD% %FILENAME% %USERNAME%@%HOSTNAME%:%REMOTE_EMMA_HOME_DIR%
if errorlevel 1 (
    echo FAILED
    exit /B 1
)

exit /B 0
