package com.danielcs.mercuryevents;

import com.danielcs.mercuryevents.repository.utils.SQLUtils;
import com.danielcs.webserver.core.Application;
import com.danielcs.webserver.core.annotations.Dependency;
import com.danielcs.webserver.http.BasicHttpServer;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;

public class Main {

    private static final String CLASSPATH = "com.danielcs.mercuryevents";
    private static final int PORT = Integer.valueOf(System.getenv("PORT"));
    private static final int POOL_SIZE = 50;

    public static void main(String[] args) {
        Application application = new Application(BasicHttpServer.class, CLASSPATH, PORT, POOL_SIZE);
        application.start();
    }

    @Dependency
    public JwtConsumer buildJwtConsumer() {
        HttpsJwks httpsJwks = new HttpsJwks(System.getenv("JWKS_URI"));
        HttpsJwksVerificationKeyResolver resolver = new HttpsJwksVerificationKeyResolver(httpsJwks);
        return new JwtConsumerBuilder()
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
    }

    @Dependency
    public SQLUtils getDataManager() {
        return new SQLUtils(
                System.getenv("DB_PATH"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        );
    }
}
