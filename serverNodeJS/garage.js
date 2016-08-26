var gpio=require("pi-gpio");
var sleep=require("sleep")
var exports=module.exports={};
exports.pulseGate = function(){
	gpio.open(26,"output",function(err){
		gpio.write(26,1,function(){
			gpio.close(26);
		});
	});
}
