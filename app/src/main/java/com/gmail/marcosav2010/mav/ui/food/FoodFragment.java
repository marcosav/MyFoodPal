package com.gmail.marcosav2010.mav.ui.food;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gmail.marcosav2010.mav.R;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.CustomFoodFormater;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodList;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;

public class FoodFragment extends Fragment {

    public static final String CONFIG_DATA = "{\"exceptions\":[\"Oregano\",\"Ajo Diente\",\"Perejil\",\"Lechuga\",\"Alcohol\",\"cocina\",\"Aceite\",\"Vino Blanco Verdejo\",\"Albahaca\",\"Clara\",\"Ginebra\",\"Pimienta Negra\",\"Laurel\",\"Tomillo\",\"sal con ajo\",\"Sal De Ajo\",\"Pan rallado\",\"Cebolla en polvo\",\"Merluza\",\"Cebolla\",\"Ketchup\",\"Huevos\",\"Mostaza\",\"Tomates\",\"Chorizo Aperitivo oreado\",\"Bacon para lentejas\",\"Lentejas\",\"Zanahoria\",\"Kiwi\"],\"aliases\":{\"Cacahuete tostado sin sal\":\"Cacahuetes\",\"FRESON\":\"Fresas\",\"creado\":\"Pan de hogaza\",\"Mostaza\":\"Mostaza\",\"Atun Claro\":\"Lata de atun\",\"Arroz Integral\":\"Arroz integral\",\"Huevo Entero\":\"Huevos\",\"Tomate Frito Con Aceite De Oliva Virgen Extra\":\"Bote de tomate frito\",\"Pollo Asado\":\"Pollo guisado\",\"patata bolsa\":\"Patatas baby\",\"Lenteja Pardina\":\"Lentejas\",\"Ensalada\":\"Ensalight\",\"ketchup\":\"Ketchup\",\"jamon\":\"Jamon york\",\"Jamon Serrano\":\"Jamon Serrano\",\"filete de pechuga(pollo)\":\"Filetes pechuga de pollo\",\"Tomates Frescos\":\"Tomates\",\"Queso Gouda Tierno\":\"Queso Gouda\",\"Pan burger integral\":\"Pan de hamburguesa integral\",\"Tiras De Bacon\":\"Tiras/Tacos de Bacon\",\"Bacon\":\"Bacon para lentejas\",\"Integral Familiar\":\"Pan rebanadas integral\",\"Vino Blanco\":\"Vino Blanco\",\"chimichurri\":\"Salsa Chimichurri\",\"salsa worcestershire\":\"Salsa Worcestershire (Perrins)\",\"Aceite De Oliva Virgen\":\"Aceite\",\"Pizza fresca 4 quesos\":\"Pizza 4 Quesos\",\"Pechuga De Pavo Reducido En Sal\":\"Pechuga De Pavo\",\"Lomo Embuchado Extra\":\"Lomo Embuchado\"},\"unit_aliases\":[\"pieza\",\"piezas\",\"pieces\",\"piece\",\"u\",\"ud\",\"uds\",\"units\",\"unit\",\"unidades\",\"unidad\"],\"buy_measured\":[\"Filetes pechuga de pollo\",\"Jamon york\",\"carne picada\",\"Langostino\",\"Chipirones\",\"Salmon Ahumado\",\"Pan de hamburguesa integral\",\"Ensalight\",\"Lata de atun\",\"Filete De Cerdo\",\"Filetes De Ternera\"],\"conversions\":{\"Lata de atun\":50,\"Kiwi\":60,\"Patatas baby\":400,\"Ensalight\":230,\"Queso Light Havarti\":25,\"queso gouda\":25,\"ñoquis\":500,\"Pan de hamburguesa integral\":85,\"Jamon Serrano\":10,\"Banana\":140,\"Vino Blanco\":-1,\"Lechuga\":-1,\"Bote de tomate frito\":-1,\"Pimiento\":-1,\"Cebolla\":-1,\"Ajo Diente\":3,\"Tomate Triturado Natural\":-1,\"Laurel\":0.5,\"Queso en polvo\":-1},\"dated_food\":{\"Pan de hogaza\":[1]},\"p_sacar\":[\"Huevo\",\"Vino\",\"Jamon Serrano\",\"Pan de hogaza\",\"Bote de tomate frito\",\"Laurel\",\"Tomate Triturado Natural\",\"Queso en polvo\"],\"p_pesar\":[\"Aguacate\",\"Pollo\",\"Filetes pechuga de pollo\",\"Uvas\",\"Fresas\",\"Manzana\",\"cacahuete\",\"Pistacho\",\"arroz\",\"Aceite\",\"Salmon\",\"Spaghetti\",\"Contra muslo\",\"Kiwi\",\"Banana\",\"Lata de atun\",\"Lechuga\",\"Jamon York\",\"Lomo Embuchado\",\"Pechuga De Pavo\",\"Empanada\",\"Sandia\",\"Nectarina\",\"Ciruela\",\"Cerezas\",\"Paraguayo\",\"Ensalight\",\"Langostino\",\"Chipirones\",\"Melocoton\",\"Pan de hogaza\",\"Melon\"],\"p_picar\":[\"Pimiento\",\"Ajo Diente\",\"Cebolla\"]}";

    private RadioButton buyRB, prepareRB;
    private Button copyBT, wpBT;
    private FloatingActionButton genBT;
    private EditText foodTextContainer;
    private EditText dateOpt, mealsOpt;
    private TextView backgroundLB;
    private View root;

    private Date date;
    private boolean buy;
    private String meals;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_food, container, false);

        buyRB = root.findViewById(R.id.buyRB);
        prepareRB = root.findViewById(R.id.prepareRB);
        buyRB.setOnClickListener(this::onBuySelect);
        prepareRB.setOnClickListener(this::onPrepareSelect);

        backgroundLB = root.findViewById(R.id.backgroundLB);

        copyBT = root.findViewById(R.id.copyBT);
        wpBT = root.findViewById(R.id.wpBT);
        genBT = root.findViewById(R.id.genBT);

        copyBT.setOnClickListener(this::onShareClick);
        wpBT.setOnClickListener(this::onShareClick);

        genBT.setOnClickListener(this::onGenClick);

        dateOpt = root.findViewById(R.id.genDateOptField);
        mealsOpt = root.findViewById(R.id.genMealsOptField);

        mealsOpt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                meals = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        dateOpt.setOnClickListener(v -> pickDate());

        foodTextContainer = root.findViewById(R.id.foodTextContainer);

        buyRB.callOnClick();

        return root;
    }

    private void onBuySelect(View v) {
        setDate(getTomorrow());
        setMeals(getString(R.string.def_meals_opt));
        buy = true;
    }

    private void onPrepareSelect(View v) {
        Calendar now = Calendar.getInstance();
        setDate(now);
        setMeals(now.get(Calendar.HOUR_OF_DAY) >= 16 ? "2" : "1");
        buy = false;
    }

    private Calendar getTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    private void setDate(Calendar c) {
        Date d = c.getTime();
        date = d;
        dateOpt.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(d));
    }

    private void setMeals(String meals) {
        this.meals = meals;
        mealsOpt.setText(meals);
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(root.getContext(), (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            setDate(cal);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String getFoodContent() {
        return foodTextContainer.getText().toString();
    }

    private void onGenClick(View v) {
        editable(false);
        foodTextContainer.setText("");
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.generating);

        new MFPDayQuery(meals, buy, date, this).execute("", "");
    }

    @AllArgsConstructor
    private static class MFPDayQuery extends AsyncTask<String, Void, List<String>> {

        private String meals;
        private boolean buy;
        private Date date;

        private FoodFragment ff;

        protected List<String> doInBackground(String... login) {
            try {
                ListerData lc = new ListerData(CONFIG_DATA);
                lc.load();
                FoodList fl = new FoodList(lc, MFPSession.create(login[0], login[1]).getDayFood(date, meals, new CustomFoodFormater(lc)));
                return fl.toList(buy);
            } catch (IOException ex) {
                return null;
            }
        }

        protected void onPostExecute(List<String> got) {
            if (got == null)
                ff.onResultError();
            else
                ff.onResult(got);
        }
    }

    private void onResultError() {
        backgroundLB.setText(R.string.empty_list);

        Toast.makeText(getContext(), R.string.generation_error, Toast.LENGTH_LONG).show();
    }

    private void onResult(List<String> got) {
        if (got.isEmpty()) {
            backgroundLB.setText(R.string.empty_list);
            return;
        }

        backgroundLB.setVisibility(View.INVISIBLE);

        foodTextContainer.append(getString(buy ? R.string.buy_header : R.string.prepare_header));
        got.forEach(f -> foodTextContainer.append("\n - " + f));

        editable(true);
    }

    private void editable(boolean b) {
        foodTextContainer.setClickable(b);
        foodTextContainer.setCursorVisible(b);
        foodTextContainer.setFocusable(b);
        foodTextContainer.setFocusableInTouchMode(b);
    }

    private void onShareClick(View v) {
        Integer msg = tryShare(v);
        if (msg == null)
            return;
        Toast.makeText(getContext(), getString(msg), Toast.LENGTH_SHORT).show();
    }

    private Integer tryShare(View v) {
        String content = getFoodContent();
        if (content.trim().isEmpty())
            return R.string.no_content;

        if (v.getId() == copyBT.getId()) {
            try {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied List", content);
                clipboard.setPrimaryClip(clip);

                return R.string.successfully_copied;
            } catch (Exception ex) {
                return R.string.error_copy;
            }

        } else if (v.getId() == wpBT.getId()) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);

            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, content);

            try {
                getActivity().startActivity(whatsappIntent);
            } catch (Exception ex) {
                return R.string.error_whatsapp;
            }
        }

        return null;
    }
}