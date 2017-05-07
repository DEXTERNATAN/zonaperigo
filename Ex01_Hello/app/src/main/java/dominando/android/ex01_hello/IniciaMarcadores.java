package dominando.android.ex01_hello;

import java.io.Serializable;

/**
 * Created by THIAGO on 04/04/2017.
 */

public class IniciaMarcadores implements Serializable {
    public String latitude;
    public String longitude;
    public String data;
    public String horario;
    public String descricao;

    public IniciaMarcadores(String latitude, String longitude,String data,String horario,String descricao){

        this.latitude = latitude;
        this.longitude =longitude;
        this.data = data;
        this.horario = horario;
        this.descricao = descricao;

    }


}
