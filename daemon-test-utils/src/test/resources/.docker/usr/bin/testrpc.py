import pyjsonrpc


class RequestHandler(pyjsonrpc.HttpRequestHandler):

    @pyjsonrpc.rpcmethod
    def getbestblockhash(self):
        """Test method"""
        return "abc"


# Threading HTTP-Server
http_server = pyjsonrpc.ThreadingHttpServer(
    server_address = ('0.0.0.0', 8080),
    RequestHandlerClass = RequestHandler
)
print("Starting HTTP server ...")
print("URL: http://0.0.0.0:8080")
http_server.serve_forever()