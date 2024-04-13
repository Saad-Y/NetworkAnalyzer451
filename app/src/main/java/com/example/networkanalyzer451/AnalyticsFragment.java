package com.example.networkanalyzer451;
import java.util.List;
import android.view.View;
import com.jjoe64.graphview.GraphView;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class AnalyticsFragment extends Fragment {
    private TextInputEditText startDateEditText;
    private TextInputEditText endDateEditText;
    private DatabaseHelper databaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startDateEditText = view.findViewById(R.id.startDateEditText1);
        endDateEditText = view.findViewById(R.id.endDateEditText2);
        databaseHelper = new DatabaseHelper(requireContext());
        startDateEditText.setOnClickListener(v -> showStartDatePicker());
        endDateEditText.setOnClickListener(v -> showEndDatePicker());
        GraphView graphView = view.findViewById(R.id.idGraphView);
        fetchAndDisplayData();
    }
    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            String selectedDate = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth1);
            startDateEditText.setText(selectedDate);
            if (isDateInFuture(selectedDate)) {
                Toast.makeText(requireContext(), "Selected date cannot be in the future", Toast.LENGTH_SHORT).show();
                startDateEditText.setText("");
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            String selectedDate = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth1);
            endDateEditText.setText(selectedDate);
            if (isDateInFuture(selectedDate)) {
                Toast.makeText(requireContext(), "Selected date cannot be in the future", Toast.LENGTH_SHORT).show();
                endDateEditText.setText("");
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
    private boolean isDateInFuture(String date) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int dayOfMonth = Integer.parseInt(parts[2]);
        return year > currentYear ||
                (year == currentYear && month > currentMonth) ||
                (year == currentYear && month == currentMonth && dayOfMonth > currentDayOfMonth);
    }
    private void fetchAndDisplayData() {
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();
        // Check if both start and end dates are selected
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            List<CellData> cellDataList = databaseHelper.fetchDataFromDatabase(startDate, endDate);
            if (cellDataList != null && !cellDataList.isEmpty()) {
                calculateAndDisplayStatistics(cellDataList);
            } else {
                Toast.makeText(requireContext(), "No data available for selected date range", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show();
        }
    }
    private void calculateAndDisplayStatistics(List<CellData> cellDataList) {
    }
}
class CellData {
    private String operator;
    private int signalPower;
    private String networkType;
    public CellData(String operator, int signalPower, String networkType) {
        this.operator = operator;
        this.signalPower = signalPower;
        this.networkType = networkType;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public int getSignalPower() {
        return signalPower;
    }
    public void setSignalPower(int signalPower) {
        this.signalPower = signalPower;
    }
    public String getNetworkType() {
        return networkType;
    }
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
