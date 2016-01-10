var url, page;

if (phantom.args.length < 1) {
  console.log('Expected a target URL parameter');
  phantom.exit(1);
}

url  = phantom.args[0];
page = require('webpage').create();

page.onConsoleMessage = function(message) {
  console.log('REPL Console: ' + message);
};

console.log('Loading URL: ' + url);

page.open(url, function(status) {
  if (status != "success") {
    console.log('Failed to open ' + url);
    phantom.exit(1);
  }

  console.log('Loaded successfully');
});
