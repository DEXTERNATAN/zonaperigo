package dominando.android.ex01_hello;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    String parametros = "";
    String url = "";

    private TextView cancela;
    EditText nome1,email1,senha1,confirmasenha;
    Button registrar,cancelar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nome1 = (EditText)findViewById(R.id.nome);
        email1 = (EditText)findViewById(R.id.etEmail);
        senha1 = (EditText)findViewById(R.id.etPass);
        confirmasenha = (EditText)findViewById(R.id.etconfirm);
        registrar = (Button)findViewById(R.id.btnReg);
        cancela = (TextView)findViewById(R.id.tvLogin);

        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelar = new Intent(RegisterActivity.this,LoginActivity.class);
                RegisterActivity.this.startActivity(cancelar);
                RegisterActivity.this.finish();

            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){

                    String email = email1.getText().toString();
                    String nome = nome1.getText().toString();
                    String senha = senha1.getText().toString();
                    String senhaConfirma = confirmasenha.getText().toString();

                    if(nome.isEmpty() ||email.isEmpty() || senha.isEmpty() || senhaConfirma.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "nunhum campo pode esta vazio ", Toast.LENGTH_LONG).show();
                    }else if(senha.equals(senhaConfirma)){

                            url = "http://192.168.43.250:80/registrar.php";

                            parametros = "nome="+ nome+ "&email=" + email + "&senha=" +senha;

                            new SolicitaDados().execute(url);
                        }else {

                            Toast.makeText(RegisterActivity.this,"senha devem ser iguais",Toast.LENGTH_LONG).show();
                        }

                        }else{
                    Toast.makeText(getApplicationContext(),"nenhuma conexao encontrada" ,Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private class SolicitaDados extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {

            return Conexao.postDados(urls[0], parametros);

        }


        @Override
        protected void onPostExecute(String resultado) {

            //nome1.setText(resultado);
            if(resultado.contains("email_erro")){

                Toast.makeText(getApplicationContext(),"Este email já está cadastrado",Toast.LENGTH_LONG).show();

            }else if(resultado.contains("registro_ok")){
                Toast.makeText(getApplicationContext(),"Registro concluido com sucesso",Toast.LENGTH_LONG).show();
                Intent cadastrado = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(cadastrado);
            }else{
                Toast.makeText(getApplicationContext(),"Ocorreu um erro",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onPause(){
        super.onPause();

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
