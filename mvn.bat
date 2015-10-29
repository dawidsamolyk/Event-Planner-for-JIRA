set actualtime=%TIME%
set timestamp=%actualtime::=_%
set timestamp=%timestamp:,=_%
if not exist "logs" mkdir logs
if not exist "logs" mkdir logs
cd event-planner
atlas-mvn eclipse:eclipse > ../logs/mvn_%DATE%-%timestamp%.log
cd ..