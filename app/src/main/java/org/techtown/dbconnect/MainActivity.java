package org.techtown.dbconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText editText; // url 주소를 입력받는 텍스트 박스
    TextView textView; // 입력받은 url 에서 가져온 정보를 표시하는 텍스트 박스

    String urlStr; // url 주소 받을 변수

    Handler handler = new Handler();// Thread에서 전달받은 값을 메인으로 가져오기 위한 Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {// saveInstanceState -> 상태저장
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText); // 앱 내에서 위치 확인
        textView = findViewById(R.id.textView); // 앱 내에서 위치 확인

        Button button = findViewById(R.id.button); // 앱 내에서 위치확인
        button.setOnClickListener(new View.OnClickListener() { // 버튼을 눌렀을 때
            @Override
            public void onClick(View v) {
                urlStr = editText.getText().toString();

                RequestThread thread = new RequestThread(); // Thread 생성
                thread.start(); // Thread 시작
            }
        });
    }

    class RequestThread extends Thread { // DB를 불러올 때도 앱이 동작할 수 있게 하기 위해 Thread 생성
        @Override
        public void run() { // 이 쓰레드에서 실행 될 메인 코드
            try {
                URL url = new URL(urlStr); // 입력받은 웹서버 URL 저장
                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // DB에 연결
                if(conn != null){ // 만약 연결이 되었을 경우
                    conn.setConnectTimeout(10000); // 10초 동안 기다린 후 응답이 없으면 종료
                    conn.setRequestMethod("GET"); // GET 메소드 : 웹 서버로 부터 리소스를 가져온다.
                    conn.setDoInput(true); // 서버에서 온 데이터를 입력받을 수 있는 상태인가? true
                    conn.setDoOutput(true); // 서버에서 온 데이터를 출력할 수 있는 상태인가? true

                    int resCode = conn.getResponseCode(); // 응답 코드를 리턴 받는다.
                    if(resCode == HttpURLConnection.HTTP_OK){ // 만약 응답 코드가 200(=OK)일 경우
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        // BufferedReader() : 엔터만 경계로 인식하고 받은 데이터를 String 으로 고정, Scanner 에 비해 빠름!
                        // InputStreamReader() : 지정된 문자 집합 내의 문자로 인코딩
                        // getInputStream() : url 에서 데이터를 읽어옴
                        String line = null; // 웹에서 가져올 데이터를 저장하기위한 변수
                        while(true){
                            line = reader.readLine(); // readLine() : 한 줄을 읽어오는 함수
                            if(line == null) // 만약 읽어올 줄이 없으면 break
                                break;
                            println(line); // 출력 *80번째 줄의 함수*
                        }
                        reader.close(); // 입력이 끝남
                    }
                    conn.disconnect(); // DB연결 해제
                }
            } catch (Exception e) { //예외 처리
                e.printStackTrace(); // printStackTrace() : 에러 메세지의 발생 근원지를 찾아서 단계별로 에러를 출력
            }
        }
    }

    public void println(final String data){ // final : 변수의 상수화 => 변수 변경 불가
        handler.post(new Runnable() {
            // post() : 핸들러에서 쓰레드로 ()를 보냄
            // Runnable() : 실행 코드가 담긴 객체
            @Override
            public void run() {
                textView.append(data);
            } // run() : 실행될 코드가 들어있는 메소드
        });
    }
}
