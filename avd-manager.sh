#!/bin/bash

EMULATOR_PORT=5556
EMULATOR_NAME="emulator-"${EMULATOR_PORT}
#EMULATOR_TARGET='android-25'
EMULATOR_TARGET="system-images;android-25;google_apis;x86_64"
EMULATOR_ABI='google_apis/x86_64'


check_avd(){
    echo "Checking if exists ${EMULATOR_NAME} ..."
    AVD_LIST_RESULT=$(emulator -list-avds | grep ${EMULATOR_NAME})
    if [ ${AVD_LIST_RESULT} == ${EMULATOR_NAME} ]; then
        echo "Emulator exists!"
        return 0
    else
        echo "Emulator not exists!"
        return 1
    fi
    echo ${AVD_LIST_RESULT}
}


create_avd(){
    echo "Creating emulator with name ${EMULATOR_NAME}, with target ${EMULATOR_TARGET} and with ABI ${EMULATOR_ABI} ..."
    echo "no" | $ANDROID_HOME/tools/bin/avdmanager -s create avd -n ${EMULATOR_NAME} -k "${EMULATOR_TARGET}" -b ${EMULATOR_ABI} -f
}

start_avd(){
    echo "Starting emulator ${EMULATOR_NAME} on port ${EMULATOR_PORT} ... "
    nohup $ANDROID_HOME/emulator/emulator -avd ${EMULATOR_NAME} -port ${EMULATOR_PORT} -no-window 2>/dev/null<&1 &
}

init_avd(){
   check_avd
   if [ $? == 0 ]; then
        start_avd
   else
        create_avd
        start_avd
   fi
}

wait_avd(){

    BOOT_COMPLETE=$(adb -s ${EMULATOR_NAME} shell getprop dev.bootcomplete 2>/dev/null)

    if [ "$BOOT_COMPLETE" == "1" ]; then
        echo "Start complete"
        sleep 3
       # adb shell input keyevent 82
     else
        echo  "Wait avd start ..."
        sleep 3
        wait_avd
    fi
}

init_avd
wait_avd