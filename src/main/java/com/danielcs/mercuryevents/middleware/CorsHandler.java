package com.danielcs.mercuryevents.middleware;

import com.danielcs.webserver.http.HttpMiddleware;
import com.danielcs.webserver.http.Request;
import com.danielcs.webserver.http.Responder;

import java.io.IOException;

public class CorsHandler implements HttpMiddleware {

    @Override
    public boolean process(Request request, Responder responder) throws IOException {
        request.setHeader("Access-Control-Allow-Origin", "*");
        request.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        request.setHeader("Access-Control-Allow-Headers", "Content-type,Accept,Authorization");

        if (request.getMethod().equals("OPTIONS")) {
            responder.sendResponse("");
        }

        return true;
    }
}
