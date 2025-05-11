package com.chat.resource;

import com.chat.model.Message;
import com.chat.service.ChatService;
import com.sun.jersey.spi.resource.Singleton;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.List;

/**
 * Point d'entrée REST pour le service de chat
 */
@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ChatResource {
    // Tomcat 6 n'a pas CDI, donc nous utilisons une instance directe
    private ChatService chatService = new ChatService();
    
    private ChatService getService() {
        return chatService;
    }
    
    /**
     * Enregistre un nouvel utilisateur
     * @param username Nom d'utilisateur
     * @return 200 OK si l'utilisateur est enregistré, 400 BAD REQUEST sinon
     */
    @POST
    @Path("/users")
    @Consumes("application/x-www-form-urlencoded")
    public Response registerUser(@FormParam("username") String username) {
        if (getService().registerUser(username)) {
            return Response.ok().entity("Utilisateur enregistré: " + username).build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Impossible d'enregistrer l'utilisateur: " + username)
                    .build();
        }
    }
    
    /**
     * Déconnecte un utilisateur
     * @param username Nom d'utilisateur à déconnecter
     * @return 200 OK si l'utilisateur est déconnecté, 404 NOT FOUND sinon
     */
    @DELETE
    @Path("/users/{username}")
    public Response removeUser(@PathParam("username") String username) {
        if (getService().removeUser(username)) {
            return Response.ok().entity("Utilisateur déconnecté: " + username).build();
        } else {
            return Response.status(Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé: " + username)
                    .build();
        }
    }
    
    /**
     * Récupère la liste des utilisateurs actifs
     * @return Liste des noms d'utilisateurs actifs
     */
    @GET
    @Path("/users")
    public Response getActiveUsers() {
        List<String> users = getService().getActiveUsers();
        try {
            // Convertir manuellement en JSON avec Jackson
            ObjectMapper mapper = new ObjectMapper();
            String jsonUsers = mapper.writeValueAsString(users);
            return Response.ok().entity(jsonUsers).type("application/json").build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la sérialisation: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Envoie un nouveau message
     * @param username Nom de l'expéditeur
     * @param content Contenu du message
     * @return 200 OK avec l'ID du message si envoyé, 400 BAD REQUEST sinon
     */
    @POST
    @Path("/messages")
    @Consumes("application/x-www-form-urlencoded")
    public Response postMessage(
            @FormParam("username") String username,
            @FormParam("content") String content) {
        
        long messageId = getService().postMessage(username, content);
        if (messageId > 0) {
            return Response.ok().entity(Long.toString(messageId)).build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Impossible d'envoyer le message")
                    .build();
        }
    }
    
    /**
     * Récupère les messages
     * @param since ID à partir duquel récupérer les messages (optionnel)
     * @return Liste des messages
     */
    @GET
    @Path("/messages")
    public Response getMessages(@QueryParam("since") Long since) {
        List<Message> messageList;
        
        if (since != null && since > 0) {
            messageList = getService().getMessagesAfter(since);
        } else {
            messageList = getService().getAllMessages();
        }
        
        try {
            // Convertir manuellement en JSON avec Jackson
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessages = mapper.writeValueAsString(messageList);
            return Response.ok().entity(jsonMessages).type("application/json").build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la sérialisation: " + e.getMessage())
                    .build();
        }
    }
}
