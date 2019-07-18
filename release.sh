#!/usr/bin/env bash
# release a service.

ROOT=$(dirname "$0")
ANT="$ROOT/ant.sh"

# include ant.sh to pickup BUILDTOOLS_DIR
. "$ANT"

set_buildtools_dir


if [ -z "$BUILDTOOLS_DIR" -o ! -e "$BUILDTOOLS_DIR" ]; then
    echo "Error: BUILDTOOLS_DIR is not set"
    exit 1
fi


exec "$BUILDTOOLS_DIR/bin/release.sh" "$@"
