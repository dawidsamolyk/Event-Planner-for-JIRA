set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir logs
cd event-planner
atlas-integration-test  > ../logs/integration_test_%DATE%-%timestamp%.log
pause
cd ..