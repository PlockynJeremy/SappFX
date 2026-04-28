package model;

public class Article {

    private int Id;
    private String Nom;
    private String Type;
    private String couleur;
    private double Prix;
    private String Image;
    private String Description;
    private int Stock;
    private String Taille;
    private String Genre;

    public int getId() {
        return Id;
    }

    public String getNom() {
        return Nom;
    }

    public String getType() {
        return Type;
    }

    public String getCouleur() {
        return couleur;
    }

    public double getPrix() {
        return Prix;
    }

    public String getImage() {
        return Image;
    }

    public String getDescription() {
        return Description;
    }

    public int getStock() {
        return Stock;
    }

    public String getTaille() {
        return Taille;
    }

    public String getGenre() {
        return Genre;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public void setNom(String nom) {
        this.Nom = nom;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setPrix(double prix) {
        this.Prix = prix;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public void setStock(int stock) {
        this.Stock = stock;
    }

    public void setTaille(String taille) {
        this.Taille = taille;
    }

    public void setGenre(String genre) {
        this.Genre = genre;
    }

    @Override
    public String toString() {
        return "Article{Id=" + Id + ", Nom='" + Nom + "', Type='" + Type + "', Prix=" + Prix + "}";
    }
}
