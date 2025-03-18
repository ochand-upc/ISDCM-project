package com.isdcm.minetflix.utils;

import com.isdcm.minetflix.dao.VideoDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.http.HttpSession;

/**
 * Singleton para manejar reproducciones de videos
 */
public class VideoPlaybackManager {
    
    private static VideoPlaybackManager instance;
    
    private VideoPlaybackManager() {}

    // Obtener instancia única
    public static synchronized VideoPlaybackManager getInstance() {
        if (instance == null) {
            instance = new VideoPlaybackManager();
        }
        return instance;
    }

    /**
     * Verifica y aumenta la reproducción del video si aún no se ha contado en la sesión.
     * @param session Sesión del usuario.
     * @param videoId ID del video.
     */
    public static void registrarReproduccion(HttpSession session, int videoId) {
        if (session == null) {
            return;
        }

        Set<Integer> videosVistos = (Set<Integer>) session.getAttribute("videosVistos");
        if (videosVistos == null) {
            videosVistos = new HashSet<>();
            session.setAttribute("videosVistos", videosVistos);
        }

        if (!videosVistos.contains(videoId)) {
            try {
                if (VideoDAO.incrementarReproducciones(videoId)) {
                    System.out.println("✅ Reproducción incrementada para video ID: " + videoId);
                    videosVistos.add(videoId);
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}