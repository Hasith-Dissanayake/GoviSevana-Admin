package lk.javainstitute.govisevana_admin.model;


import java.util.List;

public class ProductModel {
    private String productId;
    private String title;
    private String description;
    private double price;
    private int quantity;
    private List<String> imageUrls;
    private String farmerName;
    private String farmerPhone;
    private boolean approved;

    // ✅ Default Constructor (Needed for Firestore)
    public ProductModel() {
    }

    // ✅ Constructor with all fields
    public ProductModel(String productId, String title, String description, double price, int quantity,
                        List<String> imageUrls, String farmerName, String farmerPhone, boolean approved) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrls = imageUrls;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.approved = approved;
    }

    // ✅ Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerPhone() {
        return farmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
