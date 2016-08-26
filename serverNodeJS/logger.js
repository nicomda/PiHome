var fs = require("fs");
var files;
var exports = module.exports={};
exports.writeLog = function(log){
    fs.appendFile("connections.log",log,function(err){
      if(err){
        return console.error(err);
      }
    });
};
