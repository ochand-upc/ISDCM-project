package com.isdcm.manager;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.PaginatedResponse;
import com.isdcm.model.Video;
import com.isdcm.model.VideoFilter;
import jakarta.ws.rs.core.GenericEntity;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class SearchManager {

    private static SearchManager instance;

    private SearchManager() {}

    public static synchronized SearchManager getInstance() {
        if (instance == null) {
            instance = new SearchManager();
        }
        return instance;
    }

    /**
    * Crear paginación para la búsqueda 
    */
    public static GenericEntity<PaginatedResponse<Video>> getPaginatedResponse(VideoFilter filter) 
            throws SQLException, IOException{
        int total = VideoDAO.countVideos(filter.getTitulo(), filter.getAutor(),
                filter.getFecha()
            );
            List<Video> items = VideoDAO.buscarVideos(filter.getTitulo(),
                filter.getAutor(),
                filter.getFecha(),
                filter.getSortField(),
                filter.getSortOrder(),
                filter.getPage(),
                filter.getPageSize()
            );
            PaginatedResponse<Video> page = new PaginatedResponse<>(total, items);
            return new GenericEntity<>(page){};
    }
}