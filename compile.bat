set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir logs
cd event-planner
atlas-compile > ../logs/compile_%logfilename%.log
cd ..