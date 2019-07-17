@echo off

cls
title Build
set BUILDTOOLS_DIR=
set JAVA_HOME=%JAVA_HOME%

IF EXIST ../buildtools (
    for /f "delims=" %%i in ("../buildtools") do set BUILDTOOLS_DIR=%%~fi
) ELSE (
    IF EXIST ../../buildtools (
        for /f "delims=" %%i in ("../../buildtools") do set BUILDTOOLS_DIR=%%~fi
    ) ELSE (
        IF EXIST ../../../buildtools (
            for /f "delims=" %%i in ("../../../buildtools") do set BUILDTOOLS_DIR=%%~fi
        ) ELSE (
            IF EXIST ../../../../buildtools (
                for /f "delims=" %%i in ("../../../../buildtools") do set BUILDTOOLS_DIR=%%~fi
            ) ELSE (
                echo "Error: 'buildtools' project could not be found. Please check out from SVN."
                exit /b 1
            )
        )
    )
)

if "%JAVA_HOME%" == "" (
    echo "Error: You must set 'JAVA_HOME' as an environment variable."
    exit /b 1
)

set ANT_HOME=%BUILDTOOLS_DIR%\ant
set CLASSPATH=%ANT_HOME%\lib\ant-launcher.jar
echo "BUILDTOOLS_DIR=%BUILDTOOLS_DIR%"

"%JAVA_HOME%\bin\java" -classpath "%CLASSPATH%" -Dfile.encoding=UTF-8 -ms512m -mx512m -Dant.home="%ANT_HOME%" -Dbuildtools.dir="%BUILDTOOLS_DIR%" org.apache.tools.ant.launch.Launcher %1 %2 %3 %4 %5 %6

