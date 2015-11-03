set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir "logs"
cd event-planner
atlas-clean > ../logs/clean_%logfilename%.log
cd ..