package com.example.networkanalyzer451;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    private TelephonyManager telephonyManager;
    private PhoneStateListener signalStrengthListener;
    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    public DataFragment() {
        // Required empty public constructor
    }

    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        initializeTextViews(view);
        checkAndRequestPermissions();
        return view;
    }

    private void initializeTextViews(View view) {
        operatorTextView = view.findViewById(R.id.operatorTextView);
        signalPowerTextView = view.findViewById(R.id.signalPowerTextView);
        snrTextView = view.findViewById(R.id.snrTextView);
        networkTypeTextView = view.findViewById(R.id.networkTypeTextView);
        frequencyBandTextView = view.findViewById(R.id.frequencyBandTextView);
        cellIdTextView = view.findViewById(R.id.cellIdTextView);
        timeStampTextView = view.findViewById(R.id.timeStampTextView);
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            setupTelephony();
        }
    }

    private void setupTelephony() {
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            updateOperatorInfo();
            initializeSignalStrengthListener();
            scheduleNetworkInfoRefresh();
            updateNetworkTypeText();
        }
    }

    private void updateNetworkTypeText() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String networkTypeText = getNetworkTypeString(telephonyManager.getDataNetworkType());
            networkTypeTextView.setText(networkTypeText);
        } else {
            networkTypeTextView.setText("Network Type: Permission not granted");
            // You may also want to request the permission again if it's critical for your app.
        }
    }


    private String getNetworkTypeString(int networkType) {
        String typeString;
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                typeString = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                typeString = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                typeString = "4G";
                break;
            default:
                typeString = "Unknown";
                break;
        }
        return typeString;
    }


    private void updateOperatorInfo() {
        if (telephonyManager != null) {
            String operatorName = telephonyManager.getNetworkOperatorName();
            operatorTextView.setText(getString(R.string.operator_text, operatorName));
        }
    }


    private void initializeSignalStrengthListener() {
        signalStrengthListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                updateSignalStrength(signalStrength);
                updateCellInfo();
            }
        };
        telephonyManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void updateSignalStrength(SignalStrength signalStrength) {
        int dBm = extractSignalStrengthDbm(signalStrength);
        // Since we are posting to the handler, there is no need to call post() again here.
        signalPowerTextView.setText("Signal Power: " + dBm + " dBm");
    }

    private int extractSignalStrengthDbm(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            int asu = signalStrength.getGsmSignalStrength();
            if (asu != 99) {
                return -113 + 2 * asu; // Convert to dBm
            }
        } else {
            return signalStrength.getCdmaDbm();
        }
        return -1; // Default or unknown dBm
    }

    private void updateCellInfo() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider providing feedback to the user that permission is required
            return;
        }

        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
        if (cellInfos != null && !cellInfos.isEmpty()) {
            for (CellInfo info : cellInfos) {
                if (info instanceof CellInfoLte) {
                    CellInfoLte lteInfo = (CellInfoLte) info;
                    CellSignalStrengthLte lte = lteInfo.getCellSignalStrength();
                    int snr = lte.getRssnr(); // Ensure this value is in a valid range before displaying

                    // Check if SNR is valid, assuming a valid range is -30 to +30
                    if (snr >= -30 && snr <= 30) {
                        snrTextView.setText("SNR: " + snr + " dB");
                    } else {
                        snrTextView.setText("SNR: Not Available");
                    }

                    cellIdTextView.setText("Cell ID: " + lteInfo.getCellIdentity().getCi());
                    networkTypeTextView.setText("Network Type: LTE");
                    break; // Break after finding LTE info
                }
                // Add additional checks here for other cell info types if needed
            }
        } else {
            snrTextView.setText("SNR: Not Available");
            cellIdTextView.setText("Cell ID: Not Available");
            networkTypeTextView.setText("Network Type: Not Available");
        }
    }

    private void scheduleNetworkInfoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                updateNetworkInfo();
                handler.postDelayed(this, 2000); // Refresh every 2 seconds instead of 10
            }
        };
        handler.post(refreshRunnable);
    }

    private void updateNetworkInfo() {
        if (telephonyManager != null) {
            String operatorName = telephonyManager.getNetworkOperatorName();
            operatorTextView.setText("Operator: " + operatorName);

            String currentTime = DateFormat.format("dd MMM yyyy hh:mm:ss", new Date()).toString();
            timeStampTextView.setText("Time Stamp: " + currentTime);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (allPermissionsGranted(grantResults)) {
                setupTelephony();
            } else {
                // Provide feedback that permission is necessary
                operatorTextView.setText("Permissions not granted. Unable to display all data.");
                signalPowerTextView.setText("Signal Power: Not Available");
                snrTextView.setText("SNR: Not Available");
                networkTypeTextView.setText("Network Type: Not Available");
                cellIdTextView.setText("Cell ID: Not Available");
                // Consider providing a rationale or guiding the user to settings
            }
        }
    }


    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (telephonyManager != null) {
            telephonyManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_NONE);
        }
        handler.removeCallbacks(refreshRunnable);
    }
}




