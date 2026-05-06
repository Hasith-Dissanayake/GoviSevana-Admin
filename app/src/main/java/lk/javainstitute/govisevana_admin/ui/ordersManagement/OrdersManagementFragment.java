package lk.javainstitute.govisevana_admin.ui.ordersManagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.adapter.OrdersManagementAdapter;
import lk.javainstitute.govisevana_admin.model.OrderModel;



import android.os.Environment;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;




public class OrdersManagementFragment extends Fragment implements OnOrderUpdatedListener {

    private RecyclerView ordersRecyclerView;
    private OrdersManagementAdapter ordersAdapter;
    private List<OrderModel> orderList = new ArrayList<>();
    private FirebaseFirestore db;
    private ToggleButton toggleFilter;

    private Button downloadReportButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_management, container, false);

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        toggleFilter = view.findViewById(R.id.toggleFilter);
        downloadReportButton = view.findViewById(R.id.downloadReportButton); // Initialize button

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        ordersAdapter = new OrdersManagementAdapter(getContext(), orderList, this);
        ordersRecyclerView.setAdapter(ordersAdapter);

        // Load Unpaid Orders by Default
        loadOrders(false);

        // Toggle Filter Listener
        toggleFilter.setOnCheckedChangeListener((buttonView, isChecked) -> loadOrders(isChecked));

        // Set Download Report Button Listener
        downloadReportButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                generateReport();
            }
        });

        return view;
    }

    private void generateReport() {
        try {
            // Get the storage directory
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Reports");
            if (!folder.exists()) folder.mkdirs();

            // Generate unique filename with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File file = new File(folder, "orders_report_" + timestamp + ".pdf");

            // Create PDF Writer
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Orders Report\n\n")
                    .setFontSize(18)
                    .setBold());

            // Create table with 5 columns
            float[] columnWidths = {120, 100, 100, 100, 120};
            Table table = new Table(columnWidths);
            table.addCell(new Cell().add(new Paragraph("Order ID")));
            table.addCell(new Cell().add(new Paragraph("Farmer ID")));
            table.addCell(new Cell().add(new Paragraph("Status")));
            table.addCell(new Cell().add(new Paragraph("Payment")));
            table.addCell(new Cell().add(new Paragraph("Total Amount")));

            // Add data rows
            for (OrderModel order : orderList) {
                table.addCell(new Cell().add(new Paragraph(order.getOrderId())));
                table.addCell(new Cell().add(new Paragraph(order.getFarmerId())));
                table.addCell(new Cell().add(new Paragraph(order.getStatus())));
                table.addCell(new Cell().add(new Paragraph(order.getPaymentStatus())));
                table.addCell(new Cell().add(new Paragraph("RS. " + String.format("%.2f", order.getTotalAmount()))));
            }

            // Add table to document
            document.add(table);
            document.close();

            // Show success message
            Toast.makeText(getContext(), "Report saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error generating report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onOrderUpdated() {
        loadOrders(toggleFilter.isChecked()); // Reload based on filter status
    }

    private void loadOrders(boolean showPaid) {
        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        OrderModel order = document.toObject(OrderModel.class);

                        // Handle missing paymentStatus (default to "Unpaid")
                        if (order.getPaymentStatus() == null || order.getPaymentStatus().isEmpty()) {
                            order.setPaymentStatus("Unpaid");
                        }

                        // Apply filter (Paid or Unpaid)
                        if ((showPaid && "Paid".equals(order.getPaymentStatus())) ||
                                (!showPaid && "Unpaid".equals(order.getPaymentStatus()))) {
                            orderList.add(order);
                        }
                    }
                    ordersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
