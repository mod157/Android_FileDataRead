package pe.nammu.prototype.smile;

import android.Manifest;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String FILE_NAME = "/tmp.txt";
    private TextView tv;
    private ArrayList<String> data;
    private String state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textview);
        permissionCheck();
    }

    private void dataReadSDcard(File file){
        data = new ArrayList<>();

        BufferedReader br = null ;
        try {
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
                String str = null;
                while((str = br.readLine()) != null) {
                    data.add(str);
                    tv.append(str+"\n");
                }
            }
            else {
                tv.setText("파일이 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }
    private void fileDataRead(){
       if(checkExternalStorage()){
           //내장 path
            //File file = new File(Environment.getExternalStorageDirectory() + FILE_NAME);
           //외장
           File file = new File("/sdcard/"+FILE_NAME);
           //Log.d("Smile Path", file.getPath());
           dataReadSDcard(file);
       }
    }

    private boolean checkExternalStorage() {
        state = Environment.getExternalStorageState();
        // 외부메모리 상태
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 읽기 쓰기 모두 가능
           // Log.d("test", "외부메모리 읽기 쓰기 모두 가능");
            tv.setText("외부메모리 읽기 쓰기 모두 가능");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //읽기전용
            //Log.d("test", "외부메모리 읽기만 가능");
            tv.setText("외부메모리 읽기만 가능");
            return true;
        } else {
            // 읽기쓰기 모두 안됨
           // Log.d("test", "외부메모리 읽기쓰기 모두 안됨 : "+ state);
            tv.setText("외부메모리 읽기쓰기 모두 안됨 : "+ state);
            return false;
        }
    }
    private void permissionCheck(){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                fileDataRead();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("SD Card를 읽기 위해서는 권한이 필요합니다.")
                .setDeniedMessage("권한 설정을 하지 않으면 이용하기 어렵습니다.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setGotoSettingButton(true)
                .check();
    }

}
