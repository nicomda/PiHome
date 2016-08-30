package org.nicomda.pihome;

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.net.*;


public class MainActivity extends AppCompatActivity {
    public static final int MENU_SETTINGS = Menu.FIRST;
    public static final int MENU_CLOSE = Menu.FIRST + 1;
    public static final double GARAGE_LATITUDE = 38.03783;
    public static final double GARAGE_LONGITUDE = -4.16973;
    private GPSTracker gps;
    TextView pb_text;
    ProgressBar pbar;
    String inString;
    boolean pbar_async_running;
    boolean door_stoped;
    public static final String TAG="NFC_Tag";
    private NfcAdapter mNfcAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //buildPassDialog();
        final SharedPreferences settings=getSharedPreferences("ServerInfo", Context.MODE_PRIVATE);
        pb_text = (TextView) findViewById(R.id.textView);
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setMax(100);
        final Location current_loc = new Location("ProviderNicomda");
        final Location garage_loc = new Location("ProviderNicomda");
        garage_loc.setLatitude(GARAGE_LATITUDE);
        garage_loc.setLongitude(GARAGE_LONGITUDE);
        pb_text.setText("Esperando botón pulsado");
        pbar_async_running=false;
        door_stoped=false;
        final ImageButton button = (ImageButton) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()) {
                    pbar.setProgress(0);
                    double latitude = gps.getLatitude();
                    current_loc.setLatitude(latitude);
                    double longitude = gps.getLongitude();
                    current_loc.setLongitude(longitude);
                    float distance = garage_loc.distanceTo(current_loc);
                    pbar.setProgress(10);
                    pb_text.setText("Localización obtenida");
                    Toast.makeText(getApplicationContext(), "Estás a " + (int) distance + "m del garaje", Toast.LENGTH_SHORT).show();
                    if ((int) distance < 2000) {
                        new AsyncTCP().execute();
                        pbar.setProgress(20);

                    } else {
                        pbar.setProgress(100);
                        pb_text.setText("Estás demasiado lejos para abrir");
                    }
                } else gps.showSettingsAlert();

            }
        });
        NFCRead();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i=new Intent(MainActivity.this, Settings.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password");
        builder.setMessage("Introduce el password");
        builder.setIcon(android.R.drawable.ic_lock_lock);
        final EditText pass = new EditText(this);
        pass.setInputType(InputType.TYPE_CLASS_NUMBER);
        pass.setTransformationMethod(new PasswordTransformationMethod());
        builder.setView(pass);
        final AlertDialog dialog = builder.create();
        TextWatcher mPassTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if ((Integer.parseInt(pass.getText().toString())) == 1513) dialog.dismiss();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // if (Integer.parseInt(pass.getText().toString())==1513)dialog.dismiss();
            }
        };
        pass.addTextChangedListener(mPassTextWatcher);
        dialog.setCancelable(false);
        dialog.show();
    }

    public class AsyncTCP extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String IP = null;
                final SharedPreferences settings=getSharedPreferences("ServerInfo", Context.MODE_PRIVATE);
                if(isConnectedMobile(getApplicationContext())){
                    IP=settings.getString("IP_3G", "noip.domain.com");
                }
                if(isConnectedWifi(getApplicationContext())){
                    IP=settings.getString("IP_WIFI", "192.168.X.X");
                }
                Socket clientSocket = new Socket(IP, Integer.parseInt(settings.getString("PORT", "2014")));
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes(settings.getString("PASS", "0000"));
                //outToServer.writeBytes("Abrir" + "\n");
                //outToServer.writeBytes(settings.getString("LIGHT_SWITCH", "false")+"\n");
                inString=inFromServer.readLine();
                publishProgress(inString);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute() {
			// TODO Auto-generated method stub
			
		}
		protected void onProgressUpdate(String... values) {
        	String text_server=values[0];
        	pb_text.setText(text_server);
        	try {
				Thread.sleep(1000);
				pb_text.setText(inString);
				pbar.setProgress(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        }

        public NetworkInfo getNetworkInfo(Context context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }
        public boolean isConnectedWifi(Context context){
            NetworkInfo info=getNetworkInfo(context);
            return(info!=null && info.isConnected() && info.getType()==ConnectivityManager.TYPE_WIFI);
        }
        public boolean isConnectedMobile(Context context){
            NetworkInfo info=getNetworkInfo(context);
            return(info!=null && info.isConnected() && info.getType()==ConnectivityManager.TYPE_MOBILE);
        }
    }

    class Counter extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int current = 80;
            while (current > 60) {
                try {
                    Thread.sleep(1000);
                    current--;
                    Log.d("TEST",
                            String.format("Contador: %d", current));
                    publishProgress(current);
                } catch(Exception e) {
                    Log.d("TEST", "Error en doInBackground()");
                }
            }
            while (current > 20) {
                try {
                    Thread.sleep(1000);
                    current--;
                    Log.d("TEST",
                            String.format("Contador: %d", current));
                    publishProgress(current);
                } catch(Exception e) {
                    Log.d("TEST", "Error en doInBackground()");
                }
            }
            while (current > 0) {
                try {
                    Thread.sleep(1000);
                    current--;
                    Log.d("TEST",
                            String.format("Contador: %d", current));
                    publishProgress(current);
                } catch(Exception e) {
                    Log.d("TEST", "Error en doInBackground()");
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("TEST", "Counter.onProgressUpdate()");
            for(Integer value: values) {
                int valueopened=value-20;
                pbar.incrementProgressBy(1);
                if(pbar.getProgress()<40)pb_text.setText("Puerta Abriéndose");
                if(pbar.getProgress()>40&&pbar.getProgress()<80)pb_text.setText("Puerta en espera "+Integer.toString(valueopened));
            }

            }
        protected void onPreExecute(){
            pbar_async_running=true;
        }
        protected void onPostExecute(){
            pbar_async_running=false;
            pb_text.setText("Puerta cerrada");
        }
        }
    private void NFCRead(){
    	mNfcAdapter=NfcAdapter.getDefaultAdapter(this);
    	if(mNfcAdapter==null){
    		Toast.makeText(this, "Este dispositivo no soporta NFC.", Toast.LENGTH_SHORT).show();
    	}
    	if(!mNfcAdapter.isEnabled()){
    		Toast.makeText(this, "NFC está desactivado.", Toast.LENGTH_SHORT).show();
    	}
    	handleIntent(getIntent());
    }

	private void handleIntent(Intent intent) {
		 String action = intent.getAction();
		    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
		        final Location current_loc = new Location("ProviderNicomda");
		        final Location garage_loc = new Location("ProviderNicomda");
		        garage_loc.setLatitude(GARAGE_LATITUDE);
		        garage_loc.setLongitude(GARAGE_LONGITUDE);
		    	gps = new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()) {
                    pbar.setProgress(0);
                    double latitude = gps.getLatitude();
                    current_loc.setLatitude(latitude);
                    double longitude = gps.getLongitude();
                    current_loc.setLongitude(longitude);
                    float distance = garage_loc.distanceTo(current_loc);
                    pbar.setProgress(10);
                    pb_text.setText("Localización obtenida");
                    Toast.makeText(getApplicationContext(), "Estás a " + (int) distance + "m del garaje", Toast.LENGTH_SHORT).show();
                    if ((int) distance < 2000) {
                        new AsyncTCP(){
                        	@Override
                        	protected void onPostExecute(){
                        	}
                        }.execute();
                        pbar.setProgress(20);
                        NFC_Notification_OK();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        finish();
                    } else {
                        pbar.setProgress(100);
                        pb_text.setText("Estás demasiado lejos para abrir");
                        NFC_Notification_FAIL();
                    }
                } else gps.showSettingsAlert();
		    }
    }

	private void NFC_Notification_OK(){
		NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.nfc_icon)
		.setContentTitle("NFC: Abrir Garaje")
		.setContentText("Solicitada apertura del Garaje NFC");
		
		Intent resultIntent=new Intent(this, MainActivity.class);
		PendingIntent resultPendingIntent=PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		int mNotificationId=001;
		NotificationManager mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(mNotificationId, mBuilder.build());
	
	}
	private void NFC_Notification_FAIL(){
		NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.nfc_icon_fail)
		.setContentTitle("NFC: Distancia Incorrecta ")
		.setContentText("Debes estar a menos de 2KM para abrir");
		
		Intent resultIntent=new Intent(this, MainActivity.class);
		PendingIntent resultPendingIntent=PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		int mNotificationId=002;
		NotificationManager mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(mNotificationId, mBuilder.build());
	
	}
}
