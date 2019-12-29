package com.example.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class BLEService extends Service {
    private int REQUEST_ENABLE_BT = 1;
    private int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final long SCAN_PERIOD = 10000;

    private Handler mHandler;
    private List<ScanFilter> filters;
    private ScanSettings settings;

    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction("Action2");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, "channel2")
                    .setContentTitle("Bluetooth")
                    .setTicker("블루투스 인식중")
                    .setContentText("블루투스 인식중")
                    .setSmallIcon(R.drawable.ble)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(222, notification);

            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent i = new Intent(this, PermissionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Intent i2 = new Intent(this, PermissionActivity.class);
                i2.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i2);
            }


                    /** 블루투스를 관리하는 넘 **/
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /**어뎁터가 없다면 **/
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth를 지원하지 않는 기기 입니다.", Toast.LENGTH_SHORT).show();
            return flags;
        }

        /** 어뎁터에서 scanner 가져오기 **/
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        /** 블루투스 스캐너를 돌릴수 있는 Handler 생성 **/
        //mHandler = new Handler();

        //bluetoothLeScanner.startScan(scanCallback);

        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        } else {
            /** 블루투스가 살아있는지 체크 **/
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(enableBtIntent);
            }
        }
        bluetoothLeScanner.stopScan(scanCallback);
        bluetoothLeScanner.startScan(scanCallback);



        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (Settings.canDrawOverlays(this)) {
            // You have permission
            PermissionActivity.isOberlayed = true;
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
                    Log.i("BLE", "gattCallback >> STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("BLE", "gattCallback >> STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("BLE", "gattCallback >> STATE_OTHER");
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
            Log.i("BLE", "장치 이름 " + device);

            /** 내 블루투스 장치와 이름이 같다면 **/
            if ("HUSTAR_99".equals(device)) {
                Log.i("BLE", "HUSTAR 장치 찾음");
                bluetoothLeScanner.stopScan(scanCallback);
                result.getDevice().connectGatt(BLEService.this, true, bluetoothGattCallback);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("BLE", "스캔에 실패 하였습니다." + errorCode);
            super.onScanFailed(errorCode);
        }
    };
}
