# stf-console-client
A console client for the STF

## Download
[Latest version](https://github.com/e13mort/stf-console-client/releases/latest)

[All versions](https://github.com/e13mort/stf-console-client/releases/)

## Configuration

todo:

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
          -abi
            Filter by device abi architecture
          -api
            Filter by device api level
          -count
            Filter devices by count
          -maxApi
            Filter by device max api level
          -minApi
            Filter by device min api level
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
          -count
            Filter devices by count
          -maxApi
            Filter by device max api level
          -minApi
            Filter by device min api level
          -name
            Filter devices by its name
          -provider
            Filter devices by provider
          -serial
            Filter devices by serial number


[![Build Status](https://travis-ci.org/e13mort/stf-console-client.svg?branch=master)](https://travis-ci.org/e13mort/stf-console-client)