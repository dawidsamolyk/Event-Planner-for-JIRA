set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir "logs"
cd event-planner
atlas-clean > ../logs/atlas-clean_%DATE%-%timestamp%.log
cd ..