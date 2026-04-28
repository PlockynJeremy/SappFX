package model;

public class StatArticle {

    private int    Id;
    private String Nom;
    private String Image;
    private int    nb_vues;
    private int    nb_ventes;

    public int    getId()       { return Id; }
    public String getNom()      { return Nom; }
    public String getImage()    { return Image; }
    public int    getNbVues()   { return nb_vues; }
    public int    getNbVentes() { return nb_ventes; }
}
