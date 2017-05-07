package dominando.android.ex01_hello;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String url="";
    String parametros  = "";
    EditText login;
    EditText senha1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login = (EditText)findViewById(R.id.etEmail);
        senha1 = (EditText)findViewById(R.id.etPass);
        Button loginEntrar = (Button)findViewById(R.id.btnLogin);
        Button cadastrar = (Button)findViewById(R.id.btnReg);
        Button visualizar = (Button)findViewById(R.id.btnVisualizar);

        loginEntrar.setOnClickListener(this);
        cadastrar.setOnClickListener(this);
        visualizar.setOnClickListener(this);
    }


    public void onClick(View view){
        switch (view.getId()){

            case R.id.btnLogin:

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){

                    String email = login.getText().toString();
                    String senha = senha1.getText().toString();

                    if(email.isEmpty() || senha.isEmpty()){
                        Toast.makeText(getApplicationContext(),"nunhum campo pode esta vazio ",Toast.LENGTH_LONG).show();
                    }else{
                        url = "http://192.168.43.250:80/login.php";

                        parametros = "email=" + email + "&senha=" +senha;

                        new SolicitaDados().execute(url);

                    }

                }else{
                    Toast.makeText(getApplicationContext(),"nenhuma conexao encontrada" ,Toast.LENGTH_LONG).show();
                }


                break;

            case R.id.btnReg:

                Intent TelaRegistro = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(TelaRegistro);
                LoginActivity.this.finish();
                break;

            case R.id.btnVisualizar:

                Intent TelaVisualizar = new Intent(LoginActivity.this,Mapa_visualizacao.class);
                LoginActivity.this.startActivity(TelaVisualizar);
                LoginActivity.this.finish();
                break;
            default:


        }


    }


    private class SolicitaDados extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {


            return Conexao.postDados(urls[0], parametros);
        }


        @Override
        protected void onPostExecute(String resultado) {

            //login.setText(resultado);

            if(resultado.contains("login_ok")){
                Intent TelaLogado = new Intent(LoginActivity.this,MapaActivity.class);
                LoginActivity.this.startActivity(TelaLogado);
                LoginActivity.this.finish();
            }
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

    }
    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onRestart(){
        super.onRestart();

    }

    @Override
    protected void onStop(){
        super.onStop();

    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
