var url, page;

if (phantom.args.length < 1) {
  console.log('Expected a target URL parameter');
  phantom.exit(1);
}

url  = phantom.args[0];
page = require('webpage').create();

page.onConsoleMessage = function (message) {
  console.log("Test Console: " + message);
};

console.log("Loading URL: " + url);

page.open(url, function (status) {
  if (status != "success") {
    console.log('Failed to open ' + url);
    phantom.exit(1);
  } else {
    console.log('Loaded page: ' + page);
  }

  console.log("Running tests.");
  var result = page.evaluate(function() {
    return jodadbg.runner.run();
  });

  if (result != 0) {
    console.log("*** Test failed! ***");
    phantom.exit(1);
  } else {
    console.log("Test succeeded.");
    phantom.exit(0);
  }
});
