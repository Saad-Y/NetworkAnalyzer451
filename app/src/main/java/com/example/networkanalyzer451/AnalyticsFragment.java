package com.example.networkanalyzer451;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AnalyticsFragment extends Fragment {

    private TextInputEditText startDateEditText; // Declare as class-level variable

    // Initialize your GraphView and other views here

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get reference to the start date and end date EditTexts
        startDateEditText = view.findViewById(R.id.startDateEditText1);
        TextInputEditText endDateEditText = view.findViewById(R.id.endDateEditText2);

        // Set onClickListener for start date EditText to show start date picker
        startDateEditText.setOnClickListener(v -> showStartDatePicker());

        // Set onClickListener for end date EditText to show end date picker
        endDateEditText.setOnClickListener(v -> showEndDatePicker());

        fetchAndDisplayData();
    }

    private void showStartDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog for start date
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            // Set selected date to EditText
            String selectedDate = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth1);
            startDateEditText.setText(selectedDate);

            // Check if any date is in the future
            if (isDateInFuture(selectedDate)) {
                Toast.makeText(requireContext(), "Selected date cannot be in the future", Toast.LENGTH_SHORT).show();
                startDateEditText.setText(""); // Clear the selected date
            }
        }, year, month, dayOfMonth);

        // Show date picker dialog
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        // Get reference to the end date EditText
        TextInputEditText endDateEditText = getView().findViewById(R.id.endDateEditText2);

        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog for end date
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            // Set selected date to EditText
            String selectedDate = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth1);
            endDateEditText.setText(selectedDate);

            // Check if any date is in the future
            if (isDateInFuture(selectedDate)) {
                Toast.makeText(requireContext(), "Selected date cannot be in the future", Toast.LENGTH_SHORT).show();
                endDateEditText.setText(""); // Clear the selected date
            }

            // Check if end date is before start date
            //if (isEndDateBeforeStartDate(selectedDate, startDateEditText.getText().toString())) {
                //Toast.makeText(requireContext(), "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                //endDateEditText.setText(""); // Clear the end date
            //}
        }, year, month, dayOfMonth);

        // Show date picker dialog
        datePickerDialog.show();
    }

    // Method to check if the date is in the future
    private boolean isDateInFuture(String date) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Month starts from 0
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Extract year, month, and day from the selected date
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int dayOfMonth = Integer.parseInt(parts[2]);

        // Compare with current date
        return year > currentYear ||
                (year == currentYear && month > currentMonth) ||
                (year == currentYear && month == currentMonth && dayOfMonth > currentDayOfMonth);
    }

    // Method to check if the end date is before the start date
    //private boolean isEndDateBeforeStartDate(String startDate, String endDate) {
    //    try {
    //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    //       Date startDateObj = sdf.parse(startDate);
    //        Date endDateObj = sdf.parse(endDate);
            // Compare dates
    //        return endDateObj.before(startDateObj);
    //    } catch (ParseException e) {
    //       e.printStackTrace();
    //        return false; // Return false if there's an error parsing dates
    //    }
    //}


    private void fetchAndDisplayData() {
        // Retrieve data from SQLite database based on selected date range
        List<CellData> cellDataList = fetchDataFromDatabase();

        if (cellDataList != null && !cellDataList.isEmpty()) {
            // Calculate statistics and display on the graphs
            calculateAndDisplayStatistics(cellDataList);
        } else {
            // Show a message indicating no data available
            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
        }
    }

    private List<CellData> fetchDataFromDatabase() {
        // Implement logic to retrieve data from SQLite database based on selected date range
        // You can use a SQLiteOpenHelper or a Room Database to handle database operations
        // Return a list of CellData objects containing the retrieved data
        // For demonstration purposes, let's return a mock list of CellData
        return MockDataGenerator.generateMockCellData();
    }

    private void calculateAndDisplayStatistics(List<CellData> cellDataList) {
        // Implement logic to calculate statistics based on the retrieved data
        // For demonstration purposes, let's just display the count of data points
        int dataPointCount = cellDataList.size();
        Toast.makeText(requireContext(), "Data points count: " + dataPointCount, Toast.LENGTH_SHORT).show();
    }
}

class MockDataGenerator {
    public static List<CellData> generateMockCellData() {
        List<CellData> cellDataList = new ArrayList<>();

        // Generate mock cell data
        cellDataList.add(new CellData("Operator A", 80, "4G"));
        cellDataList.add(new CellData("Operator B", 70, "3G"));
        cellDataList.add(new CellData("Operator C", 60, "2G"));
        // Add more mock data as needed

        return cellDataList;
    }
}

class CellData {
    private String operator;
    private int signalPower;
    private String networkType;
    // Add more fields as needed

    // Constructor
    public CellData(String operator, int signalPower, String networkType) {
        this.operator = operator;
        this.signalPower = signalPower;
        this.networkType = networkType;
    }

    // Getters and setters
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
