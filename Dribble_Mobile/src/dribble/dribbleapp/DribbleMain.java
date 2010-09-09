package dribble.dribbleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DribbleMain extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button buttonEnter = (Button) findViewById(R.id.buttonEnter);

		buttonEnter.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CreateDribActivity.newMessage = false;
				Intent tabs = new Intent(DribbleMain.this, DribbleTabs.class);
				DribbleMain.this.startActivity(tabs);
				
			}

		});
	}
}
