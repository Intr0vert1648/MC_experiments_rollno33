package com.santossingh.bluetoothfiletransfer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 100;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;

    Button buttonOpenFile, send;
    EditText dataPath;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    Uri selectedFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataPath = findViewById(R.id.FilePath);
        buttonOpenFile = findViewById(R.id.opendailog);
        send = findViewById(R.id.sendBtooth);

        // Open system file picker (modern way)
        buttonOpenFile.setOnClickListener(v -> openFilePicker());

        // Send file via Bluetooth
        send.setOnClickListener(v -> sendViaBluetooth());
    }

    // ===============================
    // Open modern system file picker
    // ===============================
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE);
    }

    // ===============================
    // Receive selected file
    // ===============================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // File selected
        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            dataPath.setText(selectedFileUri.toString());
        }

        // Bluetooth discoverable result
        if (requestCode == REQUEST_BLU && resultCode == DISCOVER_DURATION) {
            sendFileIntent();
        }
    }

    // ===============================
    // Send file via Bluetooth
    // ===============================
    private void sendViaBluetooth() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_LONG).show();
            return;
        }

        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
            return;
        }

        enableBluetooth();
    }

    // Make device discoverable
    private void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    // Send file using Bluetooth app
    private void sendFileIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, selectedFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Send file via"));
    }
}
