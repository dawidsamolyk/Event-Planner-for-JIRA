set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir logs
if not exist "logs" mkdir logs
cd event-planner
atlas-unit-test > ../logs/unit_test_%logfilename%.log
pause
cd ..