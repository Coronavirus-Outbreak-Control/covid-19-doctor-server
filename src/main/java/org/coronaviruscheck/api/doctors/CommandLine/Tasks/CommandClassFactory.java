package org.coronaviruscheck.api.doctors.CommandLine.Tasks;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 24/07/19
 */
@FunctionalInterface
public interface CommandClassFactory {

    Command newInstance( String[] args );

}
