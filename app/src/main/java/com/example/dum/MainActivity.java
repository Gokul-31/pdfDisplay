package com.example.dum;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.media.MediaCodec.MetricsConstants.MODE;

public class MainActivity extends AppCompatActivity {

    Map<String, String> jwtToken;
    String TAG="Dum";
    String filePath;
    ImageView img;
    Button prev,next;
    int pgNo=1;
    ParcelFileDescriptor pfd;
    PdfRenderer renderer;
    PdfRenderer.Page currPage;
    Bitmap pageBitmap;

    FrameLayout f;

    public static final String BASE_URL = "https://spider.nitt.edu/police_cms/";
    public static Retrofit retrofit = null;

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getLoginDetails(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        img=findViewById(R.id.image_pdf);

        img.setBackgroundColor(getResources().getColor(R.color.white));

        jwtToken = new HashMap<>();
        jwtToken.put("Auth-Token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiQUNQIiwiaWQiOiI1ZjlmOGUyYzhhZGE0ZTFkNzhmOTEyNjciLCJtb2JpbGVObyI6OTk5OTk5OTkwOSwiaWF0IjoxNjA3MTYxMjA2LCJleHAiOjE2MDk3NTMyMDZ9.EyApgLCY3QlQKOQ6ESlSBpfvwpBeeJQ_MfxC9uKg2Ms");

        final ServiceLogin api=getLoginDetails().create(ServiceLogin.class);
        Call<ResponseBody> call1=api.DownloadPetitionFile(jwtToken,"5f991a1a5ca64b50b1ee320c","d1e4d1b2-4ee2-47a5-af6f-269cf50ec78d.pdf");

        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse: failed: "+response.message());
                    Log.i(TAG, "onResponse: failed: code: "+response.code());
                    return;
                }
                Log.i(TAG, "onResponse: "+response.message());
                Log.i(TAG, "onResponse body: "+response.body().toString());
                boolean writtenToDisk = writeResponseBodyToDisk(response.body());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            filePath=getExternalFilesDir(null) + File.separator + "Document1.pdf";
            String[] propFilePath = filePath.split("Android",2);
            filePath = propFilePath[0];
            Log.i(TAG, "FIle path: "+filePath);
            filePath+="Download"+File.separator+"Document1.pdf";
            Log.i(TAG, "FIle path: "+filePath);
            File downloadFile = new File(filePath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloadFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    //end
                    Log.i(TAG, "path : "+filePath);


                }
                outputStream.flush();

                //preview
                pfd = ParcelFileDescriptor.open(downloadFile,ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(pfd);
                currPage = renderer.openPage(0);
                pageBitmap = Bitmap.createBitmap(currPage.getWidth(),currPage.getHeight(), Bitmap.Config.ARGB_8888);
                currPage.render(pageBitmap,null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                img.setImageBitmap(pageBitmap);
                Log.i(TAG, "preview: Image bitmap set");

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}