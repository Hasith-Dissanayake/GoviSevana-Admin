package lk.javainstitute.govisevana_admin.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import lk.javainstitute.govisevana_admin.R;

public class HomeFragment extends Fragment {

    private BarChart barChart;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        barChart = view.findViewById(R.id.barChart);
        db = FirebaseFirestore.getInstance();

        loadMonthlyRegisteredUsers();

        return view;
    }

    private void loadMonthlyRegisteredUsers() {
        db.collection("users")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Integer> monthlyUserCounts = new HashMap<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.contains("createdTimestamp")) {
                                long timestamp = document.getTimestamp("createdTimestamp").toDate().getTime();

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(timestamp);
                                String monthYear = sdf.format(calendar.getTime()); // Format as "January 2025"

                                monthlyUserCounts.put(monthYear, monthlyUserCounts.getOrDefault(monthYear, 0) + 1);
                            }
                        }

                        plotData(monthlyUserCounts);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void plotData(Map<String, Integer> monthlyUserCounts) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        Map<String, Integer> sortedMonthlyUserCounts = monthlyUserCounts.entrySet().stream()
                .sorted(Comparator.comparing(entry -> {
                    try {
                        return monthYearFormat.parse(entry.getKey());
                    } catch (Exception e) {
                        return null;
                    }
                }))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new
                ));

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedMonthlyUserCounts.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Users");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();

        Description desc = new Description();
        desc.setText("Monthly Registered Users");
        barChart.setDescription(desc);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);

        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);
    }
}
