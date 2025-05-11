/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
package com.chat;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.chat.resource.ChatResource;

/**
 * Configure l'application JAX-RS pour Jersey 1.x
 * Note: Le chemin d'API est configuré dans web.xml
 */
public class ChatApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // Ajout manuel de nos ressources REST
        classes.add(ChatResource.class);
        return classes;
    }
}
