package hk.hku.cs.warn.hkubookinglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class BookingSeat extends AppCompatActivity implements View.OnClickListener {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_seat);
        startButton = (Button) findViewById(R.id.start_booking);
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.start_booking){
            LibrarySystem bookingSession = new LibrarySystem(getBaseContext(), this);
            try {
                bookingSession.bookASeat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
