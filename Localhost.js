var http = require('http');
var url = require('url');


var successReply = {
    statusCode: '200',
    statusMessage: 'SUCCESS',
    subStatus: 'SUCCESS',
    groupId: '246',
    fullGroupName: 'TESTGROUP300'
}

var failureReply = {
    statusCode: '400',
    statusMessage: 'Unknown Failure',
    subStatus: 'FAILED'
}
//create a server object:
http.createServer(function (req, res) {
    console.log('req method ' + req.method);
    console.log('req url ' + req.url);
    console.log('req rawHeaders ' + req.rawHeaders);
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.write(JSON.stringify(successReply));
    res.end(); //end the response
}).listen(8080);
