var page = require('webpage').create(),
  system = require('system'),
  t, address;

if (system.args.length === 1) {
  console.log('Usage: loadspeed.js <some URL>');
  phantom.exit();
}

t = Date.now();
address = system.args[1];
page.open(address, function(status) {
  var title = page.evaluate(function() {
    return document.body;
  });
  console.log('Page title is ' + title);
  phantom.exit();
});