set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir logs
cd event-planner
atlas-debug --product jira -Datlassian.plugins.enable.wait=300  > ../logs/debug_%logfilename%.log
cd ..