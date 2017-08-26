package com.aabello.iwalk.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.User;
import com.aabello.iwalk.model.types.HeightUnitType;
import com.aabello.iwalk.model.types.WeightUnitType;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.UserDao;
import com.aabello.iwalk.model.types.GenderType;

import org.greenrobot.greendao.query.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataActivity extends AppCompatActivity {

    @BindView(R.id.ageEditText) EditText ageEditText;
    @BindView(R.id.genderRadioGroup) RadioGroup genderRadioGroup;
    @BindView(R.id.heightRadioGroup) RadioGroup heightRadioGroup;
    @BindView(R.id.weightRadioGroup) RadioGroup weightRadioGroup;
    @BindView(R.id.ftEditText) EditText ftEditText;
    @BindView(R.id.cmEditText) EditText cmEditText;
    @BindView(R.id.stEditText) EditText stEditText;
    @BindView(R.id.kgEditText) EditText kgEditText;
    @BindView(R.id.smokingHabitSpinner) Spinner smokingHabitSpinner;
    @BindView(R.id.saveButton) Button saveButton;

    private UserDao mUserDao;
    private Query<User> mUserQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);

        enableSelectedPreferredHeightLabel();
        enableSelectedPreferredWeightLabel();

        populateSmokingHabitSpinner();

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        mUserDao = daoSession.getUserDao();
        mUserQuery = mUserDao.queryBuilder().build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mUserQuery.list().isEmpty()) {
            setFields();
        }
    }

    private void populateSmokingHabitSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.smoking_habit_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        smokingHabitSpinner.setAdapter(adapter);


    }

    private void validateFields() {
        if(ageEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "PLEASE INSERT AGE", Toast.LENGTH_LONG).show();
            return;
        }

        if(genderRadioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "PLEASE SELECT GENDER", Toast.LENGTH_LONG).show();
            return;
        }

        if(heightRadioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "PLEASE SELECT PREFERRED HEIGHT", Toast.LENGTH_LONG).show();
            return;
        }

        if(heightRadioGroup.getCheckedRadioButtonId() == R.id.ftRadioButton && ftEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "PLEASE INPUT HEIGHT IN FT FIELD", Toast.LENGTH_LONG).show();
            return;
        }

        if(heightRadioGroup.getCheckedRadioButtonId() == R.id.cmRadioButton && cmEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "PLEASE INPUT HEIGHT IN CM FIELD", Toast.LENGTH_LONG).show();
            return;
        }
        if(weightRadioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "PLEASE SELECT PREFERRED WEIGHT", Toast.LENGTH_LONG).show();
            return;
        }

        if(weightRadioGroup.getCheckedRadioButtonId() == R.id.stRadioButton && stEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "PLEASE INPUT WEIGHT IN LB FIELD", Toast.LENGTH_LONG).show();
            return;
        }

        if(weightRadioGroup.getCheckedRadioButtonId() == R.id.kgRadioButton && kgEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "PLEASE INPUT WEIGHT IN KG FIELD", Toast.LENGTH_LONG).show();
        }

        if(smokingHabitSpinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "PLEASE SELECT SMOKING HABIT", Toast.LENGTH_LONG).show();
            return;
        }

        if(mUserQuery.list().size() == 0){
            addUserData();
        }else {
            updateUserData();
        }

    }

    @Override
    public void onBackPressed() {
        if(mUserQuery.list().size() == 0) {
            new AlertDialog.Builder(this).setCancelable(false)
                    .setMessage("You need to put all input all required fields " +
                            "before you will be allowed to proceed")
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setPositiveButton("CONTINUE", null).show().setCanceledOnTouchOutside(false);
        }else{
            loadMainActivity();
        }
    }

    private void enableSelectedPreferredHeightLabel() {
        heightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.ftRadioButton){
                    ftEditText.setEnabled(true);
                    ftEditText.requestFocus();
                    cmEditText.setEnabled(false);
                    cmEditText.setText("");
                }

                if(checkedId == R.id.cmRadioButton){
                    cmEditText.setEnabled(true);
                    cmEditText.requestFocus();
                    ftEditText.setEnabled(false);
                    ftEditText.setText("");
                }
            }
        });
    }

    private void enableSelectedPreferredWeightLabel() {
        weightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.stRadioButton){
                    stEditText.setEnabled(true);
                    stEditText.requestFocus();
                    kgEditText.setEnabled(false);
                    kgEditText.setText("");
                }

                if(checkedId == R.id.kgRadioButton){
                    kgEditText.setEnabled(true);
                    kgEditText.requestFocus();
                    stEditText.setEnabled(false);
                    stEditText.setText("");
                }
            }
        });
    }

    @OnClick (R.id.saveButton)
    public void saveUserData(View view){
        validateFields();
    }

    private void updateUserData() {
        User user = mUserQuery.list().get(0);

        int age = Integer.parseInt(ageEditText.getText().toString());
        GenderType genderType = genderRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton ? GenderType.M : GenderType.F;

        HeightUnitType heightUnitType;
        double height;

        if(heightRadioGroup.getCheckedRadioButtonId() == R.id.ftRadioButton) {
            heightUnitType = HeightUnitType.FT;
            height = Double.parseDouble(ftEditText.getText().toString());
        }else{
            heightUnitType = HeightUnitType.CM;
            height = Double.parseDouble(cmEditText.getText().toString());
        }

        WeightUnitType weightUnitType;
        double weight;
        if(weightRadioGroup.getCheckedRadioButtonId() == R.id.stRadioButton){
            weightUnitType = WeightUnitType.ST;
            weight = Double.parseDouble(stEditText.getText().toString());
        }else{
            weightUnitType = WeightUnitType.KG;
            weight = Double.parseDouble(kgEditText.getText().toString());
        }

        String smokingHabit = smokingHabitSpinner.getSelectedItem().toString();

        user.setAge(age);
        user.setGenderType(genderType);
        user.setHeightUnitType(heightUnitType);
        user.setHeight(height);
        user.setWeightUnitType(weightUnitType);
        user.setWeight(weight);
        user.setSmokingHabit(smokingHabit);

        mUserDao.update(user);
        loadMainActivity();
    }

    private void addUserData() {
        int age = Integer.parseInt(ageEditText.getText().toString());
        GenderType genderType = genderRadioGroup.getCheckedRadioButtonId() ==
                R.id.maleRadioButton ? GenderType.M : GenderType.F;

        HeightUnitType heightUnitType;
        double height;

        if(heightRadioGroup.getCheckedRadioButtonId() == R.id.ftRadioButton) {
            heightUnitType = HeightUnitType.FT;
            height = Double.parseDouble(ftEditText.getText().toString());
        }else{
            heightUnitType = HeightUnitType.CM;
            height = Double.parseDouble(cmEditText.getText().toString());
        }

        WeightUnitType weightUnitType;
        double weight;
        if(weightRadioGroup.getCheckedRadioButtonId() == R.id.stRadioButton){
            weightUnitType = WeightUnitType.ST;
            weight = Double.parseDouble(stEditText.getText().toString());
        }else{
            weightUnitType = WeightUnitType.KG;
            weight = Double.parseDouble(kgEditText.getText().toString());
        }

        String smokingHabit = smokingHabitSpinner.getSelectedItem().toString();

        User user = new User();
        user.setAge(age);
        user.setGenderType(genderType);
        user.setHeightUnitType(heightUnitType);
        user.setHeight(height);
        user.setWeightUnitType(weightUnitType);
        user.setWeight(weight);
        user.setSmokingHabit(smokingHabit);

        mUserDao.insert(user);
        loadMainActivity();
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void setFields(){
        User user = mUserQuery.list().get(0);

        ageEditText.setText(String.valueOf(user.getAge()));
        if(user.getGenderType().equals(GenderType.M)){
            genderRadioGroup.check(R.id.maleRadioButton);
        }else{
            genderRadioGroup.check(R.id.femaleRadioButton);
        }

        if(user.getHeightUnitType().equals(HeightUnitType.FT)){
            heightRadioGroup.check(R.id.ftRadioButton);
            ftEditText.setText(String.valueOf(user.getHeight()));
        }else{
            heightRadioGroup.check(R.id.cmRadioButton);
            cmEditText.setText(String.valueOf(user.getHeight()));
        }

        if(user.getWeightUnitType().equals(WeightUnitType.ST)){
            weightRadioGroup.check(R.id.stRadioButton);
            stEditText.setText(String.valueOf(user.getWeight()));
        }else{
            weightRadioGroup.check(R.id.kgRadioButton);
            kgEditText.setText(String.valueOf(user.getWeight()));
        }

        for(int i = 0; i < smokingHabitSpinner.getAdapter().getCount(); i++){
            if(smokingHabitSpinner.getItemAtPosition(i).equals(user.getSmokingHabit())){
                smokingHabitSpinner.setSelection(i);
                break;
            }
        }
    }


}
