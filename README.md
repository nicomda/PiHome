PiHome
======

Selfdomotize your home

That is a Raspberry server and Android App to domotize what you want with relays on your home.
Will consist on:

Android App:
MainActivity to present a button you press to activate contact with the server and sent the order packets.
Settings Activity to configure some parameters like Server IP on Wifi and 3g, port, password...
Gps Class to handle reverse location on Android, to avoid far away openings.
NFC Mimetype=text/plain intent handler

Server for RPi:
Daemon for server.
Java Server.

Server usage:
#sudo java -jar PiHome_Server.jar "PORT_NUMBER" "SERVER_PASSWORD"
