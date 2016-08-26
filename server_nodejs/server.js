var garage = require("./garage");
var logger = require("./logger");
var net = require('net');
process.env.TZ= 'Europe/Madrid';

//CONFIG
var host='127.0.0.1';
var port=1993;
var password='3fdf75de';

var server=net.createServer();
server.on('connection',handleConnection);

server.listen(port,function(){
  console.log('PiHome server is listening on port '+ port );
  console.log('--------------------------------------');
});

function handleConnection(conn){
  var str = conn.remoteAddress;
  var address = str.split(":");
  var port_n = conn.remotePort;
  var date = new Date();
  console.log("Client connected - IP: "+address[3]+":"+port_n);
  console.log(date);
  logger.writeLog("Client connected - IP: "+address[3]+":"+port_n+"\n");
  logger.writeLog(date+"\n");
  //Data Handler
  conn.on('data',function(d){
  console.log("Password verification in process...");
  logger.writeLog("Password verification in process...\n");
  //CASE 1: Correct password
  if(password==d.toString()){
    conn.write("Password OK. Activating gate.");
    logger.writeLog("Sending: Password OK. Activating gate.\n");
    //garage.pulseGate();
  }
  //CASE 2: Wrong password
  else {
    console.log(d.toString());
    logger.writeLog("Wrong password "+d.toString()+"\n");
    logger.writeLog("Sending: Wrong password. Set a correct password.\n");
    conn.write("Wrong password. Set a correct password.");

  }

  });

  //Disconnection handler
  conn.on('end',function(){
    console.log('Client disconnected IP: '+address[3]);
    console.log('---------------------------------------');
    logger.writeLog('Client disconnected IP: '+address[3]+'\n');
    logger.writeLog('---------------------------------------\n');
  });
};
