#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Startup Batch script for UNIX
# ----------------------------------------------------------------------------

set -e

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASE_DIR=`cd "$PRGDIR" >/dev/null; pwd`

. "$BASE_DIR/.mvn/wrapper/maven-wrapper.properties" 2>/dev/null || true

# Determine Maven wrapper jar location
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"

# Download wrapper jar if not present
if [ ! -r "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    mkdir -p "$WRAPPER_DIR"
    WRAPPER_JAR_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    curl -o "$WRAPPER_JAR" "$WRAPPER_JAR_URL" -s || wget -O "$WRAPPER_JAR" "$WRAPPER_JAR_URL" --quiet
fi

# Run maven
exec java -jar "$WRAPPER_JAR" "$@"
