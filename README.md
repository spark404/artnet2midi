ArtNet2Midi
===========

[![Build Status](https://travis-ci.org/spark404/artnet2midi.svg?branch=master)](https://travis-ci.org/spark404/artnet2midi)

Familiyday special

application.properties holds all the relevant configuration

```
logging.level.org.springframework.web.client.RestTemplate=DEBUG
logging.level.net.strocamp=DEBUG
logging.file=artnet2midi.log

# Make sure this points to the Titan Quarts, the port is pretty much default
titan.wepapi.url=http://192.168.168.107:4430

# This bit is to make the node automagicaly start an AnrtNetNode
# Configure the right interface and the rest is probably standard
artnetnode.autostart=true
artnetnode.dmx.interface=wlan0
artnetnode.dmx.universe=0
artnetnode.dmx.network=0
artnetnode.dmx.subnet=0

# This is the DMX start address of the gameserver
# The gameserver uses two channels, 
# 1: Reset (set to zero, set to >200, set to zero)
# 2: Reserved
gameserver.dmx.address=25

# These are the playbacks that are triggered when the 
# corresponding buttons are hit.
# Find these numbers on the top left of the handles on the titan console
gamerunner.button_a.playback=7
gamerunner.button_b.playback=11
```

Building
--------
`mvn -Ppi4j clean package`

Requires the pi4j library from http://pi4j.com. Install the pi4j-core.jar in the local maven respository.

`mvn install:install-file -Dfile=/opt/pi4j/lib/pi4j-core.jar -DgroupId=org.pi4j \
    -DartifactId=pi4j-core -Dversion=1.1 -Dpackaging=jar`


Running
-------
`sudo java -jar artnet2midi-1.0-SNAPSHOT.jar`

Make sure the application.properties file is in the current directory when you start the application. Requires sudo to allow access to the Raspberry GPIO lines.


Notes
-----
The gameserver has a personality file for the Titan console. It is already loaded, search for _Raspberry Gameserver_ with manufacturer _Hugo_ or load the file in the personality directory.