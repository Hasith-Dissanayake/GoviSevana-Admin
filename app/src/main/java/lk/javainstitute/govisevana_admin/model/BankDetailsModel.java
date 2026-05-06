package lk.javainstitute.govisevana_admin.model;

public class BankDetailsModel {
    private String accountHolderName;
    private String accountNumber;
    private String bankName;
    private String branchName;

    public BankDetailsModel() {
        // Empty constructor for Firestore
    }

    public BankDetailsModel(String accountHolderName, String accountNumber, String bankName, String branchName) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.branchName = branchName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
