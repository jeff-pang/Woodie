package droidcon.sg.woodiemobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_main);

        final DatabaseReference command = mDatabase.getReference("command");

        Button btnHello = findViewById(R.id.btnHello);
        btnHello.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                RemoteCommand newCommand=new RemoteCommand("Script","wavehello");
                command.setValue(newCommand);
                Log.i(TAG,"Setting Firebase Value command:"+newCommand.getCommandName()+" commandValue:"+newCommand.getCommandValue());
            }
        });

        Button btnWorkout = findViewById(R.id.btnWorkout);
        btnWorkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                RemoteCommand newCommand=new RemoteCommand("Script","workout");
                command.setValue(newCommand);
                Log.i(TAG,"Setting Firebase Value command:"+newCommand.getCommandName()+" commandValue:"+newCommand.getCommandValue());
            }
        });

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                RemoteCommand newCommand=new RemoteCommand("Script","reset");
                command.setValue(newCommand);
                Log.i(TAG,"Setting Firebase Value command:"+newCommand.getCommandName()+" commandValue:"+newCommand.getCommandValue());
            }
        });

        Button btnPuppet = findViewById(R.id.btnPuppet);
        btnPuppet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                RemoteCommand newCommand=new RemoteCommand("Puppet","");
                command.setValue(newCommand);
                Log.i(TAG,"Setting Firebase Value command:"+newCommand.getCommandName()+" commandValue:"+newCommand.getCommandValue());
            }
        });
    }
}
