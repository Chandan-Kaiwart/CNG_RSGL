package com.apc.cng_hpcl.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apc.cng_hpcl.R;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageFragment extends Fragment {

    private static final String TAG = "ImageFragment";
    private Bitmap bitmap,bitmap2;

    @BindView(R.id.res_photo)
    ImageView resPhoto;

    @BindView(R.id.res_photo_size)
    TextView resPhotoSize;

    @BindView(R.id.res_photo2)
    ImageView resPhoto2;

    @BindView(R.id.res_photo_size2)
    TextView resPhotoSize2;

    TextView txt_message,txt_reading,txt_reading_message;
    LinearLayout ll_buttons;
    Button btn_yes,btn_no;

    public void imageSetupFragment(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap != null) {
            this.bitmap = bitmap;
            this.bitmap2 = bitmap2;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, view);

        txt_message = view.findViewById(R.id.txt_message);
        txt_reading = view.findViewById(R.id.txt_reading);
        txt_reading_message = view.findViewById(R.id.txt_reading_message);
        ll_buttons = view.findViewById(R.id.ll_buttons);
        btn_yes = view.findViewById(R.id.btn_yes);
        btn_no = view.findViewById(R.id.btn_no);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CameraActivity.class));
                getActivity().finish();
            }
        });

//        if (bitmap != null)
//        {
//            resPhoto.setImageBitmap(bitmap);
//            String info = "image with:" + bitmap.getWidth() + "\n" + "image height:" + bitmap.getHeight();
//            resPhotoSize.setText(info);
//
//            resPhoto2.setImageBitmap(bitmap2);
//            String info2 = "image with:" + bitmap2.getWidth() + "\n" + "image height:" + bitmap2.getHeight();
//            resPhotoSize2.setText(info2);
//            String customer_Id = SharedPreferenceManager.with(getContext()).getLoggedInUser().getCustomer_bp_no();
//            String meter_Id = SharedPreferenceManager.with(getContext()).getLoggedInUserInfo().getCustomer_meter_id();
//
//
//            String Customer_last_meter_reading = SharedPreferenceManager.with(getContext()).getLastBillingDetails().getCustomer_current_meter_reading();
//            String Customer_last_reading_date = SharedPreferenceManager.with(getContext()).getLastBillingDetails().getCurent_meter_reading_date();
//
//            Log.e("TAG-",customer_Id +" "+ meter_Id +" "+ Customer_last_meter_reading +" "+ Customer_last_reading_date);
//
//            insertImages(customer_Id,meter_Id,"12345",
//                    "06-01-2021",getFileDataFromDrawable(getContext(), resPhoto.getDrawable()),
//                    getFileDataFromDrawable(getContext(), resPhoto2.getDrawable()));
//
//
//
//
//        }
        return view;
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable)
    {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

//    private void insertImages(String customer_id,String meter_Id,String Customer_last_meter_reading,
//                              String Customer_last_reading_date, byte[] img1,byte[] img2)
//    {
//        final ProgressDialog progressDialog;
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Fetching....");
//        progressDialog.setTitle("Calculating gas meter reading");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(40, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl(Constant.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RetrofitInterface request = retrofit.create(RetrofitInterface.class);
//
//        RequestBody reqImg1 = RequestBody.create(MediaType.parse("image/jpeg"), img1);
//        RequestBody reqImg2 = RequestBody.create(MediaType.parse("image/jpeg"), img2);
//
//        MultipartBody.Part doc_id1 = MultipartBody.Part.createFormData("img1", "img1.jpg", reqImg1);
//        MultipartBody.Part doc_id2 = MultipartBody.Part.createFormData("img2", "img2.jpg", reqImg2);
//
//        RequestBody cust_id = RequestBody.create(MediaType.parse("text/plain"), customer_id);
//        RequestBody meter_ID = RequestBody.create(MediaType.parse("text/plain"), meter_Id);
//        RequestBody last_meter_reading = RequestBody.create(MediaType.parse("text/plain"), Customer_last_meter_reading);
//        RequestBody last_reading_date = RequestBody.create(MediaType.parse("text/plain"), Customer_last_reading_date);
//
//        Call<RetrofitResponse> call = request.insert_captured_images_gas(cust_id,meter_ID,last_meter_reading,last_reading_date,doc_id1,doc_id2);
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
//                Log.e("INSERT", response.raw() + "");
//                RetrofitResponse jsonResponse = response.body();
//                if (jsonResponse.success.equals("1"))
//                {
//                    progressDialog.dismiss();
//                    Log.e("TAG",jsonResponse.message);
//                    txt_reading.setText("Captured data : "+jsonResponse.message);
//                } else {
//                    Toast.makeText(getContext(), "Error Submit", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    Log.e("TAG",jsonResponse.message);
//                }
//                /*
//                if (jsonResponse.success.equals("1"))
//                {
//                    fetchCustomerReading(progressDialog,jsonResponse.s_no,customer_id);
//                    //Toast.makeText(getContext(), jsonResponse.s_no, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Error Submit", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//                */
//            }
//
//            @Override
//            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
//                Log.e("ERROR", t.getMessage() + "");
//                progressDialog.dismiss();
//            }
//        });
//    }

//    private void insertImages(byte[] img1,byte[] img2)
//    {
//        final ProgressDialog progressDialog;
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Fetching....");
//        progressDialog.setTitle("Calculating meter reading/date/time");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(40, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl(Constant.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RetrofitInterface request = retrofit.create(RetrofitInterface.class);
//
//        RequestBody reqImg1 = RequestBody.create(MediaType.parse("image/jpeg"), img1);
//        RequestBody reqImg2 = RequestBody.create(MediaType.parse("image/jpeg"), img2);
//
//        MultipartBody.Part doc_id1 = MultipartBody.Part.createFormData("img1", "img1.jpg", reqImg1);
//        MultipartBody.Part doc_id2 = MultipartBody.Part.createFormData("img2", "img2.jpg", reqImg2);
//
//        Call<RetrofitResponse> call = request.insert_captured_images(doc_id1,doc_id2);
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
//                Log.e("INSERT", response.raw() + "");
//                RetrofitResponse jsonResponse = response.body();
//                if (jsonResponse.success.equals("1"))
//                {
//                    progressDialog.dismiss();
//                    Log.e("TAG",jsonResponse.message);
//                    txt_reading.setText(jsonResponse.message);
//                } else {
//                    Toast.makeText(getContext(), "Error Submit", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    Log.e("TAG",jsonResponse.message);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
//                Log.e("ERROR", t.getMessage() + "");
//                progressDialog.dismiss();
//            }
//        });
//    }

//    private void fetchCustomerReading(ProgressDialog progressDialog, String s_no, String customer_id)
//    {
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(300, TimeUnit.SECONDS)
//                .readTimeout(300, TimeUnit.SECONDS)
//                .writeTimeout(300, TimeUnit.SECONDS)
//                .build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl(Constant.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RetrofitInterface request = retrofit.create(RetrofitInterface.class);
//        Call<RetrofitResponse> call = request.getCurrentMeterReadingInfo(s_no,customer_id);
//        call.enqueue(new Callback<RetrofitResponse>()
//        {
//            @Override
//            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response)
//            {
//                Log.e(TAG,response.raw()+"");
//                if(response.isSuccessful())
//                {
//                    RetrofitResponse jsonResponse = response.body();
//                    if(jsonResponse.success.equals("1"))
//                    {
//                        List<CurrentReading> currentReadings = jsonResponse.getCurrent_meter_reading();
//                        if (currentReadings != null)
//                        {
//                            setData(currentReadings.get(0),progressDialog,s_no,customer_id);
//
//                            Log.e(TAG,"Data");
//                        }
//                        else
//                        {
//                            progressDialog.dismiss();
//                            Toast.makeText(getContext(), "Cannot find reading", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else
//                    {
//                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), "response 0", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    progressDialog.dismiss();
//                    Toast.makeText(getContext(), "Server Down", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<RetrofitResponse> call, Throwable t)
//            {
//                progressDialog.dismiss();
//                Log.e(TAG,t.getMessage()+"");
//            }
//        });
//    }
//
//    private void setData(CurrentReading currentReading,ProgressDialog progressDialog, String s_no, String customer_id)
//    {
//        if(currentReading.getCustomer_current_meter_reading()==null)
//        {
//            fetchCustomerReading(progressDialog,s_no,customer_id);
//        }
//        else
//        {
//            progressDialog.dismiss();
//            txt_reading.setText(currentReading.getCustomer_current_meter_reading());
//            txt_message.setVisibility(View.INVISIBLE);
//            btn_yes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(getContext(),CurrentBillingActivity.class);
//                    //intent.putExtra("current_reading",currentReading.getCustomer_current_meter_reading());
//                    intent.putExtra("s_no",s_no);
//                    SharedPreferenceManager.with(getContext()).setLatestMeterReading(currentReading.getCustomer_current_meter_reading());
//                    startActivity(intent);
//                    getActivity().finish();
//                }
//            });
//
//            btn_no.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(getContext(), CameraActivityclass));
//                    getActivity().finish();
//                }
//            });
//        }
//    }
//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
}
