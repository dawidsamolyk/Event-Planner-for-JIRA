set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir logs
if not exist "logs" mkdir logs
cd event-planner
atlas-run --product jira -Datlassian.plugins.enable.wait=300  > ../logs/run_%DATE%-%timestamp%.log
cd ..