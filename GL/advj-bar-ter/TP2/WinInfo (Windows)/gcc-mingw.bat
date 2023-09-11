@REM ------------------ R. Scheurer, 03/2023 --------------------------
@echo off

REM Please adjust the following path if gcc installation directory was not found
set MINGW=C:\Users\nterr\Documents\mingw64\bin

:parms
IF NOT %1/==/ goto parm2
echo Parameter(s) missing!
goto usage

:parm2
IF NOT %2/==/ goto checkGCCPath
echo Second parameter is missing!
goto usage

:checkGCCPath
REM check if path to gcc has already been added to environment variable PATH
WHERE gcc > NUL 2>&1 && goto comp
set PATH=%PATH%;%MINGW%
WHERE gcc > NUL 2>&1 && goto comp
echo ERROR: could not find gcc installation !
echo You need to edit this batch file ...
goto end

:comp
echo compiling executable ...
echo on
gcc -s %1 -o %2
@goto end

:usage
echo.
echo USAGE: gcc "c-file" "exe-file"
echo.

:end