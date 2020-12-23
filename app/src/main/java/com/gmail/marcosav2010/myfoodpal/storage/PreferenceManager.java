package com.gmail.marcosav2010.myfoodpal.storage;

import android.content.SharedPreferences;

import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListerData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PreferenceManager {

    public static final String BASE_CONFIG = "{\"p_pesar\":[\"aceite\",\"aguacate\",\"albaricoque\",\"arroz\",\"banana\",\"bote de tomate frito\",\"cacahuete\",\"carne picada ternera\",\"cerezas\",\"chipirones\",\"ciruela\",\"contra muslo\",\"empanada\",\"ensalight\",\"filete de cerdo\",\"filetes pechuga de pollo\",\"fresas\",\"granada\",\"jamon york\",\"kaki\",\"kiwi\",\"langostino\",\"lata de atun\",\"lechuga\",\"lomo embuchado\",\"macarrones\",\"manzana\",\"melocoton\",\"melon\",\"nectarina\",\"pan de hogaza\",\"paraguayo\",\"pechuga de pavo\",\"pera\",\"pistacho\",\"pollo\",\"queso afuega\",\"queso cremoso\",\"queso untar\",\"salmon\",\"sandia\",\"spaghetti\",\"uvas\"],\"conversions\":{\"ajo diente\":\"3\",\"banana\":\"140\",\"cebolla\":\"-1\",\"jamon serrano\":\"10\",\"kiwi\":\"60\",\"lata de atun\":\"50\",\"laurel\":\"0.5\",\"lechuga\":\"-1\",\"pan de hamburguesa integral\":\"85\",\"pan hamburguesa\":\"75\",\"patatas baby\":\"400\",\"pimiento\":\"-1\",\"queso en polvo\":\"-1\",\"queso gouda\":\"25\",\"queso light havarti\":\"25\",\"tomate triturado natural\":\"-1\",\"tortellini con carne de bolsa\":\"250\",\"tortellini con espinacas de bolsa\":\"250\",\"tortellini con queso de bolsa\":\"250\",\"tortelloni de carne de bandeja\":\"250\",\"vino blanco\":\"-1\",\"ñoquis\":\"500\"},\"aliases\":{\"aceite de oliva virgen\":\"Aceite\",\"arroz integral\":\"Arroz integral\",\"atun claro\":\"Lata de atun\",\"atun y bacon\":\"Pizza de Atún y Bacon\",\"bacon\":\"Bacon para lentejas\",\"cacahuete tostado sin sal\":\"Cacahuetes\",\"chimichurri\":\"Salsa Chimichurri\",\"creado\":\"Pan de hogaza\",\"filete de pechuga(pollo)\":\"Filetes pechuga de pollo\",\"freson\":\"Fresas\",\"granada\":\"Granada\",\"huevo entero\":\"Huevos\",\"integral familiar\":\"Pan rebanadas integral\",\"jamon\":\"Jamon york\",\"jamon serrano\":\"Jamon Serrano\",\"jamón\":\"Jamon york\",\"ketchup\":\"Ketchup\",\"lasaña con salsa boloñesa\":\"Lasaña boloñesa\",\"lenteja pardina\":\"Lentejas\",\"lomo embuchado extra\":\"Lomo Embuchado\",\"mayo\":\"Mayonesa\",\"mostaza\":\"Mostaza\",\"pan burger integral\":\"Pan de hamburguesa integral\",\"pan hamburguesa (maxi)\":\"Pan Hamburguesa\",\"patata bolsa\":\"Patatas baby\",\"pechuga de pavo reducido en sal\":\"Pechuga De Pavo\",\"pizza fresca 4 quesos\":\"Pizza 4 Quesos\",\"pollo asado\":\"Pollo guisado\",\"queso gouda tierno\":\"Queso Gouda\",\"salsa worcestershire\":\"Salsa Worcestershire (Perrins)\",\"tiras de bacon\":\"Tiras/Tacos de Bacon\",\"tomate frito con aceite de oliva virgen extra\":\"Bote de tomate frito\",\"tomates frescos\":\"Tomates\",\"tortellini con carne m10\":\"Tortellini Con Carne de Bolsa\",\"tortellini con queso m10\":\"Tortellini Con Queso de Bolsa\",\"tortellini con ricotta y espinacas\":\"Tortellini Con Espinacas de Bolsa\",\"tortelloni carne\":\"Tortelloni de Carne de Bandeja\",\"trucha al horno\":\"Trucha\",\"vino blanco\":\"Vino Blanco\"},\"buy_measured\":[\"carne picada\",\"chipirones\",\"ensalada césar\",\"ensalight\",\"filete de cerdo\",\"filetes de ternera\",\"filetes pechuga de pollo\",\"jamon york\",\"langostino\",\"lata de atun\",\"pan de hamburguesa integral\",\"pan hamburguesa\",\"patatas baby\",\"salmon ahumado\",\"tortellini con carne de bolsa\",\"tortellini con espinacas de bolsa\",\"tortellini con queso de bolsa\",\"tortelloni de carne de bandeja\",\"ñoquis\"],\"unit_aliases\":[\"item\",\"piece\",\"pieces\",\"pieza\",\"piezas\",\"slice\",\"u\",\"ud\",\"uds\",\"unidad\",\"unidades\",\"unit\",\"units\"],\"p_picar\":[\"ajo diente\",\"cebolla\",\"pimiento\"],\"exceptions\":[\"aceite\",\"ajo diente\",\"albahaca\",\"alcohol\",\"bacon para lentejas\",\"cebolla\",\"cebolla en polvo\",\"chorizo aperitivo oreado\",\"clara\",\"cocina\",\"evobeef\",\"ginebra\",\"huevos\",\"ketchup\",\"kiwi\",\"laurel\",\"lechuga\",\"lentejas\",\"merluza\",\"mostaza\",\"oregano\",\"pan de hogaza\",\"pan rallado\",\"perejil\",\"pimienta negra\",\"sal con ajo\",\"sal de ajo\",\"tomates\",\"tomillo\",\"vino blanco verdejo\",\"zanahoria\"],\"p_sacar\":[\"huevo\",\"jamon serrano\",\"laurel\",\"pan de hogaza\",\"pan hamburguesa\",\"pan rebanadas integral\",\"queso en polvo\",\"tomate triturado natural\",\"vino\"]}";

    private static final String MFP_CONFIG = "mfp_config";
    private static final String MFP_USERNAME = "mfp_username";
    private static final String MFP_PASSWORD = "mfp_password";

    private final SharedPreferences preferences;

    private ListerData ld;

    public ListerData getListerData() {
        if (ld == null)
            reloadListerData();

        return ld;
    }

    private void reloadListerData() {
        ld = new ListerData(getMFPConfig());
        ld.load();
    }

    public void saveMFPConfig(String config) {
        preferences.edit().putString(MFP_CONFIG, config).apply();
        reloadListerData();
    }

    public void saveCredentials(String username, String password) {
        preferences.edit().putString(MFP_USERNAME, username).putString(MFP_PASSWORD, password).apply();
    }

    public String getMFPConfig() {
        return preferences.getString(MFP_CONFIG, BASE_CONFIG);
    }

    public String getMFPUsername() {
        return preferences.getString(MFP_USERNAME, null);
    }

    public String getMFPPassword() {
        return preferences.getString(MFP_PASSWORD, null);
    }
}
