package org.akshara.Util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.akshara.R;
import org.akshara.model.ConfigModel;
import org.ekstep.genieservices.sdks.Partner;
import org.ekstep.genieservices.sdks.Telemetry;
import org.ekstep.genieservices.sdks.UserProfile;
import org.ekstep.genieservices.sdks.response.GenieResponse;

import java.io.File;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;



/**
 * Created by Jaya on 9/24/2015.
 */
public class Util extends Application {
    public static final boolean DEBUG=true;
    private static String TAG =Util.class.getSimpleName();
    static String FILE_PATH;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private HashMap<String,String>hashMap;
    private Context mContext;
    public static final String USERMODEL_DATA="userModel";
    public static final String CONFIGMODEL_DATA="configModel";

    public static String TEMPLATE_NAME="Akshara";
    private String PARTNER_REG="partnerreg";
    public static final String PACKAGENAME="org.ekstep.genieservices";
    public static  final String GENIE_PACKAGENAME="org.ekstep.android.genie";
    public  static  String handle="NEW_CHILD_AKSHARA";
    public  static String avatar="AKSHARA";
    public  static final String partnerId="org.ekstep.partner.akshara";
    public static final String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgDm/lRk4ZU4ZUAaLRqX\n" +
            "hzxGbRzSFOjOsIEgGAMYkh3+pULK/9evvOhI5X2afbnLLTo6h9MzjzWKio/G5jTH\n" +
            "8YRS61ohBnhL8TKkVwXlU9GYnvOZimIoizPXimhNrVcAYvo4GNwrB9sxGFyNPup0\n" +
            "CBCnyWifdhKOWGo5LGhNCP9ehmJJchPw23RN+VeF/fsW9WVJNTZFXy4WYbsM7YVG\n" +
            "cQWYgCZX4eNqBcckP3aXaFTej1pPHfti2n+BLmudGK60lnZ4ePBidEi6WoPzpMrd\n" +
            "MnwzkYOnQ8KBV0LKJr0vzqATzxGMC85fo1OUm+ZMobdl8SCLAzn5+2WFnNKyct1m\n" +
            "twIDAQAB";
    private String spinnerDistric_selected,spinnerBlock_selected ,spinnerCluster_selected,spinnerSchool_selected,spinnerSchoolCode_selected;
    private String language;
    private Partner partner;
    private Telemetry telemetry;
    private UserProfile userProfile;
    public static final int dropDownItemSize=50;

    public Telemetry getTelemetry() {
        return telemetry;
    }

    public void setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public String getSpinnerSchoolCode_selected() {
        return spinnerSchoolCode_selected;
    }

    public void setSpinnerSchoolCode_selected(String spinnerSchoolCode_selected) {
        this.spinnerSchoolCode_selected = spinnerSchoolCode_selected;
    }


    public String getSpinnerSchool_selected() {
        return spinnerSchool_selected;
    }

    public void setSpinnerSchool_selected(String spinnerSchool_selected) {
        this.spinnerSchool_selected = spinnerSchool_selected;
    }
    public  static final String student_fileName = "klp_gka_student_list.csv";
    public  static final String school_fileName = "klp_gka_school_list.csv";
    public  static final String aksharacsv_fileName = "aksharacsv.ser";

    public  static final  String UID="uid";

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSpinnerDistric_selected() {
        return spinnerDistric_selected;
    }

    public void setSpinnerDistric_selected(String spinnerDistric_selected) {
        this.spinnerDistric_selected = spinnerDistric_selected;
    }

    public String getSpinnerBlock_selected() {
        return spinnerBlock_selected;
    }

    public void setSpinnerBlock_selected(String spinnerBlock_selected) {
        this.spinnerBlock_selected = spinnerBlock_selected;
    }

    public String getSpinnerCluster_selected() {
        return spinnerCluster_selected;
    }

    public void setSpinnerCluster_selected(String spinnerCluster_selected) {
        this.spinnerCluster_selected = spinnerCluster_selected;
    }

    public Util(){}
    public Util(Context context){
        this.mContext=context;
        sharedPreferences=mContext.getSharedPreferences("partner",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void storeConfigModel(ConfigModel configModel){
        // Convert the object to a JSON string
        String json = new Gson().toJson(configModel);
        if(DEBUG)
            Log.d(TAG,"storeConfigModel json==>"+json);
        //Strore CONFIGMODEL_DATA
        editor.putString(CONFIGMODEL_DATA, json);
        editor.commit();
    }
    public  ConfigModel getConfigModel(){
        ConfigModel configModel=null;
        // Now convert the JSON string back to your java object
        Type type = new TypeToken<ConfigModel>(){}.getType();
        String json=sharedPreferences.getString(CONFIGMODEL_DATA, "NOTHING");
       /* if(DEBUG)
            Log.d(TAG,"getConfigModel json==>"+json);
       */ if(!json.equals("NOTHING"))
            configModel = new Gson().fromJson(json, type);

        /*if(DEBUG)
            Log.d(TAG,"configModel:"+configModel);
*/
        return configModel;
    }

    public  String getJsonData(){
        ConfigModel configModel=null;
        // Now convert the JSON string back to your java object
        Type type = new TypeToken<ConfigModel>(){}.getType();
        String json=sharedPreferences.getString(CONFIGMODEL_DATA, "NOTHING");
        if(DEBUG)
            Log.d(TAG,"getConfigModel json==>"+json);

        return json;
    }




    public  void storePartnerRegistration(String partnerid){
        editor.putString(PARTNER_REG, partnerid);
        editor.commit();
 }
    public void clearSharedPreferences(){
        //clear sharedPreferences
        editor.clear();
        editor.commit();

    }


    public boolean isRegisterPartner(){
        if(!(sharedPreferences.getString(PARTNER_REG,"NOTHING").equals("NOTHING")))
          return   true;
        else
            return   false;

    }


    /*
   *@return String FILE_PATH
   */
    public static String getFilePath(String fileName){

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            FILE_PATH = sdCard.getAbsolutePath()+ "/Docs/" + fileName;
            if(DEBUG)
                Log.d(TAG, "FILE_PATH :" + FILE_PATH);

        }catch (Exception e){
            e.printStackTrace();
        }

        return  FILE_PATH;
    }
    public static String generateUniqueId(){
        String sha1id = null;
        String timeStamp =String.valueOf(System.currentTimeMillis());
        String id= UUID.randomUUID().toString()+timeStamp;

        try {
            sha1id=sha1(id);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sha1id;

    }
    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }


    public static void showToastmessage(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /* To hide keyboard */
    public static void hideKeyboard(Activity activity,Context context) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);


        }
    }
    /* To show keyboard */
    public static void showKeyboard(Activity activity,Context context) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

       /* if (activity.getCurrentFocus() != null) {
           *//* InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
           *//* activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        }*/
    }

    public static void processSuccess(Context context ,Object object){
        GenieResponse response = (GenieResponse) object;
        Log.i("Successfull", response.getStatus());
        if(response.getResult()!=null){
            Log.i("Success Gson Response", response.getResult().toString());
        }

    }
    public static void processSendFailure(Context context ,Object object){
        GenieResponse response = (GenieResponse)object;
        String error = response.getError();
        Log.e("Genie Service Error Log",error);
        for(int i=0;i<response.getErrorMessages().size();i++){
            String errorPos=response.getErrorMessages().get(i);
            Log.e("Error info",errorPos);
        }
        if(response.getResult()!=null){
            Log.e("Failure Gson Response", response.getResult().toString());
        }

        if(error.equalsIgnoreCase(context.getResources().getString(R.string.invalid_event))){
            Toast.makeText(context, context.getResources().getString(R.string.invalid_event_message), Toast.LENGTH_LONG).show();
        }
        else if(error.equalsIgnoreCase(context.getResources().getString(R.string.db_error))){
            Toast.makeText(context, context.getResources().getString(R.string.db_error_message), Toast.LENGTH_LONG).show();
        }
        else if(error.equalsIgnoreCase(context.getResources().getString(R.string.validation_error))){
            Toast.makeText(context, context.getResources().getString(R.string.validation_error_message), Toast.LENGTH_LONG).show();
        }
        else if(error.equalsIgnoreCase(context.getResources().getString(R.string.db_error))){
           // DialogUtils.showAppNotInstalledDialog(((Activity) context));
        }

    }
}
