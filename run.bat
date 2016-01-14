set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
if not exist "logs" mkdir logs

cd event-planner
atlas-run --product jira --version 6.4.4 -Datlassian.plugins.enable.wait=300 > ..\logs\run_%logfilename%.log
cd ..