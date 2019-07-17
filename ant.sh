#!/bin/sh

# This file is designed to be includable in other scripts to encapsulate
# the code that finds build tools. Both release.sh and ant.sh need to
# use the same buildtools.


## FUNCTIONS ##

# search up from given path for buildtools
find_buildtools_in_path() {
    cur_dir="$1"
    while true ; do
        buildtools="$cur_dir/buildtools"
        if [ -d "$buildtools" ]; then
            echo "$buildtools"
            return 0
        else
            cur_dir="`dirname "$cur_dir"`"
            if [ "$cur_dir" = "/" ]; then
                echo ""
                return 1
            fi
        fi
    done
}


# Find buildtools and set BUILDTOOLS_DIR
find_buildtools() {
    logical_path="`pwd -L`"
    valid_buildtools="`find_buildtools_in_path "$logical_path"`"

    if [ -z "$valid_buildtools" ]; then
        physical_path="`pwd -P`"
        valid_buildtools="`find_buildtools_in_path "$physical_path"`"
    fi

    if [ -z "$valid_buildtools" ]; then
        echo "Error: cannot find buildtools"
        exit 1
    fi

    BUILDTOOLS_DIR="$valid_buildtools"
}


set_buildtools_dir() {
    [ "$BUILDTOOLS_DIR" ] || find_buildtools
    if [ ! -d "${BUILDTOOLS_DIR}" ]; then
        echo "Error: buildtools is missing: $BUILDTOOLS_DIR"
        exit 1
    fi

    BUILDTOOLS_DIR="` cd "$BUILDTOOLS_DIR" >/dev/null 2>&1 ; pwd `"
}


## CODE START ##


SCRIPT_NAME="`basename "$0"`"

if [ "$SCRIPT_NAME" = "ant.sh" ]; then
    set_buildtools_dir
    exec ${BUILDTOOLS_DIR}/bin/ant.sh "$@"
fi

