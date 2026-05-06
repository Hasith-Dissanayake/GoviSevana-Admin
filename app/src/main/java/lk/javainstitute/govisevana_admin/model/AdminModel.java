package lk.javainstitute.govisevana_admin.model;

public class AdminModel {
    private String phone;
    private String role;
    private String isActive;

    public AdminModel() {
    }

    public AdminModel(String phone, String role, String isActive) {
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
