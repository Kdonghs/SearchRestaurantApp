package com.example.finalAssignment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String keyCode ="29c35793516841e7bf8e94a5f1cf522e";
    private final String address = "https://openapi.gg.go.kr/PlaceThatDoATasteyFoodSt";
    EditText editText;
    TextView textView;
    Button button;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>경기도 맛집 검색</font>"));

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    button.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void buttonClicked(View v){

        switch( v.getId() ){

            case R.id.button:

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        data = getData();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                textView.setText(data);
                            }
                        });
                    }
                }).start();
                break;
        }
    }


    String getData(){
        StringBuffer buffer = new StringBuffer();
        String str =  editText.getText().toString();
        int i=1;

        String queryUrl=address
                +"?&pIndex=1&pSize=100&Key="+keyCode
                +"&SIGUN_NM="+str;

        try {
            URL url= new URL(queryUrl);
            InputStream is= url.openStream();

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();

            xpp.setInput( new InputStreamReader(is, "UTF-8") );

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();
                        if(tag.equals("CODE")){
                            xpp.next();
                            if (xpp.getText().equals("INFO-200")) {
                                buffer.append("해당하는 검색 결과가 없습니다.\n");
                                buffer.append("다음의 조건을 확인하십시오\n\n");
                                buffer.append("-지역명을 정확히 적으십시오\n");
                                buffer.append("(과천시, 수원시, 안양시 등)\n\n");
                                buffer.append("-지역명을 제외한 다른 조건으로는 검색이 불가능 합니다.\n\n");
                                buffer.append("-원하는 결과가 존재하지 않을수도 있습니다\n");
                                return buffer.toString();
                            }
                        }

                        if(tag.equals("row"));
                        else if(tag.equals("SIGUN_NM")){
                            buffer.append(i+++".\n");
                            buffer.append("지역 :");
                            xpp.next();

                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("RESTRT_NM")){
                            buffer.append("가게이름 : ");
                            xpp.next();

                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("REPRSNT_FOOD_NM")){
                            buffer.append("대표메뉴 :");
                            xpp.next();

                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("TASTFDPLC_TELNO")){
                            buffer.append("가게번호 : ");
                            xpp.next();

                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("REFINE_LOTNO_ADDR")){
                            buffer.append("가게주소 :");
                            xpp.next();

                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();

                        if(tag.equals("row")){
                            buffer.append("\n");
                        }
                        break;
                }

                eventType= xpp.next();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public void clickExit(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("앱을 종료하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();
    }
}