package com.example.weather;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConnectionBLE extends AppCompatActivity {
    private int REQUEST_ENABLE_BT = 1;
    private int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final long SCAN_PERIOD = 10000;

    private Handler mHandler;
    private List<ScanFilter> filters;
    private ScanSettings settings;

    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        /** 블루투스를 관리하는 넘 **/
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /**어뎁터가 없다면 **/
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth를 지원하지 않는 기기 입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /** 어뎁터에서 scanner 가져오기 **/
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        /** 블루투스 스캐너를 돌릴수 있는 Handler 생성 **/
        //mHandler = new Handler();

        bluetoothLeScanner.startScan(scanCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /** 스캐닝 할때 사용될 필터 **/
        filters = new ArrayList<ScanFilter>();
        /** 스캐닝 세팅 **/
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        /** 블루투스 장치 스캔 시작 **/
        //scanLeDevice(true);

    }

    @Override
    protected void onPause() {
        super.onPause();

        /** 블루투스 장치 스캔 중지 **/
        //scanLeDevice(false);
        bluetoothLeScanner.stopScan(scanCallback);

    }

    @Override
    protected void onStart() {
        super.onStart();
        /** 블루투스가 살아있는지 체크 **/
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /** 권한 요청하는 부분 **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }

    /*************************
     * 블루투스 GATT 콜백
     ************************/
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("TEST", "gattCallback >> STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("TEST", "gattCallback >> STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("TEST", "gattCallback >> STATE_OTHER");
            }
        }
    };

    /****************
     * 스캐너 콜백
     ****************/
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String device = result.getDevice().getName();
            Log.i("TEST", "장치 이름 " + device);

            /** 내 블루투스 장치와 이름이 같다면 **/
            if ("HUSTAR_99".equals(device)) {
                Log.i("TEST", "HUSTAR 장치 찾음");
                bluetoothLeScanner.stopScan(scanCallback);
                result.getDevice().connectGatt(ConnectionBLE.this, true, bluetoothGattCallback);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("TEST", "스캔에 실패 하였습니다.");
            super.onScanFailed(errorCode);
        }
    };

//
//    /** 블루투스 LE 스캐너 **/
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi,
//                                     byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.i("TEST", "onLeScan >> " + device.toString());
//                            device.connectGatt(ConnectionBLE.this,true, bluetoothGattCallback);
//                        }
//                    });
//                }
//            };
//
//    /** 블루투스 스캔 **/
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (Build.VERSION.SDK_INT < 21) {
//                        bluetoothAdapter.stopLeScan(mLeScanCallback);
//                    } else {
//                        bluetoothLeScanner.stopScan(scanCallback);
//                    }
//                }
//            }, SCAN_PERIOD);
//            if (Build.VERSION.SDK_INT < 21) {
//                bluetoothAdapter.startLeScan(mLeScanCallback);
//            } else {
//                bluetoothLeScanner.startScan(filters, settings, scanCallback);
//            }
//        } else {
//            if (Build.VERSION.SDK_INT < 21) {
//                bluetoothAdapter.stopLeScan(mLeScanCallback);
//            } else {
//                bluetoothLeScanner.stopScan(scanCallback);
//            }
//        }
//    }
}

