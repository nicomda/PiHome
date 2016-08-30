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

##Server for RPi:
Server on Node.js. To run it on boot, just add it to rc.local or make a daemon 

##Server usage:
   $sudo node server.js

Server password and port must be set on server.js file.

(sudo is needed because of the GPIO library) 
