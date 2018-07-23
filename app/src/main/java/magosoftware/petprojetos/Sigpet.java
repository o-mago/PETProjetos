package magosoftware.petprojetos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

public class Sigpet extends BaseFragment implements View.OnClickListener{

    WebView webview;
    WebViewClient webViewClient;
    String cpf;
    String senha;
    boolean pronto = false;
    private Button entrar;
    private EditText et_cpf;
    private EditText et_senha;
    private CardView loginSigpet;
    private ProgressBar progressBar;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;

    public static Sigpet newInstance() {
        Sigpet sigpet = new Sigpet();
        return sigpet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
        return inflater.inflate(R.layout.webview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        editor = sharedPref.edit();

        webview = getView().findViewById(R.id.webview_master);
        webview.setVisibility(View.GONE);
        entrar = getView().findViewById(R.id.submit);
        et_cpf = getView().findViewById(R.id.cpf);
        et_senha = getView().findViewById(R.id.senha);
        entrar.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        String cpfShared = sharedPref.getString("cpf_sigpet", "nada");
        String senhaShared = sharedPref.getString("senha_sigpet", "nada");
        Log.d("DEV/SIGPET", "cpfShared: "+cpfShared);
        if(!cpfShared.equals("nada")) {
            et_cpf.setText(cpfShared);
        }
        if(!senhaShared.equals("nada")) {
            et_senha.setText(senhaShared);
        }
        loginSigpet = getView().findViewById(R.id.login_sigpet);
        entrar.setOnClickListener(this);

        progressBar = getView().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setBuiltInZoomControls(true);

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
//                getActivity().setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                handler.proceed();
//                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("Clique no botão abaixo para permitir");
//                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        handler.proceed();
//                        webview.setVisibility(View.VISIBLE);
//                    }
//                });
//                builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        handler.cancel();
//                    }
//                });
//                final AlertDialog dialog = builder.create();
//                dialog.show();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), "Tente novamente" + description, Toast.LENGTH_SHORT).show();
            }

            public void onPageFinished(WebView view, String url){
                if(Build.VERSION.SDK_INT >= 19) {
                    if(url.contains("primeiro-acesso")) {
                        String js = "javascript:document.querySelectorAll(\"a[href='/login/login']\")[0].click()";
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                            }
                        });
                    } else if (url.contains("ssd.mec.gov.br")){
                        pronto = true;
                        entrar.getBackground().setColorFilter(null);
                    } if(url.contains("grupo")) {
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        loginSigpet.setVisibility(View.GONE);
//                        webview.setVisibility(View.VISIBLE);
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        progressBar.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        if(savedIntanceState == null) {
            webview.loadUrl("http://sigpet.mec.gov.br/primeiro-acesso");
        } else {
            loginSigpet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.submit) {
            if(pronto && Build.VERSION.SDK_INT >= 19) {
                cpf = et_cpf.getText().toString();
                senha = et_senha.getText().toString();
                String js = "javascript:document.getElementById('id').value = '"+cpf+"';" +
                        "document.getElementById('pw').value = '"+senha+"';" +
                        ";document.querySelectorAll(\"input[value=Autenticar]\")[0].click()";
                webview.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        loginSigpet.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        editor.putString("cpf_sigpet", cpf);
                        editor.putString("senha_sigpet", senha);
                        editor.apply();
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        webview.restoreState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
