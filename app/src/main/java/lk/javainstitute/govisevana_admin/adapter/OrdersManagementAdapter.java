package lk.javainstitute.govisevana_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.model.BankDetailsModel;
import lk.javainstitute.govisevana_admin.model.OrderModel;
import lk.javainstitute.govisevana_admin.ui.ordersManagement.OnOrderUpdatedListener;

public class OrdersManagementAdapter extends RecyclerView.Adapter<OrdersManagementAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> orderList;
    private FirebaseFirestore db;
    private OnOrderUpdatedListener orderUpdatedListener;

    public OrdersManagementAdapter(Context context, List<OrderModel> orderList, OnOrderUpdatedListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
        this.orderUpdatedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.farmerId.setText("Farmer ID: " + order.getFarmerId());
        holder.paymentStatus.setText("Payment: " + order.getPaymentStatus());
        holder.totalAmount.setText("Total Amount: RS." + String.format("%.2f", order.getTotalAmount()));
        holder.orderStatus.setText("Order Status: " + order.getStatus());

        // Fetch bank details using farmerId
        db.collection("bank_details")
                .document(order.getFarmerId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        BankDetailsModel bankDetails = documentSnapshot.toObject(BankDetailsModel.class);
                        if (bankDetails != null) {
                            holder.bankDetails.setText("Bank: " + bankDetails.getBankName());
                            holder.bankBranch.setText("Branch: " + bankDetails.getBranchName());
                            holder.accountHolderName.setText("Account Holder: " + bankDetails.getAccountHolderName());
                            holder.accountNumber.setText("Account Number: " + bankDetails.getAccountNumber());
                        }
                    } else {
                        setBankDetailsUnavailable(holder);
                    }
                })
                .addOnFailureListener(e -> setBankDetailsUnavailable(holder));

        // Mark as Paid button action
        holder.markPaidButton.setOnClickListener(v -> {
            db.collection("orders").document(order.getOrderId())
                    .update("paymentStatus", "Paid")
                    .addOnSuccessListener(aVoid -> {
                        order.setPaymentStatus("Paid");
                        notifyDataSetChanged();
                        Toast.makeText(context, "Order marked as Paid", Toast.LENGTH_SHORT).show();

                        // Notify fragment to reload orders
                        if (orderUpdatedListener != null) {
                            orderUpdatedListener.onOrderUpdated();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to update payment status!", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void setBankDetailsUnavailable(ViewHolder holder) {
        holder.bankDetails.setText("Bank: Not Available");
        holder.bankBranch.setText("Branch: Not Available");
        holder.accountHolderName.setText("Account Holder: Not Available");
        holder.accountNumber.setText("Account Number: Not Available");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, farmerId, paymentStatus, totalAmount, orderStatus;
        TextView bankDetails, bankBranch, accountHolderName, accountNumber;
        Button markPaidButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            farmerId = itemView.findViewById(R.id.farmerId);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            bankDetails = itemView.findViewById(R.id.bankDetails);
            bankBranch = itemView.findViewById(R.id.bankBranch);
            accountHolderName = itemView.findViewById(R.id.accountHolderName);
            accountNumber = itemView.findViewById(R.id.accountNumber);
            markPaidButton = itemView.findViewById(R.id.markPaidButton);
        }
    }
}
