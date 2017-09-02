# STF console client
A console client for the [Smartphone Test Farm](https://github.com/openstf/stf) service

## Download
[Latest version](https://github.com/e13mort/stf-console-client/releases/latest)

[All versions](https://github.com/e13mort/stf-console-client/releases/)

![stf devices](stf_usage.png)

## Configuration

* Download and extract an artifact from "releases" page into some target directory
* Create file "farm.properties" either in your home directory or in the target app directory
* Add following properties into the file:

        stf.url=<your_farm_url>/api/v1/
        stf.key=<token created in 'Settings/Keys/Access Tokens' seciton>
        stf.timeout=<farm connection timeout in seconds, eg. 60>
        android_sdk=<path to android sdk directory>
* Optionally, add the app's directory into your PATH environment var

## Usage
stf [command] [command options]

  Commands:

    disconnect      Disconnect from all of currently connected devices
      Usage: disconnect

    devices      Print list of available devices
      Usage: devices [options]
        Options:
          --all
            Show all devices. By default only available devices are returned.
            Default: false
          --my-columns
            <BETA> Use columns from web panel
            Default: false
          -abi
            Filter by device abi architecture
          -api
            Filter by device api level
            Default: 0
          -count
            Filter devices by count
            Default: 0
          -maxApi
            Filter by device max api level
            Default: 0
          -minApi
            Filter by device min api level
            Default: 0
          -name
            Filter devices by its name
          -provider
            Filter devices by provider
          -serial
            Filter devices by serial number

    connect      Connect to devices
      Usage: connect [options]
        Options:
          --all
            Show all devices. By default only available devices are returned.
            Default: false
          --my
            Connect to currently taken devices
            Default: false
          -abi
            Filter by device abi architecture
          -api
            Filter by device api level
            Default: 0
          -count
            Filter devices by count
            Default: 0
          -l
            Connect to devices by its indexes from the results of previous
            "devices" command. E.g. "-l 1 2 5"
            Default: []
          -maxApi
            Filter by device max api level
            Default: 0
          -minApi
            Filter by device min api level
            Default: 0
          -name
            Filter devices by its name
          -provider
            Filter devices by provider
          -serial
            Filter devices by serial number


[![Build Status](https://travis-ci.org/e13mort/stf-console-client.svg?branch=master)](https://travis-ci.org/e13mort/stf-console-client)