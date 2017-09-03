package tw.com.frankchang.houli.classno_15_network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText etURL;
    private ProgressBar progressBar;
    private ImageView imgShow;

    private int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findviewer();
    }

    private void findviewer() {
        etURL = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgShow = (ImageView) findViewById(R.id.imageView);
    }

    public void downloadOnClick(View view) {
        etURL.setText("http://assets.volvocars.com/tw/~/media/shared-assets/images/galleries/new-cars/s60/gallery/gallery1_exterior/s60_exterior_1.jpg");

        //直接讀取的做法
        //loadImages1();

        //分段式讀取並以 ProgressBar 呈現
        loadImages2();
    }

    private void loadImages2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int totalSize, readSize;
                sum = 0 ;

                try {
                    //建立連線
                    URL url = new URL(etURL.getText().toString());
                    URLConnection conn = url.openConnection();
                    //取得目標物件大小
                    totalSize = conn.getContentLength();
                    //設定 ProgressBar 的最大值
                    progressBar.setMax(totalSize);
                    //讀取
                    InputStream is = conn.getInputStream();
                    byte[] buffer = new byte[200];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    while ((readSize = is.read(buffer)) != -1){
                        //將讀取到的 buffer 寫入 ByteArrayOutputStream
                        baos.write(buffer, 0, readSize);

                        //計算並更新 ProgressBar 進度
                        sum += readSize;
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(sum);
                            }
                        });
                    }

                    byte[] imagesArray = baos.toByteArray();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(imagesArray, 0, sum);
                    imgShow.post(new Runnable() {
                        @Override
                        public void run() {
                            imgShow.setImageBitmap(bmp);
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadImages1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(etURL.getText().toString());
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(is);
                    imgShow.post(new Runnable() {
                        @Override
                        public void run() {
                            imgShow.setImageBitmap(bmp);
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

