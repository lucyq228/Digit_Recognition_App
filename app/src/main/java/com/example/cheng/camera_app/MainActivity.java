package com.example.cheng.camera_app;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//An activity is a single, focused thing that the user can do. Almost all activities interact with the user,
//so the Activity class takes care of creating a window for you in which you can place your UI with setContentView(View)
//A mapping from String keys to various Parcelable values (interface for data container values, parcels)
//Object used to report movement (mouse, pen, finger, trackball) events.
//This class represents the basic building block for user interface components.
// A View occupies a rectangular area on the screen and is responsible for drawing
//A user interface element the user can tap or click to perform an action.
//A user interface element that displays text to the user. To provide user-editable text, see EditText.
import android.widget.TextView;
//Resizable-array implementation of the List interface. Implements all optional list operations, and permits all elements,
// including null. In addition to implementing the List interface, this class provides methods to
// //manipulate the size of the array that is used internally to store the list.
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
// basic list
import java.util.List;
//encapsulates a classified image
//public interface to the classification class, exposing a name and the recognize function
import com.example.cheng.camera_app.models.Classification;
import com.example.cheng.camera_app.models.Classifier;
//contains logic for reading labels, creating classifier, and classifying
import com.example.cheng.camera_app.models.TensorFlowClassifier;
//class for drawing MNIST digits by finger
//class for drawing the entire app
import com.example.cheng.camera_app.views.DrawView2;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    Button btnpic;
    Button btngallery;
    Button btclass;
    ImageView imgTakenPic;
    private static final int CAM_REQUEST=1313;
    int SELECT_PIC_CODE =100;
    Bitmap mBitmap;


    private static final int PIXEL_WIDTH = 28;
    private TextView resText;
    private List<Classifier> mClassifiers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnpic = (Button) findViewById(R.id.camera);
        btngallery = (Button) findViewById(R.id.gallery);
        btclass = (Button) findViewById(R.id.btn_class);
        imgTakenPic = (ImageView)findViewById(R.id.draw);
        // res text
        //this is the text that shows the output of the classification
        resText = (TextView) findViewById(R.id.tfRes);
        btnpic.setOnClickListener(new btnTakePhotoClicker());
        btngallery.setOnClickListener(new selectPicture());
        btclass.setOnClickListener(new processImage());

        // tensorflow
        //load up our saved model to perform inference from local storage
        loadModel();
    }

    //creates a model object in memory using the saved tensorflow protobuf model file
    //which contains all the learned weights
    private void loadModel() {
        //The Runnable interface is another way in which you can implement multi-threading other than extending the
        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
        // //which provides the method run.
        // //Threads are implementations and use Runnable to call the method run().
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //add 2 classifiers to our classifier arraylist
                    //the tensorflow classifier and the keras classifier
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "TensorFlow",
                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
                                    "input", "output", true));
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "Keras",
                                    "opt_mnist_convnet-keras.pb", "labels.txt", PIXEL_WIDTH,
                                    "conv2d_1_input", "dense_2/Softmax", false));
                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST){
            mBitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(mBitmap);
            resText.setText(""); //clear text
        }

//        if (requestCode == SELECT_PIC_CODE){
//            Uri selectedImageUri = data.getData();
//            InputStream in = null;
//            try{
//                in = getContentResolver().openInputStream(selectedImageUri);
//            } catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
//            mBitmap = BitmapFactory.decodeStream(in);
//            imgTakenPic.setImageBitmap(mBitmap);
//            resText.setText(""); //clear text
//        }

        if (requestCode == SELECT_PIC_CODE){// Get selected gallery image
            Uri selectedPicture = data.getData();
            // Get and resize profile image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            mBitmap = BitmapFactory.decodeFile(picturePath);

            ExifInterface exif = null;
            try {
                File pictureFile = new File(picturePath);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mBitmap = rotateBitmap(mBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mBitmap = rotateBitmap(mBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    mBitmap = rotateBitmap(mBitmap, 270);
                    break;
            }

            imgTakenPic.setImageBitmap(mBitmap);
            resText.setText(""); //clear text
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    //take picture
    class btnTakePhotoClicker implements  Button.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAM_REQUEST);
        }
    }

    //select picture from gallery
    class selectPicture implements Button.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction( Intent.ACTION_GET_CONTENT );
            startActivityForResult(Intent.createChooser( intent, "Select Picture" ), SELECT_PIC_CODE);
        }
    }


    //detect picture
    class processImage implements  Button.OnClickListener{
        @Override
        public void onClick(View view) {
            //Toast.makeText(MainActivity.this, "test test test", Toast.LENGTH_SHORT).show();

            //if the user clicks the classify button
            //get the pixel data and store it in an array
            float pixels[] = DrawView2.getPixelData(mBitmap);

            //init an empty string to fill with the classification output
            String text = "";
            //for each classifier in our array
            for (Classifier classifier : mClassifiers) {
                //perform classification on the image
                final Classification res = classifier.recognize(pixels);
                //if it can't classify, output a question mark
                if (res.getLabel() == null) {
                    text += classifier.name() + ": ?\n";
                } else {
                    //else output its name
                    text += String.format("%s: %s, %f\n", classifier.name(), res.getLabel(),
                            res.getConf());
                }
            }
            resText.setText(text);
        }
    }
}