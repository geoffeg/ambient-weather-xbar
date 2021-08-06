# ambient-weather-xbar
An Ambient Weather plugin for [xbar](https://github.com/matryer/xbar) to show the latest reading from a weather station.

Setup:
* Install [Babashka](https://github.com/babashka/babashka)
* Go to https://ambientweather.net/account and create a new API Key and a new APP Key. Enter those values into the configuration sections in xBar.
* Locate the MAC Address of the station you wish to display the readings from by grabbing the MAC Address from https://ambientweather.net/devices and add that to the configuration section in xBar.
* Copy or symlink this script in to your xbar plugins directory with a .sh extension.
