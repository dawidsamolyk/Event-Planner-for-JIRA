set actualtime=%TIME%
set logfilename=%actualtime::=_%
set logfilename=%logfilename:,=_%
set logfilename=%logfilename: =_%
set logfilename=%DATE%-%logfilename%
set logfiledir=logs\compilation
if not exist "%logfiledir%" mkdir %logfiledir%
cd event-planner
atlas-compile > ..\%logfiledir%\compile_%logfilename%.log
cd ..