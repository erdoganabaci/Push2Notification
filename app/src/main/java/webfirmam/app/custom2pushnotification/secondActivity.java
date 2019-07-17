package webfirmam.app.custom2pushnotification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class secondActivity extends AppCompatActivity {
    private WebView webView;
    private CustomWebViewClient webViewClient;
    private String Url ;
    ProgressDialog mProgressDialog;
    Handler handler = new Handler();
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    TextView textViewUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        String launcUrl = intent.getStringExtra("pushUrl");
        String dataUrl = intent.getStringExtra("dataUrl");

        //Url = launcUrl;
        Url = dataUrl;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Yükleniyor...");
        textViewUrl = findViewById(R.id.textViewUrl);
        textViewUrl.setText(dataUrl);
        webViewClient = new CustomWebViewClient();
        webView = findViewById(R.id.webview1);//webview mızı xml anasayfa.xml deki webview bağlıyoruz
        webView.getSettings().setBuiltInZoomControls(true); //zoom yapılmasına izin verir
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient); //oluşturduğumuz webViewClient objesini webViewımıza set ediyoruz
        webView.loadUrl(Url);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public void sendNotification(View view){
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                try {
                    //OneSignal.postNotification(new JSONObject("{'headings':{'en':'finalPageTitle'},'contents': {'en':'Everyone'}, 'include_player_ids': ['" + playerID + "']}"), null);
                    OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+textViewUrl.getText().toString()+"'},'contents': {'en':'meraba'}, 'data': {'myurl': 'webfirmam.net'},'include_player_ids': ['" + userId + "']}"), null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class CustomWebViewClient extends WebViewClient {

        //Alttaki methodların hepsini kullanmak zorunda deilsiniz
        //Hangisi işinize yarıyorsa onu kullanabilirsiniz.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { //Sayfa yüklenirken çalışır
            super.onPageStarted(view, url, favicon);

            if(!mProgressDialog.isShowing())//mProgressDialog açık mı kontrol ediliyor
            {

                mProgressDialog.show();//mProgressDialog açık değilse açılıyor yani gösteriliyor ve yükleniyor yazısı çıkıyor
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 5000);

            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {//sayfamız yüklendiğinde çalışıyor.
            super.onPageFinished(view, url);

            if(mProgressDialog.isShowing()){//mProgressDialog açık mı kontrol açıksa kapat ama sayfa yüklenmesi tamalanırsa
                mProgressDialog.dismiss();//mProgressDialog açıksa kapatılıyor
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Bu method açılan sayfa içinden başka linklere tıklandığında açılmasına yarıyor.
            //Bu methodu override etmez yada edip içini boş bırakırsanız ilk url den açılan sayfa dışında başka sayfaya geçiş yapamaz

            view.loadUrl(url);//yeni tıklanan url i açıyor
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

            //	if(errorCode !=null){
            //		Timeout
            //	} şeklinde kullanabilirsiniz



        }
    }
    public void onBackPressed() //Android Back Buttonunu Handle ediyoruz. Back butonu bir önceki sayfaya geri dönecek
    {
        if(webView.canGoBack()){//eğer varsa bir önceki sayfaya gidecek
            webView.goBack();
        }else{//Sayfa yoksa uygulamadan çıkacak
            super.onBackPressed();
        }
    }


}
