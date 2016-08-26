var net = require('net');
var port = 1993;
var client = net.connect(port, function() {
   console.log('Connected to server!');
   client.write("3fdf75");
});
client.on('data', function(data) {
   console.log(data.toString());
   client.end();
});
client.on('end', function() {
   console.log('Disconnected from server');
});
