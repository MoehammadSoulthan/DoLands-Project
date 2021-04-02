package id.ac.umn.dolands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReviewActivity extends AppCompatActivity {
    Button btnDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        btnDetail = findViewById(R.id.button_detail);

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
            }
        });

        //settingan action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }
}