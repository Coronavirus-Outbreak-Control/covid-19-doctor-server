package org.coronaviruscheck.api.doctors.Services.Redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReplicatedServersConfig;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class RedisHandler {

    public static volatile RedissonClient client = null;

    public static void init( String redisDsn) {

        if ( client == null ) {
            synchronized ( RedisHandler.class ) {
                if ( client == null ) {
                    Config config = new Config();

                    String[] dsn = redisDsn.split( "," );
                    if ( dsn.length > 1 ) {

                        ReplicatedServersConfig replConfig = config
                                .useReplicatedServers()
                                .setScanInterval( 1000 );

                        for ( String dsn_repl : dsn ) {
                            replConfig.addNodeAddress( dsn_repl );
                        }

                    } else if ( dsn.length == 1 ) {
                        config.useSingleServer().setAddress( dsn[ 0 ] );
                    }

                    config.setCodec( new JsonJacksonCodec() );
                    client = Redisson.create( config );
                }
            }

        }

    }

    private RedisHandler() {
    }

}