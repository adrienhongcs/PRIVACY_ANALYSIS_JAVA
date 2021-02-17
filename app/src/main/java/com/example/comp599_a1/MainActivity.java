package com.example.comp599_a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CAMERA_PERMISSION = 1;

    @
    Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button open_btn = findViewById(R.id.open_btn);
        Button control_btn = findViewById(R.id.control_btn);
        Button hidden_btn = findViewById(R.id.hidden_btn);

        open_btn.setOnClickListener(v -> open_camera());
        control_btn.setOnClickListener(v -> askCameraAccess());
        hidden_btn.setOnClickListener(v ->startHidden());

    }

    /*
    Starts the activity that opens the camera
     */
    private void open_camera(){
        Intent intent = new Intent(this,Open.class);
        startActivity(intent);
    }

    /*
    Starts the activity that controls the camera
     */
    private void control_camera(){
        Intent intent = new Intent(this,Control.class);
        startActivity(intent);
    }

    /*
    If the permission to access the camera is granted, the activity with the hidden camera will start
     */
    private void startHidden(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this,Hidden.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,"You should check out CONTROL THE CAMERA first!",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Called by the control_btn
    Asks for permission to access the camera if not granted, else calls control_camera()
     */
    private void askCameraAccess(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            control_camera();
        }
    }

    /*
    If the permission to access the camera has been granted, control_camera() is called
    else indicates that the camera is needed
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                control_camera();
            } else {
                Toast.makeText(this,"Camera permission needed",Toast.LENGTH_SHORT).show();
            }
        }
    }
}