 #!/bin/sh
#
# Copyright 2007-2022 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
done
SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

MAVEN_WRAPPER_PROPERTIES_FILE="${SCRIPT_DIR}/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR_FILE="${SCRIPT_DIR}/.mvn/wrapper/maven-wrapper.jar"

main () {
  read_wrapper_properties
  find_java
  download_wrapper_jar
  execute_maven "$@"
}

read_wrapper_properties() {
  if [ -z "${MAVEN_WRAPPER_PROPERTIES_URL-}" ]; then
    MAVEN_WRAPPER_PROPERTIES_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.properties"
  fi

  if [ ! -f "${MAVEN_WRAPPER_PROPERTIES_FILE}" ]; then
    log "Downloading ${MAVEN_WRAPPER_PROPERTIES_URL} to ${MAVEN_WRAPPER_PROPERTIES_FILE}"
    download "${MAVEN_WRAPPER_PROPERTIES_URL}" "${MAVEN_WRAPPER_PROPERTIES_FILE}"
  fi

  log "Reading properties from ${MAVEN_WRAPPER_PROPERTIES_FILE}"
  distributionUrl=
  wrapperUrl=
  while IFS='=' read -r key value
  do
    case "$key" in
      "distributionUrl")
        distributionUrl="$value"
        ;;
      "wrapperUrl")
        wrapperUrl="$value"
        ;;
    esac
  done < "${MAVEN_WRAPPER_PROPERTIES_FILE}"

  if [ -z "${wrapperUrl-}" ]; then
    if [ -z "${MAVEN_WRAPPER_JAR_URL-}" ]; then
      MAVEN_WRAPPER_JAR_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"
    fi
  else
    MAVEN_WRAPPER_JAR_URL="${wrapperUrl}"
  fi
}

find_java () {
  if [ -n "${JAVA_HOME-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    log "Using java from JAVA_HOME at ${JAVA_HOME}"
    _java="${JAVA_HOME}/bin/java"
  else
    log "Using java from path"
    _java="java"
  fi
}

download_wrapper_jar () {
  if [ ! -f "${MAVEN_WRAPPER_JAR_FILE}" ]; then
    if [ ! -d "${MAVEN_WRAPPER_JAR_FILE%/*}" ]; then
      log "Creating directory ${MAVEN_WRAPPER_JAR_FILE%/*}"
      mkdir -p "${MAVEN_WRAPPER_JAR_FILE%/*}"
    fi
    log "Downloading ${MAVEN_WRAPPER_JAR_URL} to ${MAVEN_WRAPPER_JAR_FILE}"
    download "${MAVEN_WRAPPER_JAR_URL}" "${MAVEN_WRAPPER_JAR_FILE}"
  fi
}

execute_maven () {
  log "Executing Maven"
  "$_java" ${MAVEN_OPTS-} -jar "${MAVEN_WRAPPER_JAR_FILE}" "$@"
}

download () {
  if [ -x "$(command -v curl)" ]; then
    curl -fsSL -o "$2" "$1"
  elif [ -x "$(command -v wget)" ]; then
    wget -q -O "$2" "$1"
  else
    log "No curl or wget found. Falling back to Java."
    find_java
    "$_java" -Dhttps.protocols=TLSv1.2 Downloader.java "$1" "$2"
  fi
}

log () {
  if [ "${MVNW_VERBOSE-}" = "true" ]; then
    echo "[MVNW] $1"
  fi
}

main "$@"