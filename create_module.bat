set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir logs
cd event-planner
atlas-create-jira-plugin-module > ../logs/create_module_%logfilename%.log
cd ..