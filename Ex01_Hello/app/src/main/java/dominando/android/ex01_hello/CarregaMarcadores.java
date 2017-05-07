package dominando.android.ex01_hello;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CarregaMarcadores {


    public static final String LOCALIZACAO_URL_JSON =
            "http://192.168.43.250:80/carregarMarcadores.php";

    private static HttpURLConnection connectar(String urlArquivo) throws IOException{
        final int SEGUNDOS =1000;
        URL url = new URL(urlArquivo);
        HttpURLConnection conexao =(HttpURLConnection)url.openConnection();
        conexao.setReadTimeout(10*SEGUNDOS);
        conexao.setConnectTimeout(15*SEGUNDOS);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);
        conexao.connect();
        return conexao;
    }

    public static boolean temConexao(Context ctx){
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static List<IniciaMarcadores> carregarLocalizacaoJson(){
        try{
            HttpURLConnection conexao = connectar(LOCALIZACAO_URL_JSON);
            int resposta = conexao.getResponseCode();
            if(resposta == HttpURLConnection.HTTP_OK){
                InputStream is = conexao.getInputStream();
                JSONObject json = new JSONObject(bytesParaString(is));
                return lerJsonLocalizacao(json);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<IniciaMarcadores> lerJsonLocalizacao(JSONObject json) throws JSONException{
        List<IniciaMarcadores> listaDeLocalizacoes = new ArrayList<IniciaMarcadores>();
        JSONArray jsonLocalizacao = json.getJSONArray("localizacao");
        for(int i = 0; i<jsonLocalizacao.length();i++){
            JSONObject jsonLocais = jsonLocalizacao.getJSONObject(i);
            IniciaMarcadores iniciaMarcadores = new IniciaMarcadores(
                    jsonLocais.getString("latitude"),
                    jsonLocais.getString("longitude"),
                    jsonLocais.getString("data"),
                    jsonLocais.getString("horario"),
                    jsonLocais.getString("descricao")
            );
            listaDeLocalizacoes.add(iniciaMarcadores);

        }
        return listaDeLocalizacoes;
    }

    private static String bytesParaString(InputStream is) throws  IOException{
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        int bytesLidos;
        while ((bytesLidos = is.read(buffer)) != -1){
            bufferzao.write(buffer,0,bytesLidos);
        }
        return new String(bufferzao.toByteArray(),"UTF-8");
    }



}
