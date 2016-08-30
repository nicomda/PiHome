package org.nicomda.pihome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Settings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		final SharedPreferences settings = getSharedPreferences("ServerInfo",
				Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = settings.edit();
		final Button button_nfc = (Button) findViewById(R.id.button_nfc);
		final Button button_commit = (Button) findViewById(R.id.button_save);
		final EditText ip_wifi = (EditText) findViewById(R.id.editText_ipwifi);
		final EditText ip_3g = (EditText) findViewById(R.id.editText_ip3g);
		final EditText pass = (EditText) findViewById(R.id.editText_pass);
		final EditText port = (EditText) findViewById(R.id.editText_port);
		final Switch light_switch = (Switch) findViewById(R.id.switch_light);
		button_nfc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
			}
		});
		button_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editor.putString("IP_WIFI", ip_wifi.getText().toString());
				editor.putString("IP_3G", ip_3g.getText().toString());
				editor.putString("PASS", pass.getText().toString());
				editor.putString("PORT", port.getText().toString());
				editor.commit();
			}
		});
		ip_wifi.setText(settings.getString("IP_WIFI", "192.168.X.X"));
		ip_3g.setText(settings.getString("IP_3G", "noip.domain.com"));
		pass.setText(settings.getString("PASS", ""));
		port.setText(settings.getString("PORT", "0000"));
		Boolean light_select=Boolean.valueOf(settings.getString("LIGHT_SWITCH", "false"));
		light_switch.setChecked(light_select);
		light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked){
						editor.putString("LIGHT_SWITCH", "true");
						}
						else{
						editor.putString("LIGHT_SWITCH", "false");	
						}
						editor.commit();
					}
				});

	}
}
