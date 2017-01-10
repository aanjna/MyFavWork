package com.prodyogic.userauthgithub;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Feedback extends AppCompatActivity implements View.OnClickListener {
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL derived from form URL
    public static final String URL = "https://docs.google.com/forms/d/e/1FAIpQLSed9V5fs0Oygl2cEdWDUjL1mbs5zCfNEK2tMufCxG-PR8MWyw/formResponse";
    //input element ids found from the live form page
    public static final String EMAIL_KEY = "entry.1065598798";
    public static final String SUBJECT_KEY = "entry.1590301386";
    public static final String MESSAGE_KEY = "entry.1483197269";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSIONS = 2;

    private Context context;
    private EditText emailEditText;
    private EditText subjectEditText;
    private EditText messageEditText;
    private ImageView selectedImageView;
    private String realPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        context = this;
        FrameLayout selectImage = (FrameLayout) findViewById(R.id.selectImage);
        selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        //Get references to UI elements in the layout
        Button sendButton = (Button) findViewById(R.id.submit);
        emailEditText = (EditText) findViewById(R.id.et_email);
        subjectEditText = (EditText) findViewById(R.id.et_subject);
        messageEditText = (EditText) findViewById(R.id.et_message);
        selectImage.setOnClickListener(this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Make sure all the fields are filled with values
                if (TextUtils.isEmpty(emailEditText.getText().toString()) ||
                        TextUtils.isEmpty(subjectEditText.getText().toString()) ||
                        TextUtils.isEmpty(messageEditText.getText().toString())) {
                    Toast.makeText(context, "All fields are mandatory.", Toast.LENGTH_LONG).show();
                    return;
                }
                //Check if a valid email is entered
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_LONG).show();
                    return;
                }

                //Create an object for PostDataTask AsyncTask
                PostDataTask postDataTask = new PostDataTask();

                //execute asynctask
                postDataTask.execute(URL, emailEditText.getText().toString(),
                        subjectEditText.getText().toString(),
                        messageEditText.getText().toString());
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
    public void selectImage() {


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                return;

            } else
                //already granted
                selectPicture();


        } else {
            //normal process
            selectPicture();
        }


    }

    private void selectPicture() {

        realPath = null;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //AsyncTask to send data as a http POST request
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String email = contactData[1];
            String subject = contactData[2];
            String message = contactData[3];
            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + SUBJECT_KEY + "=" + URLEncoder.encode(subject, "UTF-8") +
                        "&" + MESSAGE_KEY + "=" + URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = false;
            }

            try {
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            } catch (IOException exception) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Print Success or failure message accordingly
            Toast.makeText(context, result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }
    }
}
