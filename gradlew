#!/bin/bash
# Gradle wrapper script for Unix
APP_NAME="Gradle"
APP_PATH=`pwd`
GRADLE_HOME="$APP_PATH/gradle/wrapper"
exec java -jar "$GRADLE_HOME/gradle-wrapper.jar" "$@"
