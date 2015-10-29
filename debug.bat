set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir logs
cd event-planner
atlas-debug --product jira -Datlassian.plugins.enable.wait=300  > ../logs/debug_%DATE%-%timestamp%.log
cd ..