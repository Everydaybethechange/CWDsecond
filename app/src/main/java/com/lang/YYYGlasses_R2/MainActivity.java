package com.lang.YYYGlasses_R2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;

import android.speech.RecognizerIntent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.speech.tts.TextToSpeech;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lang.YYYGlasses_R2.Globals.t_solresult;


public class MainActivity extends AppCompatActivity {


    //----------------------------------------------------------(((----_________________-----)))-------------------------------
//    private String deviceName = null;
//    private String deviceAddress;
//    public static Handler handler;
//    public static BluetoothSocket mmSocket;
//    public static ConnectedThread connectedThread;
//    //public static CreateConnectThread createConnectThread;
//
//    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
//    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update(s)...,,j
    //dsfa

    //----------------------------------------------------------(((----_________________-----)))-------------------------------
    private static BluetoothAdapter mBluetoothAdapter;
    public static Handler handler;
    static boolean isConnected = false;
    static BluetoothGattCharacteristic foundCharacteristic = null;
    static BluetoothGatt bluetoothGattC = null;

    public static ConnectedThread connectedThread;
    public static BluetoothSocket mmSocket;
    static final String sESP32_MAC = "48:26:2C:52:D6:80";
    //   static final String sESP32_MAC = "24:6F:28:A1:87:7A";                                           // test
    static final String sCHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";

    static SharedPreferences sharedPreferences;
    final static String SP_TITLE = "SP_TITLE";
    final static String SP_LIST = "SP_LIST";
    public static CreateConnectThread createConnectThread;
    //static boolean isConnected = false;
   //new from service below
    static Context context;

    final static String TAG = "MyInfo";

   // private BluetoothAdapter mBluetoothAdapter;

    final int REQUEST_ENABLE_BT = 1;

    Intent iNotificationService = null;
    Intent iMainActivity = null;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update



    static boolean shouldRunning = false;
    //;;;;;;
    private static final int REQUEST_CODE = 100;
    public TextView voiceOutput;
    public TextView systemStatus;
    public TextView batteryLevel;     //Naya
    public TextView arduinoMsg; //yeh bhi naya
    //
    private String deviceName = null;
    private String deviceAddress;

    Button bStart;
    Button bSstop;
    Button buttonConnect;
    //Button bupdatebattery;
    Button b_updateInput;
    EditText etCmd;
    Button bSendCmd;
    //Button bthelisten;
    Button bWebUpdate;
    Button bRestart;
    //TextView voiceOutput;
    //voiceOutput= (TextView) findViewById(R.id.voiceOutput);
    Button bTestScreen1, bTestScreen2, bTestScreen3, bTestScreen4, bTestScreen5, bTestScreen6;

  //  Button bOpenList;
    TextToSpeech textToSpeech;

    Button bspeakText;

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("code code")
            .setContentText("context done (DLA)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);



    @Override public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arduinoMsg = findViewById(R.id.arduinoMsg);
        voiceOutput = findViewById(R.id.voiceOutput);
        batteryLevel = findViewById(R.id.tv_Status);
        systemStatus = findViewById(R.id.systemStatus);
        bspeakText = findViewById(R.id.btnText);

        //instantiating a object of the ClassB and passing tv
        // MainService obj = new MainService(voiceOutput);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null) {
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progree and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            //buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);


                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String aharduinoMsg = msg.obj.toString(); // Read message from Arduino
                        systemStatus.setText(aharduinoMsg);
                        arduinoMsg.setText(aharduinoMsg);
                        break;
                }
            }
        };

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);

                    Toast.makeText(getApplicationContext(),"No errors in speech synthesis", Toast.LENGTH_SHORT).show();


                }
            }
        });


            sharedPreferences = getPreferences(MODE_PRIVATE);


            buttonConnect = findViewById(R.id.buttonConnect);
            b_updateInput = findViewById(R.id.b_updateInput);
            batteryLevel = findViewById(R.id.tv_Status);
            //bthelisten = findViewById(R.id.b_thelisten); //T\d\f\0\0\0\0\0\0\0\0\0\o-_O_DDDOooo-O_O_O_O_O_O_ssssssssss
            etCmd = findViewById(R.id.et_Cmd);
            //bupdatebattery = findViewById(R.id.updateBattery);





            // If bluetooth supported (Rage BLE not supported)
//            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//                finish();
//            }

            final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();

            // Request enabling? Bluetooth if turned off
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // ------------- Notification Listener Permission - Msg/Notification screen - MSG from notifications --------------------
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }*/

            // --- MSG/Notifications - MSG from notifications - Service Start
            iNotificationService = new Intent(MainActivity.this, NotificationService.class);
            startService(iNotificationService);

            // --- MainService - Start
            iMainActivity = new Intent(MainActivity.this, MainActivity.class);
            startService(iMainActivity);




        buttonConnect.setOnClickListener(view -> {
            // Move to adapter list
            Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
            startActivity(intent);
        });
            bSendCmd.setOnClickListener(view -> {
                if (etCmd.getText() != null && !etCmd.getText().toString().isEmpty()) {
                   // MainService.ConnectedThread.write(etCmd.getText().toString());

                    tempowrite(etCmd.getText().toString());

                }
            });


       //     bOpenList.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ListActivity.class)));
           // bupdatebattery.setOnClickListener(view -> batteryLevel.setText(Globals.espBattery));

        b_updateInput.setOnClickListener(view -> {
            // Move to adapter list
            systemStatus.setText(Globals.system);
            arduinoMsg.setText(Globals.r_incoming);
            Toast.makeText(getApplicationContext(), Globals.system, Toast.LENGTH_SHORT).show();
//            if (Globals.bleOn == 1) {
//                toolbar.setSubtitle("Device connected");
//                Toast.makeText(getApplicationContext(), "Device Connected", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            } else if (Globals.bleOn ==0){
//                toolbar.setSubtitle("Device fails to connect");
//                Toast.makeText(getApplicationContext(), "Device failed to connect", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            } else {
//                toolbar.setSubtitle("No device connected");
//                Toast.makeText(getApplicationContext(), "Connect a device", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            }
        });

        bspeakText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textToSpeech.speak(Globals.t_typewordResultFINAL, TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), Globals.t_typewordResultFINAL, Toast.LENGTH_SHORT).show();

                }
            });
        }

      public void  speak () {
          textToSpeech.speak(Globals.r_typewordResult, TextToSpeech.QUEUE_FLUSH, null);
          Toast.makeText(getApplicationContext(), Globals.r_typewordResult, Toast.LENGTH_SHORT).show();
        }

    public void onClick (View v)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {


        }
    }
    public static void wait ( int ms)
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    @Override

    //Define an OnActivityResult method in our intent caller Activity//


    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        int ms;

        switch (requestCode) {
            case REQUEST_CODE: {

//If RESULT_OK is returned...//

                if (resultCode == RESULT_OK && null != data) {

//...then retrieve the ArrayList//

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

//Update our TextView//


                    voiceOutput.setText(result.get(0));
                    t_solresult = result.get(0);
                    connectedThread.write(result.get(0));
                    wait(1000);
                    Toast.makeText(getApplicationContext(), t_solresult, Toast.LENGTH_SHORT).show();

                    //sendMessageToService(result.get(0));


                }

            }

        }




    }
    static void saveSharedPreferencesTitle(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SP_TITLE, s);
        editor.apply();
    }

    static String readSharedPreferencesTitle() {
        return sharedPreferences.getString(SP_TITLE, "");
    }

    static void saveSharedPreferencesList(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SP_LIST, s);
        editor.apply();
    }

    static String readSharedPreferencesList() {
        return sharedPreferences.getString(SP_LIST, "");
    }

    static void tempowrite (String input) {
        Globals.t_gowrite = input;
        Globals.t_sendnosend = 1;
    }


    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show());
    }
    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        MainActivity.disconnect();
        if (iNotificationService    != null) stopService(iNotificationService);
        if (iMainActivity            != null) stopService(iMainActivity);
    }


    @Override protected void onStop() {
        super.onStop();
    }
    static class ESP32Status {
        static final int OLED_ON = 0;
        static final int OLED_OFF = 1;
    }

    //static class ESP32 {
        static int STATUS = MainActivity.ESP32Status.OLED_ON;

        // Saved last evoked screen type (when user go to one of the screens)
        static class ScreenState {
            static final long TIME_IDLE_GO_HOME = 90000; // After this time with OLED off go back to mainscreen
            static long TIME_LAST_INVOKE = 0;

            static final int SCREEN_MAIN = 0;
            static final int SCREEN_MUSI = 1;
            static final int SCREEN_MSGN = 2;
            static final int SCREEN_CALL = 3;
            static final int SCREEN_LIST = 4;

            static final int SCREEN_NAVI = 5;                                                       //disabled

            static int CURRENT_SCREEN = SCREEN_MAIN;

            static String
                    prev_MsgScreen = "#1|280|None|No recent msg",
                    prev_CallScreen = "#2|No recent calls",
                    prev_NaviScreen = "#3|0|0|0|280",
                    prev_ListScreen = "#4|257|No List|280|No list",
                    prev_MusicScreen = "#5|182|No music playing|210|214";
        }


        //TODO  ********************************** Startup section - write cmds which will be sent after successful connection ***************************************8
//        static void startupSection() {
//            new Thread() {
//                public void run() {
//                    try {
//                        MainActivity.tempowrite("#MX=20");
//                        sleep(1000);
//                        MainActivity.tempowrite("#TST=100");
//                        sleep(1000);
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }
        //TODO ******************************************************************************************


        static boolean oldconnect(String address) {
            Log.i(TAG, "Connecting to: " + address);
            if (mBluetoothAdapter == null || address == null) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;

            }

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            // BluetoothGatt bluetoothGatt = device.connectGatt(context, false, mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");

            return true;
        }

        static void disconnect() {
            if (bluetoothGattC != null) {
                bluetoothGattC.disconnect();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGattC.close();
                        bluetoothGattC = null;
                        isConnected = false;
                    }
                }, 1000);
            }
        }

        static boolean isConnected() {
            return foundCharacteristic != null && bluetoothGattC != null && isConnected;
        }


        //        static void writetoESP(String input) {
//            byte[] bytes = input.getBytes(); //converts entered String into bytes
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) {
//                Log.e("Send Error", "Unable to send message", e);
//            }
//        }
        static void showLastScreen(int screenId) {
            switch (screenId) {
                case ScreenState.SCREEN_MAIN:
                    onHome();
                    break;
//                case ScreenState.SCREEN_NAVI: writetoESP(ScreenState.prev_NaviScreen) ; break;    // For now disabled maybe upgrade in future? :) Or YOU gonna do it? Are you? .. Do it :P

                case ScreenState.SCREEN_MSGN:
                    MainActivity.tempowrite(ScreenState.prev_MsgScreen);
                    break;
                case ScreenState.SCREEN_CALL:
                    MainActivity.tempowrite(ScreenState.prev_CallScreen);
                    break;
                case ScreenState.SCREEN_LIST:
                    onList();
                    break;
                case ScreenState.SCREEN_MUSI:
                    MainActivity.tempowrite(ScreenState.prev_MusicScreen);
                    break;
            }
        }
        static void onHome() {
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String HH = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
            String mm = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());

//            sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
            String date = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date());

            mainScreen(HH, mm, Globals.t_solresult, "-1", "");
        }
        static int currentListPos=0;
        static void onList() {
            String list = MainActivity.readSharedPreferencesList();

            ArrayList<String> aList = new ArrayList<>();

            Pattern p = Pattern.compile("-(.*)");
            Matcher m = p.matcher(list);

            while (m.find()) {
                aList.add(m.group(1));
            }

            if (currentListPos >= aList.size()) currentListPos = 0;

            listScreen("257", MainActivity.readSharedPreferencesTitle(), "221", aList.get(currentListPos).trim());
        }


        /* --- Screens --- */
        static void mainScreen(String HH, String mm, String date, String symbol, String degrees) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_MAIN;
            String t = "#0|" + HH + "|" + mm + "|" + date + "|" + symbol + "|" + degrees;
            MainActivity.tempowrite(t);
        }

        static void msgNotiScreen(String symbol, String from, String text) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_MSGN;
            String t = "#1|" + symbol + "|" + from + "|" + text;
            ScreenState.prev_MsgScreen = t;
            MainActivity.tempowrite(t);
        }

        //contains t_solresult
        static void callScreen(String from) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_CALL;
            String t = "#2|" + t_solresult;
            ScreenState.prev_CallScreen = t;
            MainActivity.tempowrite(t);
        }

        static void navScreen(String maxSpeed, String distance, String distanceToDes, String symbol) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_NAVI;
            String t = "#3|" + maxSpeed + "|" + distance + "|" + distanceToDes + "|" + symbol;
            ScreenState.prev_NaviScreen = t;
            MainActivity.tempowrite(t);
        }

        static void listScreen(String symbolMain, String title, String symbolSub, String text) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_LIST;
            String t = "#4|" + symbolMain + "|" + title + "|" + symbolSub + "|" + text;
            ScreenState.prev_ListScreen = t;
            MainActivity.tempowrite(t);
        }

        static void musicScreen(String musicIcon, String title, String symbolPlayStop, String symbolNext) {
            ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_MUSI;
            String t = "#5|" + musicIcon + "|" + title + "|" + symbolPlayStop + "|" + symbolNext;
            ScreenState.prev_MusicScreen = t;
            MainActivity.tempowrite(t);
        }

        static void sendToast (String msg) {
//          if (Looper.myLooper() == Looper.getMainLooper()) {
//              Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//
//          } else {
//              new Handler(Looper.getMainLooper()).post(new Runnable() {
//                  @Override
//                  public void run() {
//                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                  }
//              });
//          }


//
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                public void run() {
//                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//                }
//            });
        }



        /* --- On Touch Click on ESP32 --- */
        static void onInterrupt(String interruptName) {
            // @Override
            // public void handleMessage(Message msg){
//                                                                                static void onInterrupt(String interruptName) {

            Log.i(TAG, "Interrupt: " + interruptName);


            /* --- All Actions --- */

            // After time of idle go back to home
            if ((System.currentTimeMillis() - ScreenState.TIME_LAST_INVOKE) >= ScreenState.TIME_IDLE_GO_HOME)
                ScreenState.CURRENT_SCREEN = ScreenState.SCREEN_MAIN;

            // If oled off 1 click turn on last viewed screen
            if (interruptName.equals("#TS1") && STATUS == 1) {
                showLastScreen(ScreenState.CURRENT_SCREEN);

            } else if (interruptName.equals("#TS0")) {      // Long click changes the screens
                if (ScreenState.CURRENT_SCREEN >= 4) ScreenState.CURRENT_SCREEN = -1;
                showLastScreen(++ScreenState.CURRENT_SCREEN);
            }

            // Screen Actions
            else if (ScreenState.CURRENT_SCREEN == ScreenState.SCREEN_MUSI) {
//                if (interruptName.equals("#TS1")) {
//                    boolean isplaying = MainService.Music.musicToggle();
//
////                    "#5|182|No music playing|210|214"
//                    if (isplaying) musicScreen("182", "Music", "210", "214");
//                    else musicScreen("182", "Music", "211", "214");
//                } else if (interruptName.equals("#TS2")) {
//                    MainService.Music.musicNext();
//
//                }
            } else if (ScreenState.CURRENT_SCREEN == ScreenState.SCREEN_LIST) {
                if (interruptName.equals("#TS1")) {
                    currentListPos++;
                    onList();
                }
            }

            //Sense Word
            else if (interruptName.length() == 1) {
                if (interruptName.equals("]")) {
                    Globals.t_typewordResultFINAL = Globals.r_typewordResult;

                    Globals.r_typewordResult = "";


                } else {
                    Globals.r_typewordResult = Globals.r_typewordResult + interruptName;
                }
            }


            //Battery Status
            else if (interruptName.equals("#b")) {
                Globals.espBattery = interruptName.substring(2);
                MainActivity.tempowrite("o");

            }


            // Awoken - nvm which key pattern
            STATUS = 0;



//        static void onHome() {
////            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            String HH = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
//            String mm = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
//
////            sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
//            String date = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date());
//
//            mainScreen(HH, mm, date, "-1", "");
//        }




//        static void showLastScreen(int screenId) {
//            switch (screenId) {
//                case ScreenState.SCREEN_MAIN:
//                    onHome();
//                    break;
////                case ScreenState.SCREEN_NAVI: writetoESP(ScreenState.prev_NaviScreen) ; break;    // For now disabled maybe upgrade in future? :) Or YOU gonna do it? Are you? .. Do it :P
//
//                case ScreenState.SCREEN_MSGN:
//                    MainActivity.tempowrite(ScreenState.prev_MsgScreen);
//                    break;
//                case ScreenState.SCREEN_CALL:
//                    MainActivity.tempowrite(ScreenState.prev_CallScreen);
//                    break;
//                case ScreenState.SCREEN_LIST:
//                    onList();
//                    break;
//                case ScreenState.SCREEN_MUSI:
//                    MainActivity.tempowrite(ScreenState.prev_MusicScreen);
//                    break;
//            }
//        }



        }

        /* ============================ Thread to Create Bluetooth Connection =================================== */
        public static class CreateConnectThread extends Thread {

            public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
        /*
        Use a temporary object that is later assigned to mmSocket
        because mmSocket is final.
         */
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                BluetoothSocket tmp = null;
                UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

                try {
            /*
            Get a BluetoothSocket to oldconnect with the given BluetoothDevice.
            Due to Android device varieties,the method below may not work fo different devices.
            You should try using other methods i.e. :
             */
                    tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

                    // USED IN OG->tmp = deviceName.createRfcommSocketToServiceRecord(uuid);
                    //tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    Globals.system="Socket-Create method attempted";
                    Globals.r_incoming="Socket-create method attempted";

//                            } catch (IOException e) {
//                                Globals.system="Socket Create method failed";
//                                Globals.bleOn=0;
//
//                                Log.e(TAG, "Socket's create() method failed", e);
//                            }
                } catch (IOException e) {
                    Log.e(TAG, "Socket's create() method failed", e);
                }
                mmSocket = tmp;
            }

            // @RequiresApi(api = Build.VERSION_CODES.N)

            public void run() {
                // Cancel discovery because it otherwise slows down the connection.
                Globals.system="Service-run started";
                Globals.r_incoming="Service-run started";

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.cancelDiscovery();
                try {
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.

                    mmSocket.connect();
                    handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
                    Globals.system="success connecting through socket";
                    Log.e("Status", "Device connected");
                    // handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
                    //sendToast("Device Connected");
                    Globals.bleOn=1;
                } catch (IOException connectException) {
                    // Unable to connect; close the socket and return.
                    try {
                        mmSocket.close();
                        handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        Log.e("Status", "Cannot connect to device");
                        Globals.bleOn=0;
                        // handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        Globals.system="cannot connect to device";
                        //sendToast("Cannot connect to device");
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                    return;
                }
                //ESP32.startupSection();
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                connectedThread = new ConnectedThread(mmSocket);
                //IMPORTANT! REQUIRES HIGHER API LEVEL
                connectedThread.run();
            }

            // Closes the client socket and causes the thread to finish.
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the client socket", e);
                }
            }
        }


        ///* ============================ data transfer thread  ============================  */

        public static class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            public final OutputStream mmOutStream;
            private byte[] mmBuffer; // mmBuffer store for the stream


            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams, using temp objects because
                // member streams are final
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }


            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                //mmBuffer = new byte[1024];
                //int numBytes; // secondary backup for bytes returned from read()

                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes = 0; // bytes returned from read()
                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */

                        buffer[bytes] = (byte) mmInStream.read();
                        String readMessage;


                        if (buffer[bytes] == '\n') {
                            readMessage = new String(buffer, 0, bytes);
                            Log.e("Arduino Message", readMessage);
                             handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                            //sendToast("in- "+ readMessage);
                            Globals.system=readMessage;

                            bytes = 0;
                        } else {
                            bytes++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

            /* Call this from the main activity to send data to the remote device */



            public void write(String input) {
                byte[] bytes = input.getBytes(); //converts entered String into bytes
                try {
                    mmOutStream.write(bytes);

                } catch (IOException e) {
                    Log.e("Send Error", "Unable to send message", e);
                    Globals.system="unable to send";
                }
            }




            /* Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                }
            }
        }




        /* --- Music Player --- */
        static class Music {
            public static final String CMDTOGGLEPAUSE = "togglepause";
            public static final String CMDPAUSE = "pause";
            public static final String CMDPREVIOUS = "previous";
            public static final String CMDNEXT = "next";
            public static final String CMDSTOP = "stop";

            public static final String Service = "com.android.music.musicservicecommand";
            public static final String Command = "command";

            static boolean isMusicPlaying = false;

            static boolean musicToggle() {
                if (isMusicPlaying) musicPause();
                else musicPlay();

                isMusicPlaying = !isMusicPlaying;

                return isMusicPlaying;
            }

            static void musicPlay() {
                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
                MainActivity.context.sendOrderedBroadcast(i, null);

                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
                MainActivity.context.sendOrderedBroadcast(i, null);
            }

            static void musicPause() {
                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
                MainActivity.context.sendOrderedBroadcast(i, null);

                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
                MainActivity.context.sendOrderedBroadcast(i, null);
            }

            static void musicNext() {
                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
                MainActivity.context.sendOrderedBroadcast(i, null);

                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
                MainActivity.context.sendOrderedBroadcast(i, null);
            }
        }




    }//}

