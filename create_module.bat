set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir logs
cd event-planner
atlas-create-jira-plugin-module > ../logs/create_module_%DATE%-%timestamp%.log
cd ..