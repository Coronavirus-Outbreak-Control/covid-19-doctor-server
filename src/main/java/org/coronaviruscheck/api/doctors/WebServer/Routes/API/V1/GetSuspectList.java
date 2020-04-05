package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 05/04/2020
 */
@Path("/v1")
public class GetSuspectList {

    @GET
    @Path("/patient/suspect/{by_doctor_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getList(
            HashMap<String, String> payload,
            @PathParam("by_doctor_id") Integer doctorID,
            @HeaderParam("authorization") String authString
    ) {
        return Response.ok().build();
    }

}



