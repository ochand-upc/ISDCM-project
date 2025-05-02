/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.model;

public class VideoFilter {
    private String titulo;
    private String autor;
    private String fecha;    // acepta "YYYY", "YYYY-MM" o "YYYY-MM-DD"
    private Integer page  = 1;
    private Integer pageSize = 20;
    private String sortField = "fecha";
    private String sortOrder = "desc";


    public VideoFilter() {}

    public String  getTitulo() { 
        return titulo;    
    }
    
    public void setTitulo(String t) {
        this.titulo = t; 
    }
    
    public String getAutor() {
        return autor;     
    }
    
    public void setAutor(String a) { 
        this.autor = a;  
    }
    
    public String getFecha() { 
        return fecha;     
    }
    
    public void setFecha(String f) {
        this.fecha = f; 
    }
    
    public Integer getPage() { 
        return page;      
    }
    
    public void setPage(Integer p) {
        this.page = p;  
    }
    
    public Integer getPageSize()  {
        return pageSize;  
    }
    
    public void setPageSize(Integer s) {
        this.pageSize = s; 
    }
    
    public String getSortField() {
        return sortField;
    }
    
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
