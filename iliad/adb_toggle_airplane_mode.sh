function adb_toggle_airplane_mode {
    # Open airplane mode settings
    adb shell am start -a android.settings.AIRPLANE_MODE_SETTINGS

    # Key UP to focus on the first switch = toggle airplane mode, then sleep 100ms
    adb shell input keyevent 19 ; sleep 0.1

    # Key CENTER to toggle the first switch, then sleep 100ms
    adb shell input keyevent 23 ; sleep 0.1

    # Key BACK to close the settings 
    adb shell input keyevent 4

}