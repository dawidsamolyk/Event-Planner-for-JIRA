set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
set logfiledir=logs\tests
if not exist "%logfiledir%" mkdir %logfiledir%
cd event-planner
atlas-integration-test  > ..\%logfiledir%\integration_test_%logfilename%.log
pause
cd ..