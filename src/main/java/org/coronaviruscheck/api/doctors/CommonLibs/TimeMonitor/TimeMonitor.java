package org.coronaviruscheck.api.doctors.CommonLibs.TimeMonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 27/02/20
 */
public class TimeMonitor {

    private final ObjectMapper mapper = new ObjectMapper();

    private final LinkedHashMap<String, PointInTime> timer = new LinkedHashMap<>();

    public static TimeMonitor getInstance() {
        return new TimeMonitor();
    }

    public PointInTime createPointInTime( String pointInTime ) {
        this.timer.put( pointInTime, new PointInTime( pointInTime ) );
        return this.timer.get( pointInTime );
    }

    public void removePointInTime( PointInTime PointInTime ) {
        this.timer.remove( PointInTime.name );
    }

    public void reset() {
        this.timer.clear();
    }

    public boolean isEmpty() {
        return this.timer.size() == 0;
    }

    public String toString() {

        try {
            return this.render();
        } catch ( JsonProcessingException e ) {
            LogManager.getLogger( this.getClass() ).error( e.getMessage(), e );
        }

        return null;
    }

    public static class PointInTime {

        private Instant  start    = Instant.now();
        private Duration duration = Duration.ZERO;

        private String name;

        double render() {
            return (double) this.duration.toNanos() / 1000000000L;
        }

        PointInTime( String name ) {
            this.name = name;
        }

        public void collect() {
            Instant end = Instant.now();
            duration = Duration.between( start, end );
        }

    }

    private String render() throws JsonProcessingException {

        LinkedHashMap<String, Double> result = this.timer.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().render(),
                        ( oldValue, newValue ) -> newValue,
                        LinkedHashMap::new
                )
        );

        HashMap<String, LinkedHashMap<String, Double>> render = new HashMap<>();
        render.put( Thread.currentThread().getName(), result );

        return this.mapper.writeValueAsString( render );
    }

}
