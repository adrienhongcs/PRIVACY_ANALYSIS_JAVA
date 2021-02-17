package com.example.comp599_a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Size;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class Hidden extends AppCompatActivity {

    private PreviewView aPreviewView;
    private ImageCapture imageCapture;
    private ImageView picture;

    @Override
    @androidx.camera.core.ExperimentalGetImage
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden);

        aPreviewView = findViewById(R.id.aPreviewView_hidden);
        ImageButton cameraBtn = findViewById(R.id.hidden_takePic_btn);
        picture = findViewById(R.id.hidden_image);

        startCamera();
        cameraBtn.setOnClickListener(v -> takePhoto());
    }

    /*
    Called by startCamera()
    Captures an ImageProxy from the controlled camera and sends it to setImage()
     */
    @androidx.camera.core.ExperimentalGetImage
    private void takePhoto() {
        if (imageCapture == null) return;
        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback(){
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy aImageProxy) {
                        setImage(aImageProxy);
                        aImageProxy.close();
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getApplicationContext(),"An ImageCaptureException occurred",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /*
    Converts an ImageProxy to a Bitmap in order to display the image in ImageView picture
     */
    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    /*
    Rotates an image into the right orientation and displays the image in ImageView picture
     */
    private void setImage(ImageProxy pImageProxy)
    {
        Bitmap aBitmap = convertImageProxyToBitmap(pImageProxy);
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(aBitmap, 0, 0, aBitmap.getWidth(), aBitmap.getHeight(), matrix, true);
        picture.setImageBitmap(rotatedBitmap);
    }

    /*
    Opens and controls the camera using a ProcessCameraProvider
     */
    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(()->{
            try{
                ProcessCameraProvider cameraProvider =  cameraProviderFuture.get();
                Preview aPreview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        //.requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280,720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                aPreview.setSurfaceProvider(aPreviewView.getSurfaceProvider());
                imageCapture =
                        new ImageCapture.Builder()
                                .setTargetRotation(aPreviewView.getDisplay().getRotation())
                                .build();
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture,aPreview, imageAnalysis);
            } catch (InterruptedException | ExecutionException e){
                Toast.makeText(this,"Error occurred when attempting to open the camera",Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}