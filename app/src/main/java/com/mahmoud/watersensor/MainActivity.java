package com.mahmoud.watersensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView txt_amount,txt_cost;
    RecyclerView recycler;

    //Button but_add;
    ServiceAdupter serviceAdupter;


    StringBuilder message;

    Handler handler;

    BluetoothAdapter myBluetooth;
    Set<BluetoothDevice> piaredDevices;
    BluetoothSocket btSocket;

    String addrss, name;

    FirebaseFirestore firebaseFirestore;
    Thread thread;


    BluetoothDevice mBTDevice;
    private static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startConnection();
        }catch (Exception e){}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_cost=findViewById(R.id.txt_cost);
        txt_amount = findViewById(R.id.txt_amount);
        recycler = findViewById(R.id.recycler);
    //    but_add = findViewById(R.id.but_add);

        serviceAdupter = new ServiceAdupter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(serviceAdupter);

        //    FirebaseApp.initializeApp(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        showData();

        //بجيب كل الاجهزة الي عامل معاها بير و ادولا على السينسور و اخزنه هو و العنوان

        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            addrss = myBluetooth.getAddress();
            piaredDevices = myBluetooth.getBondedDevices();
            if (piaredDevices.size() > 0) {
                for (BluetoothDevice bt : piaredDevices) {
                    addrss = bt.getAddress().toString();
                    name = bt.getName().toString();

                    //   Toast.makeText(this, addrss+" "+name, Toast.LENGTH_SHORT).show();
                    if (addrss.equals("") || name.equals("HC-05")) {
                        mBTDevice = bt;
                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                        ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
                        mBTDevice.getUuids();
                        for (ParcelUuid uuid : uuids) {
                            //  Toast.makeText(this, uuid.getUuid().toString(), Toast.LENGTH_SHORT).show();
                            MY_UUID_INSECURE = uuid.getUuid();
                        }
                        break;
                    }

                }
            }
        } catch (Exception we) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        message = new StringBuilder();


        startConnection();
        handler = new Handler();


    }

    private void showData() {
        firebaseFirestore.collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Service> services = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        services.add(document.toObject(Service.class));
                    }
                    serviceAdupter.setitemsList(services);
                }
            }
        });
    }

    public void startConnection() {
        try {
            startBTConnection(mBTDevice, MY_UUID_INSECURE);
        } catch (Exception e) {

        }
    }

    //starting service method
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        if (device != null) {

            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            device = myBluetooth.getRemoteDevice(device.getAddress());//connects to the device's address and check if it's available
            try {
                btSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));//create a RFCOMM (SPP) connection
                btSocket.connect();
                Connection connection = new Connection(btSocket);
                thread = new Thread(connection);
                thread.start();
            } catch (IOException e) {
                // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    btSocket.close();
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));//create a RFCOMM (SPP) connection
                    btSocket.connect();
                    Connection connection = new Connection(btSocket);
                    thread = new Thread(connection);
                    thread.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } else {
            //  Toast.makeText(this, "There is no device to cinnect", Toast.LENGTH_SHORT).show();
        }
       /* but_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddServiceActivity.class));
            }
        });*/

    }

    public class Connection extends Thread {
            BluetoothSocket socket;

            public Connection(BluetoothSocket socket) {
                this.socket = socket;

            }

        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    byte[] buffer = new byte[1024];// buffer store for the stream
                    int bytes;//bytes returned from read();
                    bytes = socket.getInputStream().read(buffer);
                    final String incomingMessage = new String(buffer, 0, bytes);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (incomingMessage != null && !incomingMessage.isEmpty()) {
                                if (incomingMessage.equalsIgnoreCase("off")){
                                    Toast.makeText(MainActivity.this, "The Water was Turned Off", Toast.LENGTH_SHORT).show();
                                }else {
                                    try {
                                        String[] words=incomingMessage.split(" ",2);
                                        txt_amount.setText(words[0].trim());
                                        txt_cost.setText(words[1].trim());
                                    }catch (Exception e){

                                    }
                                }
                            } else {
                                txt_amount.setText("NoAmount");
                            }
                            //    layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.blue_glass));

                        }
                    });
                } catch (IOException en) {
                    en.printStackTrace();
                }


            }

        }
    }
}
