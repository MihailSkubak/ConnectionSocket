package com.example.connectionsoket;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;



public class MainActivity extends AppCompatActivity {

    TextView ipadd;//zmianna ktora pokazuje swój IP
    EditText e1, e2;//IP innego tel. i sms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipadd = (TextView)findViewById(R.id.ipadd);//inicjalizacja zmiennej dla text pod IP

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        ipadd.setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));

        e1 = (EditText) findViewById(R.id.editText);//inicjalizacja zmiennej dla textbox
        e2 = (EditText) findViewById(R.id.editText2);

        Thread myThread = new Thread(new MyServer());
        myThread.start();
    }

    class MyServer implements Runnable{
        ServerSocket ss;
        Socket mysocket;
        DataInputStream dis;
        String message;
        Handler h = new Handler();
        @Override
        public void run(){
            try{
                ss = new ServerSocket(9899);
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"waiting for cliend: ",Toast.LENGTH_SHORT).show();//polączenie sms
                    }
                });
                while(true){//jeżeli jest polączenie
                    mysocket = ss.accept();
                    dis = new DataInputStream(mysocket.getInputStream());
                    message = dis.readUTF();
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"message cliend: "+message,Toast.LENGTH_SHORT).show();// pokaz sms telefony ktoryj otrzymał sms
                        }
                    });
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }

        }
    }

    public void button_click(View v){//button dla SEND
        BackEnd b = new BackEnd();
        b.execute(e1.getText().toString(),e2.getText().toString());

    }
    class BackEnd extends AsyncTask<String,Void,String>{
        Socket s;
        DataOutputStream dos;
        String ip,message;

        @Override
        protected String doInBackground(String... params){//polącenie z innym IP
            ip = params[0];
            message = params[1];
            try{
                s = new Socket(ip,9899);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);
                dos.close();
                s.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }

                    return null;
        }
    }
}