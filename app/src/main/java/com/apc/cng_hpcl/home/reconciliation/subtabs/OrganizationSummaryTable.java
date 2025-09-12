package com.apc.cng_hpcl.home.reconciliation.subtabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.apc.cng_hpcl.R;

public class OrganizationSummaryTable extends AppCompatActivity {
String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");

        }
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        String[] row = { "1", "2", "3", "4", "5", "6","7", "8", "9", "10", "11", "12"
        };
        String[] column = {"Date","Supplied from CGS", "Supplied from MGS", "Stored at DBS", "Sold at DBS","Loss of the day"};
        int rl=row.length;
        int cl=column.length;

        ScrollView sv = new ScrollView(this);
        TableLayout tableLayout = createTableLayout(row, column,rl, cl);
        HorizontalScrollView hsv = new HorizontalScrollView(this);

        hsv.addView(tableLayout);
        sv.addView(hsv);
        setContentView(sv);
    }
    private TableLayout createTableLayout(String [] rv, String [] cv,int rowCount, int columnCount)
    {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
//        tableLayout.setBackgroundColor(Color.DKGRAY);
        tableLayout.setBackgroundColor(getResources().getColor(R.color.Accent));
        tableLayout.setStretchAllColumns(true);
//        tableLayout.setShrinkAllColumns(true);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(15, 15, 15, 15);
        tableRowParams.weight = 2;
        tableRowParams.span = 7;
        for (int i = 0; i <= rowCount; i++)
        {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);
//            tableRow.setBackgroundColor(Color.BLACK);
            tableLayout.setBackgroundColor(getResources().getColor(R.color.Accent));

            for (int j= 0; j <= columnCount; j++)
            {
                // 4) create textView
                TextView textView = new TextView(this);
                //  textView.setText(String.valueOf(j));
                tableLayout.setBackgroundColor(getResources().getColor(R.color.Accent));
//                textView.setBackgroundColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.Primary));

                String s1 = Integer.toString(i);
                String s2 = Integer.toString(j);
                String s3 = s1 + s2;
                int id = Integer.parseInt(s3);
                Log.d("TAG", "-___>"+id);
                if (i ==0 && j==0)
                {
                    textView.setTextColor(getResources().getColor(R.color.Primary));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setText("Id");
                }
                else if(i==0)
                {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    textView.setGravity(Gravity.CENTER);
                    Log.d("TAAG", "set Column Headers");
                    textView.setTextColor(getResources().getColor(R.color.Primary));
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setText(cv[j-1]);

                }
                else if( j==0)
                {
                    Log.d("TAAG", "Set Row Headers");
                    textView.setText(rv[i-1]);
                    textView.setTextColor(getResources().getColor(R.color.Primary));
                }
                else
                {
                    textView.setText(""+id);


                }

                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }

            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}