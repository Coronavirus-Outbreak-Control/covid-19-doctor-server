package org.coronaviruscheck.api.doctors.CommonLibs;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BootstrapLogger {

    public static void start( Logger logger, int port ){

        int pad = 45;

        for( String line: BootstrapLogger.getLogo() ){
            logger.info( line );
        }

        logger.info( formatStartingLog( StringUtils.center( "Adding a servlet with /api/@ path.", pad, " " ) ) );
        logger.info( formatStartingLog( StringUtils.center( "Started CronScheduler Monitor", pad, " " ) ) );
        logger.info( formatStartingLog( StringUtils.center( "Server is listening on port " + port, pad, " " ) ) );
        logger.info( formatStartingLog( "@" ) );

    }

    public static String formatStartingLog( String msg ){
        return StringUtils.center( msg, 81, "@" );
    }

    public static ArrayList<String> getLogo(){
        ArrayList<String> lines = new ArrayList<>();
        lines.add( formatStartingLog( "@" ) );
        lines.add( formatStartingLog( "     ____                                  ___                       " ) );
        lines.add( formatStartingLog( "    6MMMMb/                       68b      `MM                       " ) );
        lines.add( formatStartingLog( "   8P    YM                       Y89       MM                       " ) );
        lines.add( formatStartingLog( "  6M      Y   _____   ____    ___ ___   ____MM         __/   ____    " ) );
        lines.add( formatStartingLog( "  MM         6MMMMMb  `MM(    )M' `MM  6MMMMMM         `MM  6MMMMb   " ) );
        lines.add( formatStartingLog( "  MM        6M'   `Mb  `Mb    d'   MM 6M'  `MM          MM 6M'  `Mb  " ) );
        lines.add( formatStartingLog( "  MM        MM     MM   YM.  ,P    MM MM    MM          MM MM    MM  " ) );
        lines.add( formatStartingLog( "  MM        MM     MM    MM  M     MM MM    MM MMMMMMM  MM MM    MM  " ) );
        lines.add( formatStartingLog( "  YM      6 MM     MM    `Mbd'     MM MM    MM          MM YM.  ,MM  " ) );
        lines.add( formatStartingLog( "   8b    d9 YM.   ,M9     YMP      MM YM.  ,MM          MM  YMMMMMM  " ) );
        lines.add( formatStartingLog( "    YMMMM9   YMMMMM9       M      _MM_ YMMMMMM_        _MM_      M9  " ) );
        lines.add( formatStartingLog( "                                                               ,M9   " ) );
        lines.add( formatStartingLog( "                                                             ,MM9    " ) );
        lines.add( formatStartingLog( "                                                            d9'      " ) );
        lines.add( formatStartingLog( "@" ) );
        return lines;
    }

}

