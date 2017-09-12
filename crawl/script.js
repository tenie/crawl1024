var page = require('webpage').create(),
  system = require('system'), t, address , isDynamic;

 

if (system.args.length === 2) {
  console.log('error');
  phantom.exit();
}

 

isDynamic = system.args[1];

if( isDynamic == 'false'){ 
	page.settings.javascriptEnabled  = false;
}
page.settings.loadImages = false;
//t = Date.now();
address = system.args[2];
page.open(address, function(status) {
	if( status == 'success' ) {
//     var body = page.evaluate(function() {
//	    return document.body.innerHTML;
//	  });
//	  console.log(body);
		 var content = page.content;
		  console.log(content);
	}else{
		console.log("error");
	}
	//t = Date.now() - t;
	//console.log('Loading time ' + t + ' msec');
   phantom.exit();
});