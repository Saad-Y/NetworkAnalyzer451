//package com.example.networkanalyzer451;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link DataFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class DataFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public DataFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment DataFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static DataFragment newInstance(String param1, String param2) {
//        DataFragment fragment = new DataFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_data, container, false);
//    }
//}


package com.example.networkanalyzer451;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.List;

public class DataFragment extends Fragment {

    private TextView operatorTextView;
    private TextView signalPowerTextView;
    private TextView snrTextView;
    private TextView networkTypeTextView;
    private TextView frequencyBandTextView;
    private TextView cellIdTextView;
    private TextView timeStampTextView;

    public DataFragment() {
        // Required empty public constructor
    }

    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        operatorTextView = view.findViewById(R.id.operatorTextView);
        signalPowerTextView = view.findViewById(R.id.signalPowerTextView);
        snrTextView = view.findViewById(R.id.snrTextView);
        networkTypeTextView = view.findViewById(R.id.networkTypeTextView);
        frequencyBandTextView = view.findViewById(R.id.frequencyBandTextView);
        cellIdTextView = view.findViewById(R.id.cellIdTextView);
        timeStampTextView = view.findViewById(R.id.timeStampTextView);

        // Check for phone state permission before trying to access telephony services
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            displayNetworkInfo();
        } else {
            // You'll need to request the permission here if not already done.
            // Remember to handle the permission result in onRequestPermissionsResult
        }

        return view;
    }

    private void displayNetworkInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        // Ensure we have access to telephony data
        if (telephonyManager != null) {
            String operatorName = telephonyManager.getNetworkOperatorName();
            operatorTextView.setText("Operator: " + operatorName);

            // For API level 17 and above, you can access signal strength as part of CellInfo
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
                // You will need to loop through this list and get details based on your carrier and cell type
                // This is a placeholder for illustration purposes
                // ...
            }

            String networkType = getNetworkTypeString(telephonyManager.getNetworkType());
            networkTypeTextView.setText("Network Type: " + networkType);

            // Time stamp
            String timeStamp = DateFormat.format("dd MMM yyyy hh:mm:ss", new Date()).toString();
            timeStampTextView.setText("Time Stamp: " + timeStamp);

            // Frequency band and Cell ID are specific to network technology (GSM, CDMA, LTE, etc.)
            // and require processing the CellInfo list as appropriate
            // ...

            // TODO: Replace this with real data fetching and populating logic for each TextView
        }
    }

    private String getNetworkTypeString(int networkType) {
        // This method converts network type integer to human-readable network type
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            // ... add cases for other network types as needed
            default:
                return "Unknown";
        }
    }
}
