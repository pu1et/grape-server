package com.nexsol.grape.util;

import com.nexsol.grape.dto.member.ImportMemberDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class ImportUtil {

    @Value("${import.restApiKey}")
    public String restApiKey;
    @Value("${import.restApiSecretKey}")
    public String restApiSecretKey;
    @Value("${import.accessTokenUrl}")
    public String accessTokenUrl;
    @Value("${import.certUrl}")
    public String certUrl;

    private String accessToken;
    private long expiredAt;

    // 참고 : https://kkiuk.tistory.com/314
    private String getAccessToken(){
        try {

            URL url = new URL(accessTokenUrl);
            JSONObject object = new JSONObject();
            object.put("imp_key", restApiKey);
            object.put("imp_secret", restApiSecretKey);

            String params = object.toString();

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setConnectTimeout(1000); // 접속 지연시 강제로 timeout 처리
            con.setReadTimeout(1000); // 서버에서 응답이 오지 않는 경우 강제로 timeout 처리

            con.setDoOutput(true); // POST 파라미터 전달을 위한 설정

            // 파라미터 넣기
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(params.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if(responseCode != 200){
                throw new IllegalStateException("아임포트 서버 연결을 실패했습니다.");

            }else{

                // 데이터 수신
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine = null;
                StringBuilder resp = new StringBuilder();

                while((inputLine = in.readLine()) != null){
                    resp.append(inputLine);
                }

                in.close();


                // 액세스 토큰과 만료시간 설정
                JSONObject message = new JSONObject(resp.toString());
                JSONObject response = message.getJSONObject("response");

                accessToken = response.getString("access_token");
                expiredAt = response.getLong("expired_at");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ImportMemberDto getMemberInfo(String impUid){
        // accessToken 은 Unix timestamp 기준이기 때문에 /1000 해줘야 함
        if(accessToken == null || expiredAt < System.currentTimeMillis()/1000){
            getAccessToken();
        }

        try {
            URL url = new URL(certUrl + "/" + impUid);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            con.setConnectTimeout(1000); // 접속 지연시 강제로 timeout 처리
            con.setReadTimeout(1000); // 서버에서 응답이 오지 않는 경우 강제로 timeout 처리

            con.setDoOutput(true);

            int responseCode = con.getResponseCode();

            if(responseCode != 200){
                throw new IllegalStateException("아임포트 서버 연결을 실패했습니다.");

            }else{

                // 데이터 수신
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine = null;
                StringBuilder resp = new StringBuilder();

                while((inputLine = in.readLine()) != null){
                    resp.append(inputLine);
                }

                in.close();

                JSONObject message = new JSONObject(resp.toString());
                JSONObject response = message.getJSONObject("response");

                return ImportMemberDto.builder()
                        .name(response.getString("name"))
                        .gender(response.getString("gender"))
                        .birth(response.getString("birthday"))
                        .phone(response.getString("phone"))
                        .importKey(response.getString("unique_key"))
                        .build();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
