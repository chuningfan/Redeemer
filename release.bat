cls
echo off

echo "Cleaning..."
call ant clean
if "%ERRORLEVEL%" == "1" exit /b 1

echo "Updating buildtools..."
call svn update %BUILDTOOLS_DIR%

echo "Prepping Release"
call ant prep-release
if "%ERRORLEVEL%" == "1" exit /b 1

echo "Deploying..."
call ant _publish-release
if "%ERRORLEVEL%" == "1" (
    call svn revert build.xml
    exit /b 1
)

echo "Tagging & Installing..."
call ant finish-release
if "%ERRORLEVEL%" == "1" exit /b 1

