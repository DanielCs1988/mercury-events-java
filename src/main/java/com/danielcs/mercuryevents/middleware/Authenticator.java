package com.danielcs.mercuryevents.middleware;

import com.danielcs.webserver.http.*;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.*;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;

import java.io.IOException;
import java.util.Date;

public class Authenticator implements HttpMiddleware {

    private final HttpsJwksVerificationKeyResolver resolver;

    public Authenticator(HttpsJwks httpsJwks) {
        this.resolver = new HttpsJwksVerificationKeyResolver(httpsJwks);
    }

    @Override
    public boolean process(Request request, Responder responder) throws IOException {
        String token = extractAuthHeader(request, responder);
        if (token == null) {
            return false;
        }
        try {
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setExpectedIssuer(System.getenv("ISSUER"))
                    .setExpectedAudience(System.getenv("AUDIENCE"))
                    .setVerificationKeyResolver(resolver)
                    .setJweAlgorithmConstraints(
                            new AlgorithmConstraints(
                                    AlgorithmConstraints.ConstraintType.WHITELIST,
                                    AlgorithmIdentifiers.RSA_USING_SHA256
                            )
                    )
                    .build();
            JwtClaims claims = jwtConsumer.processToClaims(token);
            if (claims.getExpirationTime().getValueInMillis() < new Date().getTime()) {
                responder.sendError(401, "The token has expired!");
                return false;
            }
            request.setProperty("userId", claims.getSubject());
        } catch (InvalidJwtException | MalformedClaimException e) {
            responder.sendError(401, "Invalid token!");
            return false;
        }
        return true;
    }

    private String extractAuthHeader(Request request, Responder responder) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            responder.sendError(401, "Authorization header must be present!");
            return null;
        }
        String[] authHeaderParts = authHeader.split(" ");
        if (authHeaderParts.length != 2) {
            responder.sendError(401, "Authorization header must use the bearer scheme!");
            return null;
        }
        return authHeaderParts[1];
    }
}
