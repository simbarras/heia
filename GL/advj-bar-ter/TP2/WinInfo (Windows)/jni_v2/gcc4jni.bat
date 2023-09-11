@REM ------------------ R. Scheurer, 03/2023 --------------------------
@echo off

REM TODO: Please adjust the following two paths if needed!
set JAVA=C:\Users\nterr\.jdks\openjdk-17\bin
set MINGW=C:\Users\nterr\Documents\mingw64\bin

:parms
IF NOT %1/==/ goto parm2
echo.
echo Parameter(s) missing!
goto usage

:parm2
IF NOT %2/==/ goto checkGCCPath
echo Second parameter is missing!
goto usage

:checkGCCPath
REM check if path to GCC has already been added to environment variable PATH
WHERE gcc > NUL 2>&1 && goto checkJDKPath
set PATH=%PATH%;%MINGW%
WHERE gcc > NUL 2>&1 && goto checkJDKPath
echo ERROR: could not find gcc installation!
echo You need to edit the environment variables in this batch file ...
goto end

:checkJDKPath
if not defined JAVA_HOME set JAVA_HOME=%JAVA%
if exist "%JAVA_HOME%\bin\javac.exe" goto comp
echo ERROR: path to Java JDK (variable JAVA_HOME) is not valid!
goto end

:comp
echo on
gcc -s -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -shared %1 -o %2
@goto end

:usage
echo.
echo USAGE: gcc4jni "c-file" "dll-name"
echo.

:end