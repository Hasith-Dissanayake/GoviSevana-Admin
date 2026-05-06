package lk.javainstitute.govisevana_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.model.ProductModel;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ViewHolder> {
    private Context context;
    private List<ProductModel> productList;
    private FirebaseFirestore db;
    public ProductManagementAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.productName.setText(product.getTitle());
        holder.productPrice.setText("Price: LKR " + product.getPrice());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());
        holder.farmerName.setText("Farmer: " + product.getFarmerName());
        holder.farmerPhone.setText("Phone: " + product.getFarmerPhone());

        if (!product.getImageUrls().isEmpty()) {
            Picasso.get().load(product.getImageUrls().get(0)).placeholder(R.drawable.placeholder_image).into(holder.productImage);
        }

        if (product.isApproved()) {
            holder.approveButton.setVisibility(View.GONE);
            holder.rejectButton.setVisibility(View.VISIBLE);
        } else {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setVisibility(View.GONE);
        }

        holder.approveButton.setOnClickListener(v -> updateProductApproval(product.getProductId(), true, holder));
        holder.rejectButton.setOnClickListener(v -> updateProductApproval(product.getProductId(), false, holder));
        holder.deleteButton.setOnClickListener(v -> deleteProduct(product.getProductId(), position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    private void updateProductApproval(String productId, boolean isApproved, ViewHolder holder) {
        db.collection("products").document(productId)
                .update("approved", isApproved)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, isApproved ? "Product Approved" : "Product Rejected", Toast.LENGTH_SHORT).show();
                        holder.approveButton.setVisibility(isApproved ? View.GONE : View.VISIBLE);
                        holder.rejectButton.setVisibility(isApproved ? View.VISIBLE : View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void deleteProduct(String productId, int position) {
        db.collection("products").document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        productList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, farmerName, farmerPhone;
        ImageView productImage;
        Button approveButton, rejectButton, deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            farmerName = itemView.findViewById(R.id.farmerName);
            farmerPhone = itemView.findViewById(R.id.farmerPhone);
            productImage = itemView.findViewById(R.id.productImage);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
