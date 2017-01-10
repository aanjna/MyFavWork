package com.prodyogic.userauthgithub;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    Button signup, submit;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
                // continue with your code
            }
        } else {
            // continue with your code
        }

        text = (TextView) findViewById(R.id.text);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View prompt = li.inflate(R.layout.dialog_userauth, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(prompt);
                final EditText username = (EditText)prompt.findViewById(R.id.name);
                final EditText pass = (EditText) prompt.findViewById(R.id.pass);

                if(this != null){
                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String password = pass.getText().toString();

                                    try{
                                        if(username.equals(getIntent().getStringExtra("USERNAME")) && password.length() > 0)
                                        {
                                            startActivity(new Intent(MainActivity.this, UserViewActivty.class));

                                           /* DBAdapter dbUser = new DBAdapter(PasswordActivity.this);
                                            dbUser.open();

                                            if(dbUser.Login(username, password))
                                            {
                                                show_add_layout();
                                            }else{
                                                msg.setText("Password is incorrect");
                                            }
                                            dbUser.close();*/
                                        }

                                    }catch(Exception e)
                                    {
                                        Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });
                    alertDialogBuilder.show();
                }
                else{
                  //  show_mesg("Please select item to edit.");
                }

            }
        });
                        /*submit = (Button) findViewById(R.id.save);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i =new Intent(MainActivity.this, UserViewActivty.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }
}
