package org.partner.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.partner.R;
import org.partner.Util.TelemetryEventGenertor;
import org.partner.Util.Util;
import org.partner.activity.MainActivity;
import org.partner.callback.CurrentGetuserResponseHandler;
import org.partner.callback.CurrentuserResponseHandler;
import org.partner.callback.EndSessionResponseHandler;
import org.partner.callback.ICurrentGetUser;
import org.partner.callback.ICurrentUser;
import org.partner.callback.IEndSession;
import org.partner.callback.IPartnerData;
import org.partner.callback.IStartSession;
import org.partner.callback.ITelemetryData;
import org.partner.callback.IUserProfile;
import org.partner.callback.PartnerDataResponseHandler;
import org.partner.callback.StartSessionResponseHandler;
import org.partner.callback.TelemetryResponseHandler;
import org.partner.callback.UserProfileResponseHandler;
import org.partner.customviews.CustomEditText;
import org.partner.customviews.CustomTextView;
import org.partner.customviews.MyProgressBar;
import org.partner.model.Age;
import org.partner.model.ConfigModel;
import org.partner.model.FieldModel;
import org.partner.model.InstructionsField;
import org.partner.model.SectionValue;
import org.ekstep.genieservices.aidls.domain.Profile;
import org.ekstep.genieservices.sdks.Partner;
import org.ekstep.genieservices.sdks.Telemetry;
import org.ekstep.genieservices.sdks.UserProfile;
import org.ekstep.genieservices.sdks.response.GenieResponse;

/**
 * Created by Dhruv on 10/7/2015.
 */
public class DisplayChildProfileFragment extends Fragment implements IEndSession,IStartSession,IPartnerData,
        IUserProfile,ICurrentUser,ICurrentGetUser,ITelemetryData{
    private boolean D= Util.DEBUG;
    private String TAG=DisplayChildProfileFragment.class.getSimpleName();
    private Context mContext;
    private  Fragment mFragment;
    private Button Register_btn;
    private Spinner spinnerLanguage,spinnerGender,spinnerClass;
    private LinearLayout displayLayout;
    private TextView child_nameLabel,father_nameLabel,child_classLabel,genderLabel,dobLabel,languageLabel;
    private EditText child_name,father_name;
    private  HashMap<Integer,String>hashMap_order=new HashMap<>();
    private Map<String,Object> hashMapData=new HashMap<>();
    private  CustomTextView[] textViewHeading=new CustomTextView[60];
    private  CustomTextView[] textViewInstructions=new CustomTextView[60];
    private  CustomTextView[] textViews=new CustomTextView[60];

    private  CustomTextView[] textViewData=new CustomTextView[60];
    private CustomEditText[] editTexts=new CustomEditText[60];
    private Spinner[] spinners=new Spinner[60];
    private int sizeOfMap=0;
    private  ArrayList<String> language_List=new ArrayList<>();
    private  String[]gender_array={"MALE","FEMALE"};
    private  String[]child_classarray={"4","5"};
    private  HashMap<String,String> code_language_List=new HashMap<>();
    private List<HashMap<String,String>>hashMapLanguage;
    private DatePickerDialog mDatePickerDialog=null;
    private int year;
    private int month;
    private int day;
    private String mSelectedDob="";
    private TextView mTxt_dob=null;
    private View rootView;
    private  GradientDrawable gradientDrawable;
    private Profile profile;
    private UserProfile userProfile;
    private UserProfileResponseHandler userProfileResponseHandler;
    private CurrentuserResponseHandler currentuserSetResponseHandler;
    private CurrentGetuserResponseHandler currentGetuserResponseHandler;
    private Partner partner;
    private Telemetry telemetry;
    private StartSessionResponseHandler startSessionResponseHandler;
    private EndSessionResponseHandler endSessionResponseHandler;
    private TelemetryResponseHandler telemetryResponseHandler;
    private PartnerDataResponseHandler partnerDataResponseHandler;
    private String UID;
    private String code;
    private String handle="";
    private MyProgressBar progressBar;
    private ScrollView scrollViewChildContainer;
    private CheckBox[] checkBoxes=new CheckBox[60];
    private RadioGroup[] radioGroups=new RadioGroup[120];
    private RadioButton[] radioButtons=new RadioButton[60];
    private ConfigModel configModel;
    private boolean ischkRB=false;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // ((MainActivity)mContext).showBackIcon("Child Registration",mFragment);
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        mContext=getActivity();
        mFragment=this;
        super.onCreate(savedInstanceState);

    }

    LayoutInflater layoutInflater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater=getLayoutInflater(savedInstanceState);
        rootView=inflater.inflate(R.layout.display_child_profile, container, false);
        Register_btn=(Button)rootView.findViewById(R.id.Register_btn);
        displayLayout=(LinearLayout)rootView.findViewById(R.id.displayLayout);
        progressBar=(MyProgressBar)rootView.findViewById(R.id.sendingDetails);
        scrollViewChildContainer=(ScrollView)rootView.findViewById(R.id.scrollViewChildContainer);

                createChildForm();


        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sectionSize = configModel.getSectionValues().length;
                if (D)
                    Log.d(TAG, "Register_btn sectionSize==>" + sectionSize);

                SectionValue[] sectionValues = configModel.getSectionValues();
                boolean flag = true, noEmptyField = true;
                int checkEditFieldBlank = 0, noOfEditText = 0;
                try {
                    int i = 0;
                    for (int pos = 0; pos < sectionSize; pos++) {
                        //sectionValues[i]=new SectionValue();
                        if (D)
                            Log.d(TAG, "Register_btn section pos==>" + pos);

                        final FieldModel[] fieldModels = sectionValues[pos].getFieldModels();
                        int length = fieldModels.length;
                        if (D)
                            Log.d(TAG, "Register_btn sectionValues length==>" + length);


                        for (int j = 0; j < length; j++) {
                            i++;
                            // fieldModels[j]=new FieldModel();
                            if (D)
                                Log.d(TAG, i + "--.>i value .....Register_btn--------jjjjjj------------==>" + j);

                            final String fieldLabelName = fieldModels[j].getFieldName();
                            if (D)
                                Log.d(TAG, "fieldModels[j]  fieldLabelName==>" + fieldLabelName);


                            // int displayOrder = fieldModels[j].getDisplayOrder();
                            if (!fieldLabelName.equalsIgnoreCase("instructions")) {
                                String fieldType = fieldModels[j].getFieldValue().getFieldType();
                                String fieldInputType = fieldModels[j].getFieldValue().getFieldInputType();

                                if (D)
                                    Log.d(TAG, "fieldType" + fieldType);
                                if (D)
                                    Log.d(TAG, "fieldInputType" + fieldInputType);
                                if (D)
                                    Log.d(TAG, "fieldType at pos inside Register :" + i);

                                if (fieldType.equals("Text")) { // label Value


                                    checkEditFieldBlank++;
                                    if (D)
                                        Log.d(TAG, i + "-->i value fieldType at pos editTexts[i] :" + editTexts[i]);

                                    if (fieldInputType.equals("date")) {
                                        if (textViewData[i].getText().toString().trim().isEmpty()) {
                                            Util.showToastmessage(mContext, "Please enter " + fieldLabelName);
                                            checkEditFieldBlank = 0;
                                            noEmptyField = false;
                                            break;
                                        }

                                    } else{
                                        final int fieldPos = j;
                                        final int editRows = i;

                                        String charstr = editTexts[editRows].getText().toString();
                                        int maxvalue = fieldModels[fieldPos].getFieldValue().getValidation().getMaximum();
                                        int minvalue = fieldModels[fieldPos].getFieldValue().getValidation().getMinimum();

                                        if (D)
                                            Log.d(TAG, editRows + " editRows... charstr---->" + charstr);
                                        int charCount = charstr.length();
                                        if (D)
                                            Log.d(TAG, editRows + " editRows... charCount---->" + charCount);

                                          if (fieldInputType.equals("textEmailAddress")) {
                                            String emailid=editTexts[i].getText().toString();
                                            //for emailid
                                            Pattern pattern2 = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
                                            Matcher matcher2 = pattern2.matcher(emailid);
                                            Boolean emailpattern = matcher2.matches();

                                            if (emailid.trim().isEmpty()) {
                                                Util.showToastmessage(mContext, "Please enter " + fieldLabelName);
                                                checkEditFieldBlank = 0;
                                                noEmptyField = false;
                                                break;
                                            }else if(!emailpattern){
                                                Util.showToastmessage(mContext, "Please enter valid " + fieldLabelName);
                                                checkEditFieldBlank = 0;
                                                noEmptyField = false;
                                                break;
                                            }else if (charCount < minvalue) { // Validation
                                                Toast.makeText(mContext, fieldLabelName + " can't be less than " + minvalue + " character.", Toast.LENGTH_LONG).show();
                                                checkEditFieldBlank = 0;
                                                noEmptyField = false;
                                                break;

                                            } else if (charCount > maxvalue) { // Validation
                                                Toast.makeText(mContext, fieldLabelName + " can't be more than " + maxvalue + " character.", Toast.LENGTH_LONG).show();
                                                checkEditFieldBlank = 0;
                                                noEmptyField = false;
                                                break;
                                            }

                                        }else if (editTexts[i].getText().toString().trim().isEmpty()) {
                                            Util.showToastmessage(mContext, "Please enter " + fieldLabelName);
                                            checkEditFieldBlank = 0;
                                            noEmptyField = false;
                                            break;
                                        } else if (charCount < minvalue) { // Validation
                                            Toast.makeText(mContext, fieldLabelName + " can't be less than " + minvalue + " character.", Toast.LENGTH_LONG).show();
                                            checkEditFieldBlank = 0;
                                            noEmptyField = false;
                                            break;

                                        } else if (charCount > maxvalue) { // Validation
                                            Toast.makeText(mContext, fieldLabelName + " can't be more than " + maxvalue + " character.", Toast.LENGTH_LONG).show();
                                            checkEditFieldBlank = 0;
                                            noEmptyField = false;
                                            break;
                                        }



                                    }

                                    //setting value
                                    if (fieldInputType.equals("date"))
                                        hashMapData.put(fieldLabelName, textViewData[i].getText().toString());
                                    else
                                        hashMapData.put(fieldLabelName, editTexts[i].getText().toString());


                                } else if (fieldType.equals("TextComment")) {
                                    if (D)
                                        Log.d(TAG, i + "fieldType at pos editTexts[i] :" + editTexts[i]);

                                    if (editTexts[i].getText().toString().isEmpty()) {
                                        Util.showToastmessage(mContext, "Please enter " + fieldLabelName);
                                        noEmptyField = false;
                                        break;
                                    }
                                    hashMapData.put(fieldLabelName, editTexts[i].getText().toString());

                                } else if (fieldType.equals("MultipleChoiceMore")) {
                                    //fieldValues
                                    ArrayList field_Value = fieldModels[j].getFieldValues();
                                    if (D)
                                        Log.d(TAG, " fieldModels[j]  field_Values=>" + field_Value);
                                    boolean ischkBox = false;
                                    ArrayList<String> OptionSelected = new ArrayList<String>();

                                    for (int k = 0; k < field_Value.size(); k++) {
                                        if (checkBoxes[k].isChecked()) {
                                            ischkBox = true;
                                            OptionSelected.add(checkBoxes[k].getText().toString());

                                        }
                                    }
                                    if (D)
                                        Log.d(TAG, " fieldModels[j]  MultipleChoiceMore ischkBox=>" + ischkBox);

                                    if (!ischkBox) {
                                        Util.showToastmessage(mContext, "Please select at least one " + fieldLabelName);
                                        noEmptyField = false;
                                        break;
                                    }
                                    hashMapData.put(fieldLabelName, OptionSelected);


                                } else if (fieldType.equals("MultipleChoiceSingle")) {
                                    //fieldValues
                                    ArrayList field_Value = fieldModels[j].getFieldValues();
                                    if (D)
                                        Log.d(TAG, " fieldModels[j] MultipleChoiceSingle field_Values=>" + field_Value);




                                   /* radioGroups[i].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            // Toast.makeText(mContext,"i=>"+i,Toast.LENGTH_LONG).show();
                                            RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                                            if (null != rb && i > -1) {
                                                selectedRB = rb.getText().toString();
                                                if (D)
                                                    Toast.makeText(mContext, fieldLabelName + "-is...>" + rb.getText(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
*/
                                    if(radioGroups[i].getCheckedRadioButtonId()==-1){
                                        Util.showToastmessage(mContext, "Please select any one " + fieldLabelName);
                                        noEmptyField = false;
                                        break;
                                    }else
                                    {
                                        for (int k = 0; k < field_Value.size(); k++) {

                                            // get selected radio button from radioGroup
                                        int selectedId = radioGroups[i].getCheckedRadioButtonId();
                                        // find the radiobutton by returned id
                                            RadioButton selectedRadioButton = (RadioButton)rootView.findViewById(selectedId);
                                            String selectedRB=selectedRadioButton.getText().toString();
                                                    hashMapData.put(fieldLabelName, selectedRB);


                                            if(D)
                                        Log.d(TAG,fieldLabelName+" .."+selectedRB+" is selected");}
                                    }



                                } else if (fieldType.equals("DropDown")) { //dropdown value
                                    hashMapData.put(fieldLabelName, spinners[i].getSelectedItem().toString());
                                }

                            }

                        } //end of inner for-loop

                        if (!noEmptyField) {
                            if (D)
                                Log.d(TAG, "  :breaking inner loop  noEmptyField==>" + noEmptyField);

                            break;
                        }

                    }//end of outer for loop
                } catch (Exception e) {
                    if (D)
                        Log.d(TAG, "Exception in register info:" + e);
                }

                if (D)
                    Log.d(TAG, noEmptyField + " noEmptyField :hashMapData ==>" + hashMapData);

                if (noEmptyField) {
                    /*progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "data successfully submitted", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            scrollViewChildContainer.setVisibility(View.GONE);
                            Register_btn.setVisibility(View.GONE);

                        }
                    }, 2000);
*/
                      registerChild();

                }

            }
        });
        return rootView;
    }

    private void registerChild(){
        progressBar.setVisibility(View.VISIBLE);
        scrollViewChildContainer.setVisibility(View.GONE);
        Register_btn.setVisibility(View.GONE);
        handle="GPA";

        Util util=(Util)mContext.getApplicationContext();
       // hashMapData.put("mother_tongue",code.trim());
        //1. End the session
       if(D)
            Log.d(TAG,"hashMapData------>"+hashMapData);

        partner=util.getPartner();
        endSessionResponseHandler=new EndSessionResponseHandler(DisplayChildProfileFragment.this);
        partner.terminatePartnerSession(configModel.getPartnerId(), endSessionResponseHandler);
        if(D)
            Log.d(TAG,"hashMapData------>"+hashMapData);
    }


    private void displayToastMsg(String msg){
       Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
    }

    int editRow=0;
    int lastSpecialRequestsCursorPosition;
    String specialRequests;
    int dateRow = 0,SectionField=0,SectionfieldRow;
    DatePickerDialog.OnDateSetListener     mDatePickerListeners[]=new DatePickerDialog.OnDateSetListener[100];
    private void createChildForm(){
        try {
             configModel=new Util(mContext).getConfigModel();
            int sectionSize=configModel.getSectionValues().length;
             if (D)
                Log.d(TAG, "sectionSize :" + sectionSize);
            String partnerName=configModel.getPartnerName();
            ((MainActivity) mContext).showTitle(partnerName, mFragment);
            // final ValidationField[] validationField=new ValidationField[fieldSize];
           final SectionValue[] sectionValues=configModel.getSectionValues();
            //InstructionsField[] instructionsFields=sectionValues.ge;

            //Layout inflater
            View view;
            InstructionsField[] instructionsFields=new InstructionsField[sectionSize];

            int i=0;
            for (int pos=0;pos<sectionSize;pos++){
                //sectionValues[i]=new SectionValue();

                if(D)
                    Log.d(TAG,"iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii==>"+pos);
                if(D)
                    Log.d(TAG," sectionValues[i]==>"+sectionValues[pos]);



               final FieldModel[] fieldModels=sectionValues[pos].getFieldModels();
                int length=fieldModels.length;
                if(D)
                    Log.d(TAG,"sectionValues length==>"+length);

                //1st position fixed as sectionHeadings
                String heading=sectionValues[pos].getSectionHeading();
                if (D)
                    Log.d(TAG, " getSectionHeading==>" +heading );

                if(!heading.isEmpty()){
                    view=layoutInflater.inflate(R.layout.textview_heading_template,displayLayout,false);
                    textViewHeading[pos] = (CustomTextView)view.findViewById(R.id.textHeadingTemplate);
                    textViewHeading[pos].setText(heading);
                    displayLayout.addView(textViewHeading[pos]);}

                //Instructions
               /* String instruction=sectionValues[pos].getInstructionsField().getTitle();
                if (D)
                    Log.d(TAG, " instruction==>" +instruction );

                if(!instruction.isEmpty()){
                    view=layoutInflater.inflate(R.layout.textview_instructions_template,displayLayout,false);
                    textViewInstructions[pos] = (CustomTextView)view.findViewById(R.id.textInstructionsTemplate);
                    textViewInstructions[pos].setText(instruction);
                    displayLayout.addView(textViewInstructions[pos]);}
                if (D)
                    Log.d(TAG, "getInstructionsField= getDisplayOrder=>" + sectionValues[pos].getInstructionsField().getDisplayOrder());
*/

                for (int j=0;j<length;j++){
                    i++;

                    // fieldModels[j]=new FieldModel();
                    if(D)
                        Log.d(TAG,"--------jjjjjj------------==>"+j);

                    if(D)
                        Log.d(TAG,"fieldModels[j]  partnerFields==>"+ fieldModels[j]);


                    try {
                               /* if (D)
                                    Log.d(TAG, "fieldModels[j]  getFieldName==>" + fieldModels[j].getFieldName());
                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getFieldHint==>" + fieldModels[j].getFieldHint());
                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getDisplayOrder==>" + fieldModels[j].getDisplayOrder());

                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getFieldType==>" + fieldModels[j].getFieldValue().getFieldType());

                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getFieldInputType==>" + fieldModels[j].getFieldValue().getFieldInputType());
                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getValidation isFlag==>" + fieldModels[j].getFieldValue().getValidation().isFlag());
                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getValidation getMinimum==>" + fieldModels[j].getFieldValue().getValidation().getMinimum());
                                if (D)
                                    Log.d(TAG, " fieldModels[j]  getValidation getMaximum==>" + fieldModels[j].getFieldValue().getValidation().getMaximum());
*/


                        //-------------------field displaying
                        final String fieldLabelName=fieldModels[j].getFieldName();
                        String fieldType ="";
                        String fieldInputType = "";

                        // int displayOrder = fieldModels[j].getDisplayOrder();
                        if(fieldLabelName.equalsIgnoreCase("instructions")){
                            String instruction=fieldModels[j].getFieldHint();
                            if(!instruction.isEmpty()){
                                view=layoutInflater.inflate(R.layout.textview_instructions_template,displayLayout,false);
                                textViewInstructions[pos] = (CustomTextView)view.findViewById(R.id.textInstructionsTemplate);
                                textViewInstructions[pos].setText(instruction);
                                displayLayout.addView(textViewInstructions[pos]);}

                        }else{
                            if (D)
                                Log.d(TAG, "fieldType at pos CreateForm :" + i);

                            fieldType =fieldModels[j].getFieldValue().getFieldType();
                            fieldInputType = fieldModels[j].getFieldValue().getFieldInputType();
                            //Heading as Label
                            view=layoutInflater.inflate(R.layout.textview_template,displayLayout,false);
                            textViews[i] = (CustomTextView)view.findViewById(R.id.textTemplate);
                            if(fieldModels[j].getFieldValue().getValidation().isFlag())
                                textViews[i].setText(fieldLabelName + "(*)");
                            else
                                textViews[i].setText(fieldLabelName);

                            displayLayout.addView(textViews[i]);

                            //Label value
                            if(fieldInputType.equals("date")){
                                if(D)
                                    Log.d(TAG,fieldLabelName+"==>fieldLabelName textViewData date row==>"+i);

                                final int row=i;
                                final int sectionPos=pos;
                                final int fieldRow=j;


                                view=layoutInflater.inflate(R.layout.textview_dob_template,displayLayout,false);
                                textViewData[i] =(CustomTextView)view.findViewById(R.id.textdobTemplate);
                                textViewData[i].setHint("Select " + fieldLabelName);
                                displayLayout.addView(textViewData[i]);

                                Calendar c = Calendar.getInstance();
                                year  = c.get(Calendar.YEAR);
                                month = c.get(Calendar.MONTH);
                                day   = c.get(Calendar.DAY_OF_MONTH);
                                mDatePickerListeners[i]=new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker dp, int selectedYear, int monthOfYear,
                                                          int dayOfMonth) {
                                        year = selectedYear;
                                        month = monthOfYear;
                                        day = dayOfMonth;
                                        String strDay;
                                        String strMonth;
                                        if(day<10){
                                            strDay="0"+day;
                                        }
                                        else{
                                            strDay=""+day;
                                        }

                                        if((month+1)<10){
                                            strMonth="0"+(month+1);
                                        }
                                        else{
                                            strMonth=""+(month+1);
                                        }
                                        String date=year+"-"+ strMonth + "-"+strDay;
                                        if(D)
                                            Log.d(TAG,"date===>"+date+" dateRow=>"+dateRow+" SectionfieldRow==>"+SectionfieldRow+" sectionPos:"+SectionField);

                                        int requiredAge= Age.getChildAge(date);
                                        String fieldName=sectionValues[SectionField].getFieldModels()[SectionfieldRow].getFieldName();
                                        int minmunYear=sectionValues[SectionField].getFieldModels()[SectionfieldRow].getFieldValue().getValidation().getMinimum();
                                        int maximunYear=sectionValues[SectionField].getFieldModels()[SectionfieldRow].getFieldValue().getValidation().getMaximum();
                                        if(D)
                                            Log.d(TAG,fieldName+" --fieldName>minmunYear:"+minmunYear+", maximunYear"+maximunYear);


                                        if(requiredAge<minmunYear)
                                            displayToastMsg(fieldName+" should be minumum "+minmunYear+" years");
                                        else if(requiredAge>maximunYear)
                                            displayToastMsg(fieldName+" can't be greater than "+maximunYear+" years");
                                        else{ //set date
                                            mSelectedDob=date;
                                            textViewData[dateRow].setText(date);
                                            // mTxt_dob.setTextColor(getResources().getColor(android.R.color.black));

                                            // scrolling a particular view in ScrollView
                                            new Handler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    scrollViewChildContainer.smoothScrollTo(0, textViewData[dateRow].getTop());
                                                    textViews[dateRow].requestFocus();
                                                    if (D)
                                                        Log.d(TAG, "scrollViewChildContainer===>");


                                                }
                                            });


                                        }//end of set date




                                    }
                                };


                                mDatePickerDialog=new DatePickerDialog(getActivity(), mDatePickerListeners[i], year, month, day);

                                textViewData[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(D)
                                            Log.d(TAG,sectionPos+"-->sectionPos,  textViewData row==>"+row+" ,fieldRow:"+fieldRow);
                                        dateRow=row;
                                        SectionField=sectionPos;
                                        SectionfieldRow=fieldRow;
                                        mDatePickerDialog.show();
                                    }
                                });


                                textViewData[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(D)
                                            Log.d(TAG,"textViewData[i].setOnFocusChangeListener= row=>"+row);

                                        Util.hideKeyboard(getActivity(),mContext);
                                    }
                                });

                            }else if (fieldType.equals("Text")) { // label Value
                                if(D)
                                    Log.d(TAG,i+"==>editTexts at pos & fieldInputType :"+fieldInputType);

                                if(D)
                                    Log.d(TAG,"fieldInputType:"+fieldInputType);
                                view=layoutInflater.inflate(R.layout.edit_text_template,displayLayout,false);

                                editTexts[i] =(CustomEditText)view.findViewById(R.id.editTextTemplate);
                                editTexts[i].setHint("Please enter your " + fieldLabelName);
                                if(fieldInputType.equals("phone"))
                                    editTexts[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                                else if(fieldInputType.equals("textEmailAddress"))
                                    editTexts[i].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                else if(fieldInputType.equals("textPassword")){
                                    editTexts[i].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    editTexts[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
                                }
                                displayLayout.addView(editTexts[i]);

                                /*final int fieldPos=j;
                                final int editRows=i;
                                editTexts[i].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        //lastSpecialRequestsCursorPosition = editTexts[editRows].getSelectionStart();

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        String charstr = editTexts[editRows].getText().toString();
                                        int maxvalue=fieldModels[fieldPos].getFieldValue().getValidation().getMaximum();
                                        int minvalue=fieldModels[fieldPos].getFieldValue().getValidation().getMinimum();

                                        if(D)
                                            Log.d(TAG,editRows+" editRows...afterTextChanged charCount---->"+charstr);
                                        int charCount=charstr.length();

                                        // editTexts[editRow].removeTextChangedListener(this);
                                        if(D)
                                            Log.d(TAG,"afterTextChanged charCount---->"+charCount);
                                        if(!editTexts[editRows].hasFocus()){
                                            if (charCount<minvalue-1) { // Validation
                                                Toast.makeText(mContext,fieldLabelName+" can't be less than "+minvalue+" character.",Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        if (charCount > maxvalue-1) { // Validation
                                            Toast.makeText(mContext,fieldLabelName+" can't be more than "+maxvalue+" character.",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
*/
                                /*/*//* editTexts[i].requestFocus();
                                    editTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            int charCount = editTexts[editRow].getText().toString().length();

                                            if (D)
                                                Log.d(TAG, "editTexts[i].setOnFocusChangeListener==>"+editRow);
                                            if (charCount > fieldModels[fieldPos].getFieldValue().getValidation().getMaximum()) { // Validation
                                                if(D)
                                                    Log.d(TAG,"inside setOnFocusChangeListener charCount---->"+charCount);
                                                Util.hideKeyboard(getActivity(), mContext);
                                            }

                                           // Util.showKeyboard(getActivity(), mContext);
                                        }
                                    });
                                editTexts[i].setOnKeyListener(new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        int charCount = editTexts[editRow].getText().toString().length();

                                        if (D)
                                            Log.d(TAG, "editTexts[i].setOnKeyListener==>");
                                        if (charCount > fieldModels[fieldPos].getFieldValue().getValidation().getMaximum()) { // Validation
                                            if(D)
                                                Log.d(TAG,"inside setOnKeyListener charCount---->"+charCount);
                                            Util.hideKeyboard(getActivity(), mContext);
                                        }

                                        return false;
                                    }
                                });
*/
                                    /*//* //Working but for last edittext showing keyboard
*/
                            }
                            else if (fieldType.equals("TextComment")) { // label Value type="comment";
                                if(D)
                                    Log.d(TAG,i+"==>editTexts at pos & fieldInputType :"+fieldInputType);

                                view=layoutInflater.inflate(R.layout.comment_template,displayLayout,false);
                                editTexts[i] =(CustomEditText)view.findViewById(R.id.commentTemplate);
                                editTexts[i].setHint("Please enter your " + fieldLabelName);
                                displayLayout.addView(editTexts[i]);
                                editRow=i;
                                final int fieldPos=j;
                                editTexts[i].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        lastSpecialRequestsCursorPosition = editTexts[editRow].getSelectionStart();

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        int lineCount = editTexts[editRow].getLineCount();
                                        editTexts[editRow].removeTextChangedListener(this);
                                        if(D)
                                            Log.d(TAG,editRow+" ==>editRow....lineCount---->"+lineCount);

                                        if (lineCount > fieldModels[fieldPos].getFieldValue().getValidation().getMaximum()) { // Validation
                                                if(D)
                                                    Log.d(TAG,"inside lineCount---->"+lineCount);
                                                editTexts[editRow].setText(specialRequests);
                                                editTexts[editRow].setSelection(lastSpecialRequestsCursorPosition);
                                                Util.hideKeyboard(getActivity(), mContext);
                                            }
                                            else
                                                specialRequests = editTexts[editRow].getText().toString();

                                        editTexts[editRow].addTextChangedListener(this);
                                    }
                                });
                            }else if (fieldType.equals("MultipleChoiceMore")) { //dropdown value MultipleChoice {"Single-select(radio-buttons)","Multi-select(checkboxes)"}
                                // ArrayList<String> Optionlist =configModel.getFieldModels()[i].getFieldValues();// configModel.getFieldModels()[i].getFieldValues();

                                //fieldValues
                                ArrayList field_Value = fieldModels[j].getFieldValues();


                                if (D)
                                    Log.d(TAG, " fieldModels[j]  field_Values=>" + field_Value);

                                for(int k=0;k<field_Value.size();k++){
                                    view=layoutInflater.inflate(R.layout.checkbox_text_template,displayLayout,false);
                                    checkBoxes[k] =(CheckBox)view.findViewById(R.id.checkBoxTemplate);
                                    checkBoxes[k].setText(""+field_Value.get(k));
                                    displayLayout.addView(checkBoxes[k]);

                                }


                            } else if (fieldType.equals("MultipleChoiceSingle")) {
                                //fieldValues
                                ArrayList<String> field_Value = fieldModels[j].getFieldValues();


                                if (D)
                                    Log.d(TAG, " fieldModels[j]  field_Values=>" + field_Value);
                                view=layoutInflater.inflate(R.layout.radiogroup_template,displayLayout,false);
                                radioGroups[i] =(RadioGroup)view.findViewById(R.id.radioGroupTemplate);

                                for(int k=0;k<field_Value.size();k++){
                                    view=layoutInflater.inflate(R.layout.radio_text_template,displayLayout,false);

                                    radioButtons[k] =(RadioButton)view.findViewById(R.id.radioTemplate);
                                    radioButtons[k].setText(field_Value.get(k));
                                    radioButtons[k].setId(k);
                                   /* if (k==0)
                                        radioButtons[k].setChecked(true);
                                   */ radioGroups[i].addView(radioButtons[k]);

                                }

                                displayLayout.addView(radioGroups[i]);



                            }

                            else if (fieldType.equals("DropDown")) { //dropdown value
                                view=layoutInflater.inflate(R.layout.spinner_template,displayLayout,false);
                                spinners[i] =(Spinner)view.findViewById(R.id.spinnerTemplate);
                                //fieldValues
                                ArrayList field_Value = fieldModels[j].getFieldValues();


                                if (D)
                                    Log.d(TAG, " fieldModels[j]  field_Values=>" + field_Value);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, field_Value);
                                spinners[i].setAdapter(adapter);
                                adapter.setDropDownViewResource(R.layout.spinner_popup_item);
                                spinners[i].setAdapter(adapter);
                                displayLayout.addView(spinners[i]);

                            }
                            //--------end of field display


                        }//end of field display



                    }catch (Exception e){
                        if(D)
                            Log.e(TAG,"ex in fieldModels[j]:"+e);
                    }


                    //end of else




                }//end of inner for-loop

                if(D)
                    Log.d(TAG," =========end fields of section======>");







            }//end of outer for-loop

              /*  String fieldLabelName = configModel.getSectionValues()[i].getFieldModels()[i].getFieldName();
                if (D)
                    Log.d(TAG, "fieldLabelName" + fieldLabelName);
                String fieldType =configModel.getFieldModels()[i].getFieldType();
                String fieldInputType = configModel.getFieldModels()[i].getFieldInputType();
                int displayOrder = configModel.getFieldModels()[i].getDisplayOrder();

                validationField[i]= configModel.getFieldModels()[i].getValidation();
                if (D)
                    Log.d(TAG, "fieldType" + fieldType);
                //Heading as Label
                view=layoutInflater.inflate(R.layout.textview_template,displayLayout,false);
                textViews[i] = (CustomTextView)view.findViewById(R.id.textTemplate);
                if(validationField[i].isFlag())
                textViews[i].setText(fieldLabelName + "(*)");
                else
                textViews[i].setText(fieldLabelName);

                displayLayout.addView(textViews[i]);
                //Label value
                 if(fieldInputType.equals("date")){
                     final int row=i;
                     view=layoutInflater.inflate(R.layout.textview_dob_template,displayLayout,false);
                     textViewData[i] =(CustomTextView)view.findViewById(R.id.textdobTemplate);
                     textViewData[i].setHint("Select " + fieldLabelName);
                     displayLayout.addView(textViewData[i]);

                     Calendar c = Calendar.getInstance();
                     year  = c.get(Calendar.YEAR);
                     month = c.get(Calendar.MONTH);
                     day   = c.get(Calendar.DAY_OF_MONTH);
                         mDatePickerListeners[i]=new DatePickerDialog.OnDateSetListener() {
                         @Override
                         public void onDateSet(DatePicker dp, int selectedYear, int monthOfYear,
                                               int dayOfMonth) {
                             year = selectedYear;
                             month = monthOfYear;
                             day = dayOfMonth;
                             String strDay;
                             String strMonth;
                             if(day<10){
                                 strDay="0"+day;
                             }
                             else{
                                 strDay=""+day;
                             }

                             if((month+1)<10){
                                 strMonth="0"+(month+1);
                             }
                             else{
                                 strMonth=""+(month+1);
                             }
                             String date=year+"-"+ strMonth + "-"+strDay;
                             mSelectedDob=date;
                             if(D)
                                 Log.d(TAG,"date===>"+date+" dateRow=>"+dateRow);
                             textViewData[dateRow].setText(date);
                             // mTxt_dob.setTextColor(getResources().getColor(android.R.color.black));

                           // scrolling a particular view in ScrollView
                             new Handler().post(new Runnable() {
                                 @Override
                                 public void run() {
                                     scrollViewChildContainer.smoothScrollTo(0, textViewData[dateRow].getTop());
                                     textViews[dateRow].requestFocus();
                                     if (D)
                                         Log.d(TAG, "scrollViewChildContainer===>");


                                 }
                             });



                         }
                     };


                     mDatePickerDialog=new DatePickerDialog(getActivity(), mDatePickerListeners[i], year, month, day);

                     textViewData[i].setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             if(D)
                                 Log.d(TAG,"textViewData row==>"+row);
                                dateRow=row;
                             mDatePickerDialog.show();
                         }
                     });


                     textViewData[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                         @Override
                         public void onFocusChange(View view, boolean b) {
                             if(D)
                                 Log.d(TAG,"textViewData[i].setOnFocusChangeListener==>");

                             Util.hideKeyboard(getActivity(),mContext);
                         }
                     });

                 }else if (fieldType.equals("Text")) { // label Value
                     if(D)
                         Log.d(TAG,"fieldInputType:"+fieldInputType);
                      view=layoutInflater.inflate(R.layout.edit_text_template,displayLayout,false);

                    editTexts[i] =(CustomEditText)view.findViewById(R.id.editTextTemplate);
                    editTexts[i].setHint("Please enter your " + fieldLabelName);
                     if(fieldInputType.equals("number"))
                         editTexts[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                     else if(fieldInputType.equals("textEmailAddress"))
                         editTexts[i].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                     else if(fieldInputType.equals("textPassword")){
                         editTexts[i].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                         editTexts[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
                     }
                     displayLayout.addView(editTexts[i]);

                    *//* editTexts[i].requestFocus();
                     editTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                         @Override
                         public void onFocusChange(View view, boolean b) {
                             if (D)
                                 Log.d(TAG, "editTexts[i].setOnFocusChangeListener==>");

                             Util.showKeyboard(getActivity(), mContext);
                         }
                     });*//* //Working but for last edittext showing keyboard

                }
                else if (fieldType.equals("TextComment")) { // label Value type="comment";

                    view=layoutInflater.inflate(R.layout.comment_template,displayLayout,false);
                    editTexts[i] =(CustomEditText)view.findViewById(R.id.commentTemplate);
                    editTexts[i].setHint("Please enter your " + fieldLabelName);
                    displayLayout.addView(editTexts[i]);
                   editRow=i;
                    editTexts[i].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            lastSpecialRequestsCursorPosition = editTexts[editRow].getSelectionStart();

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            int lineCount = editTexts[editRow].getLineCount();
                            editTexts[editRow].removeTextChangedListener(this);

                            if (lineCount > validationField[editRow].getMaximum()) { // Validation
                                if(D)
                                    Log.d(TAG,"lineCount---->"+lineCount);
                                editTexts[editRow].setText(specialRequests);
                                editTexts[editRow].setSelection(lastSpecialRequestsCursorPosition);
                               Util.hideKeyboard(getActivity(), mContext);
                            }
                            else
                                specialRequests = editTexts[editRow].getText().toString();

                            editTexts[editRow].addTextChangedListener(this);
                        }
                    });
                }else if (fieldType.equals("MultipleChoiceMore")) { //dropdown value MultipleChoice {"Single-select(radio-buttons)","Multi-select(checkboxes)"}
                    ArrayList<String> Optionlist =configModel.getFieldModels()[i].getFieldValues();// configModel.getFieldModels()[i].getFieldValues();
                    for(int j=0;j<Optionlist.size();j++){
                        view=layoutInflater.inflate(R.layout.checkbox_text_template,displayLayout,false);
                        checkBoxes[j] =(CheckBox)view.findViewById(R.id.checkBoxTemplate);
                        checkBoxes[j].setText(Optionlist.get(j));
                        displayLayout.addView(checkBoxes[j]);

                    }


                } else if (fieldType.equals("MultipleChoiceSingle")) {
                    ArrayList<String> Optionlist = configModel.getFieldModels()[i].getFieldValues();
                    view=layoutInflater.inflate(R.layout.radiogroup_template,displayLayout,false);
                     radioGroups[i] =(RadioGroup)view.findViewById(R.id.radioGroupTemplate);
                       for(int j=0;j<Optionlist.size();j++){
                        view=layoutInflater.inflate(R.layout.radio_text_template,displayLayout,false);
                        radioButtons[j] =(RadioButton)view.findViewById(R.id.radioTemplate);
                        radioButtons[j].setText(Optionlist.get(j));
                        radioButtons[j].setId(j);
                        if (j==0)
                            radioButtons[j].setChecked(true);
                        radioGroups[i].addView(radioButtons[j]);

                    }

                    displayLayout.addView(radioGroups[i]);



                }

                else if (fieldType.equals("DropDown")) { //dropdown value
                    view=layoutInflater.inflate(R.layout.spinner_template,displayLayout,false);
                     spinners[i] =(Spinner)view.findViewById(R.id.spinnerTemplate);
                    ArrayList dropdownlist = configModel.getFieldModels()[i].getFieldValues();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, dropdownlist);
                    spinners[i].setAdapter(adapter);
                    adapter.setDropDownViewResource(R.layout.spinner_popup_item);
                    spinners[i].setAdapter(adapter);
                    displayLayout.addView(spinners[i]);

                }
*/


            //if(displayOrder==1){
           /*     editTexts[0].requestFocus();
            if (D)
                Log.d(TAG, "requestFocus at 0==>");

            Util.showKeyboard(getActivity(), mContext);
           */     /*editTexts[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (D)
                            Log.d(TAG, "editTexts[i].setOnFocusChangeListener==>");

                        Util.showKeyboard(getActivity(), mContext);
                    }
                });*/
           // }



        }catch (Exception e){
            if(D)
                Log.e(TAG,"createChildForm Ex :"+e);

        }

    }



    @Override
    public void onSuccessEndSession(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onSuccessEndSession :"+result);
        //2. Create Profile
        profile=new Profile(Util.handle,Util.avatar,"en");
        //userProfile=new UserProfile(getActivity());
        Util util=(Util)mContext.getApplicationContext();
        userProfile=util.getUserProfile();
        userProfileResponseHandler=new UserProfileResponseHandler(DisplayChildProfileFragment.this);
        userProfile.createUserProfile(profile, userProfileResponseHandler);


    }

    @Override
    public void onFailureEndSession(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailureEndSession :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);
        Register_btn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccessSession(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onSuccessSession :"+result+"  UID==>"+UID);
        //6. send partner data to Genie services
        partnerDataResponseHandler=new PartnerDataResponseHandler(this);
         hashMapData.put(Util.UID,UID);
        if(D)
            Log.d(TAG,"partnerData==>"+hashMapData);

        partner.sendData(configModel.getPartnerId(), hashMapData, partnerDataResponseHandler);

    }

    @Override
    public void onFailureSession(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailureSession :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);
        Register_btn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccessPartner(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());

        if(D)
            Log.d(TAG, "onSuccessPartner :" + result);
        progressBar.setVisibility(View.GONE);
        //7 end telemetry
        Util util=(Util)mContext.getApplicationContext();
        telemetry=util.getTelemetry();
        telemetryResponseHandler=new TelemetryResponseHandler(DisplayChildProfileFragment.this);
        telemetry.send(TelemetryEventGenertor.generateOEEndEvent(mContext, util.getStartTime(), System.currentTimeMillis()).toString(), telemetryResponseHandler);



    }

    @Override
    public void onFailurePartner(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailurePartner :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);
        Register_btn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccessUserProfile(GenieResponse genieResponse) {
        String json=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onSuccessUserProfile json :"+json);
        Type type = new TypeToken<HashMap<String,String>>(){}.getType();
        HashMap<String,String> hashMap=new Gson().fromJson(json,type);

        //3. setCurrentUser
        currentuserSetResponseHandler=new CurrentuserResponseHandler(this);
        UID=hashMap.get("uid");
        if(D)
            Log.d(TAG,"onSuccessUserProfile profile.getUid() :"+UID);
        userProfile.setCurrentUser(UID, currentuserSetResponseHandler);

    }

    @Override
    public void onFailureUserprofile(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailureUserprofile :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccessCurrentUser(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onSuccessCurrentUser :"+result);
        //4. getCurrentUser
        currentGetuserResponseHandler=new CurrentGetuserResponseHandler(this);
        userProfile.getCurrentUser(currentGetuserResponseHandler);

    }

    @Override
    public void onFailureCurrentUser(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailureCurrentUser :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccessCurrentGetUser(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onSuccessCurrentGetUser :"+result);
        //5. start session
        //partner=new Partner(getActivity());
        startSessionResponseHandler=new StartSessionResponseHandler(this);
        partner.startPartnerSession(configModel.getPartnerId(),startSessionResponseHandler);
    }

    @Override
    public void onFailureCurrentGetUser(GenieResponse genieResponse) {
        String result=new Gson().toJson(genieResponse.getResult());
        if(D)
            Log.d(TAG,"onFailureCurrentGetUser :"+result);
        progressBar.setVisibility(View.GONE);
        scrollViewChildContainer.setVisibility(View.VISIBLE);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        if(D)
            Log.d(TAG,"onDetach");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(D)
            Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(D)
            Log.d(TAG, "onDestroy partner--->" + partner);


    }

    @Override
    public void onStop() {
        super.onStop();
        if(D)
            Log.d(TAG, "onStop :userProfile->" + userProfile + " partner--> " + partner);
    }

    @Override
    public void onSuccessTelemetry(GenieResponse genieResponse) {
        if(D)
            Log.d(TAG,"-------------------------------------------------------------------onSuccessTelemetry");

        Util.processSuccess(mContext, genieResponse);
        //8 exit the app
        ((MainActivity)mContext).exitApp();

    }

    @Override
    public void onFailureTelemetry(GenieResponse genieResponse) {
        if(D)
            Log.d(TAG,"-------------------------------------------------------------------onFailureTelemetry");

        Util.processSendFailure(mContext,genieResponse);
    }
}
