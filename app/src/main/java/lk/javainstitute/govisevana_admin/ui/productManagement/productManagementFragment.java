package lk.javainstitute.govisevana_admin.ui.productManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.adapter.ProductManagementAdapter;
import lk.javainstitute.govisevana_admin.model.ProductModel;

public class productManagementFragment extends Fragment {
    private RecyclerView productRecyclerView;
    private ProductManagementAdapter productAdapter;
    private List<ProductModel> productList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button refreshButton;
    public productManagementFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);

        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        refreshButton = view.findViewById(R.id.refreshButton);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        productAdapter = new ProductManagementAdapter(getContext(), productList);
        productRecyclerView.setAdapter(productAdapter);
        loadProducts();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProducts();
            }
        });
        return view;
    }
    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductModel product = document.toObject(ProductModel.class);
                            product.setProductId(document.getId());
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
