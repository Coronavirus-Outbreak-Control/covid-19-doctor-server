package org.coronaviruscheck.api.doctors.CommonLibs;

import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class RedisHandler {

    public static volatile RedissonClient client = null;

    static {
        init();
    }

    public static void init() {
        if ( client == null ) {
            synchronized ( RedisHandler.class ) {
                if ( client == null ) {
                    Config config = new Config();
                    config.useSingleServer().setAddress( ApplicationRegistry.RedisDSN );
                    config.setCodec( new JsonJacksonCodec() );
                    client = Redisson.create( config );
                }
            }
        }

    }

    private RedisHandler() {
    }

}