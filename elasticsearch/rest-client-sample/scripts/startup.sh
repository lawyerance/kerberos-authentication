#!/bin/bash

base_dir=$(dirname $0)

################################################################################
#####
#####
#####
#####
#####
################################################################################

export LOGBACK_OPTS="-Dlogback.configurationFile=$base_dir/../config/logback.groovy"

if [ "xAPP_HEAP_OPTS" = "X" ]; then
  expot APP_HEAP_OPTS="-Xmx1G -Xms1G"
fi

EXTRA_ARGS=${EXTRA_ARGS-'-name spring-rest-client -loggc'}

COMMAND=$1
case $COMMAND in
-daemon)
  EXTRA_ARGS="-daemon "$EXTRA_ARGS
  shift
  ;;
*) ;;
esac

MAIN_CLASS="pers.lyks.kerberos.RestClientApplication"

exec $base_dir/sever-run-class.sh $EXTRA_ARGS $MAIN_CLASS "$@"
