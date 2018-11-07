/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compa.basicauth;

import com.google.gson.Gson;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("user")
public class AuthService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkUser() {
        return Response.ok("Du är inloggad").build();
    }

    @POST
    public Response postUser(@HeaderParam("Authorization") String authorization) {
        Credentials credentials = CredentialFacade.createCredentials(authorization);
        CredentialFacade.save(credentials);
        return Response.status(Response.Status.CREATED).build();
    }
}
