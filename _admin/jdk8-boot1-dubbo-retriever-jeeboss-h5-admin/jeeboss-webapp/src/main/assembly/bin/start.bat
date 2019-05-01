@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set ENV_PATH=.\
if "%OS%" == "Windows_NT" set ENV_PATH=%~dp0%
set SERVER_PORT=%1%
set SPRING_PROFILES_ACTIVE=%2%
set conf_dir=%ENV_PATH%\..\conf
set logback_configurationFile=%conf_dir%\logback.xml

set CLASSPATH=%conf_dir%
set CLASSPATH=%CLASSPATH%;%conf_dir%\..\lib\*

set JAVA_MEM_OPTS= -Xms128m -Xmx512m -XX:PermSize=128m
set JAVA_OPTS_EXT= -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dapplication.codeset=UTF-8 -Dfile.encoding=UTF-8
set CANAL_OPTS= -DappName=otter-canal -Dlogback.configurationFile="%logback_configurationFile%"

set JAVA_OPTS= %JAVA_MEM_OPTS% %JAVA_OPTS_EXT% %CANAL_OPTS% -Dtomcat.server.port=%SERVER_PORT% -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%

set CMD_STR= java %JAVA_OPTS% -classpath "%CLASSPATH%" java %JAVA_OPTS% -classpath "%CLASSPATH%" com.zksite.web.common.Application
echo start cmd : %CMD_STR%

java %JAVA_OPTS% -classpath "%CLASSPATH%" com.zksite.web.common.Application