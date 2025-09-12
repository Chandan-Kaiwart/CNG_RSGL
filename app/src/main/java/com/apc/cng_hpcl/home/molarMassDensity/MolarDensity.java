package com.apc.cng_hpcl.home.molarMassDensity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apc.cng_hpcl.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation.RequestPermissionCode;
import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class MolarDensity extends AppCompatActivity {

    Button capture_denisty, calculate, proceed, save_density;
    ImageView density_image;
    TextView density_value, molar_value,today,txt_density;
    EditText manual_denisty;
    int PICK_FIRST_IMAGE = 100;
    public static final String URL_MolarMass = BASE_URL + "calculateMolarMass.php";
    String username, density, encodedDensityimage, density_name;
    Double molarmass;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molar_density);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            Log.e(TAG, "Username from Transaction to DBS=" + username);
        }
        density_image = findViewById(R.id.density_image);
        density_value = findViewById(R.id.density_value);
        manual_denisty = findViewById(R.id.manual_denisty);
        capture_denisty = findViewById(R.id.capture_denisty);
        save_density = findViewById(R.id.save_density);
        calculate = findViewById(R.id.calculate);
        molar_value = findViewById(R.id.molar_value);
        proceed = findViewById(R.id.proceed);
        today = findViewById(R.id.date);
        txt_density=findViewById(R.id.density);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        date = dateFormat.format(calendar.getTime());
        today.setText("Date : "+date);

        EnableRuntimePermission();


        Drawable old_density_image = density_image.getDrawable();

        capture_denisty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

        save_density.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_FIRST_IMAGE);


            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onClick(View v) {
                density = manual_denisty.getText().toString();
                density_value.setText("Mass/Volume :" + density );

                txt_density.setText("Density : " + String.format("%.4f",(1/Double.parseDouble(density))) + "Kg/m\u00B3");
                molarmass = (1/Double.parseDouble(density)) * 0.0821 * 273.15;
                molar_value.setText("Molar Mass : " + String.format("%.4f", molarmass) + "g");
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (density_image.getDrawable() != old_density_image && density != null) {
                    insertData();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
//                    Intent intent = new Intent(MolarDensity.this, Home.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MolarDensity.this, "Please Capture Dispenser reading", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void insertData() {


        StringRequest request = new StringRequest(Request.Method.POST, URL_MolarMass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("tagconvertstr", "["+response+"]");
                        Log.d("Error",response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(MolarDensity.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(MolarDensity.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MolarDensity.this, "Insertion failed please try again", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                String mMass = Double.toString(molarmass);
                params.put("molar_mass", mMass);
                params.put("density_meter_reading", density);

                params.put("density_meter_img", encodedDensityimage);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MolarDensity.this);
        requestQueue.add(request);


    }

    private String encodebitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteofimages = byteArrayOutputStream.toByteArray();
        return (android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            density_image.setImageBitmap(bitmap);
            encodedDensityimage = encodebitmap(bitmap);

        } else if (requestCode == PICK_FIRST_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            density_image.setImageURI(imageUri);
            File imageFileName = new File(getRealPathFromNAME(imageUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encodedDensityimage = encodebitmap(bitmap);
            density_name = imageFileName.getName();
        }

    }


    private String getRealPathFromNAME(Uri contentNAME) {
        String result;
        Cursor cursor = getContentResolver().query(contentNAME, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentNAME.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MolarDensity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(MolarDensity.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MolarDensity.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MolarDensity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MolarDensity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
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